package com.multipong.utility;

import java.util.HashMap;
import java.util.Map;

public class TimeLoggingUtility {
    private static Map<String, Long> logs = new HashMap<>();

    public static synchronized void setStartFor(String what) {
        logs.put(what, System.currentTimeMillis());
    }

    public static synchronized Long getElapsedTimeFor(String what) {
        if(!logs.containsKey(what)) return 0L;
        Long start = logs.get(what);
        return System.currentTimeMillis() - start;
    }
}
