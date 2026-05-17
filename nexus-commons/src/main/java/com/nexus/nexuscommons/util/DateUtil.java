package com.nexus.nexuscommons.util;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss'Z'")
                    .withZone(ZoneId.of("UTC"));

    private DateUtil() {}

    public static String format(Instant instant) {
        return FORMATTER.format(instant);
    }

    public static boolean isExpired(Instant expiredAt) {
        return Instant.now().isAfter(expiredAt);
    }
}
