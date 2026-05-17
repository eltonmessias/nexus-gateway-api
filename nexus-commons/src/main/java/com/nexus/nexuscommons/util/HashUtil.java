package com.nexus.nexuscommons.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public final class HashUtil {
    private HashUtil() {}

    /**
     * MurmurHash3 — usado pelo RolloutEvaluator para distribuição uniforme.
     * Garante que o mesmo userId recebe sempre o mesmo bucket (0-99).
     */
    public static int murmurBucket(String value) {
        int hash = Hashing.murmur3_32_fixed()
                .hashString(value, StandardCharsets.UTF_8)
                .asInt();
        return Math.abs(hash % 100);
    }

    /**
     * SHA-256 para hashing de API keys e tokens sensíveis.
     */
    public static String sha256(String value) {
        return Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
    }

}
