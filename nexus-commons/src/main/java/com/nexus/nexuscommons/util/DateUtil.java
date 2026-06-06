package com.nexus.nexuscommons.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private DateUtil() {}

    public static Instant now(){
        return Instant.now();
    }

    public static String format(Instant instant) {
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }
}
