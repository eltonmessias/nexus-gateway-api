package com.nexus.nexuscachelayer.util;

public class CacheKeyUtil {

    private CacheKeyUtil() {}

    public static String forUser(String userId){
        return "user:" + userId;
    }

    public static String forFlag(String flag){
        return "flag:" + flag;
    }

    public static String forJob(String jobId){
        return "job:" + jobId;
    }
}
