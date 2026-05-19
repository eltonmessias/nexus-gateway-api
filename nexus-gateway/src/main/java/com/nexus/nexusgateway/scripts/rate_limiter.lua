-- scripts/rate_limiter.lua
-- Executa atomicamente no Redis — elimina condição de corrida
-- entre múltiplas instâncias do gateway

local key      = KEYS[1]           -- 'rl:{clientId}'
local now      = tonumber(ARGV[1]) -- timestamp em ms
local window   = tonumber(ARGV[2]) -- janela em ms (ex: 60000)
local limit    = tonumber(ARGV[3]) -- requests permitidos

-- Remove entradas fora da janela deslizante
redis.call('ZREMRANGEBYSCORE', key, '-inf', now - window)

-- Conta requests na janela actual
local count = redis.call('ZCARD', key)

if count < limit then
-- Adiciona o request actual (score e member = timestamp)
	redis.call('ZADD', key, now, now .. '-' .. math.random(1000000))
	redis.call('EXPIRE', key, math.ceil(window / 1000))
	return {1, limit - count - 1}  -- {permitido, restantes}
end

return {0, 0}  -- {bloqueado, restantes}