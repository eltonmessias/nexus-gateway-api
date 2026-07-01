-- KEYS[1] = rate limit key (ex: "rl:nexus_abc123")
-- ARGV[1] = window size in milliseconds (60000 = 1 minute)
-- ARGV[2] = max requests allowed in window (rateLimitRpm)
-- ARGV[3] = current timestamp in milliseconds
-- Returns: {allowed (0/1), current_count, retry_after_ms}

local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local window_start = now - window

-- Remove entries outside the sliding window
redis.call("ZREMRANGEBYSCORE", key, 0, window_start)

-- Count current requests in window
local count = redis.call("ZCARD", key)

if count >= limit then
    -- Get oldest entry to calculate retry-after
    local oldest = redis.call("ZRANGE", key, 0, 0, "WITHSCORES")
    local retry_after = 0
    if oldest and oldest[2] then
        retry_after = window - (now - tonumber(oldest[2]))
    end
    return {0, count, retry_after}
end

-- Add current request with timestamp as score
redis.call("ZADD", key, now, now .. "-" .. math.random(1, 1000000))
redis.call("PEXPIRE", key, window)

return {1, count + 1, 0}
