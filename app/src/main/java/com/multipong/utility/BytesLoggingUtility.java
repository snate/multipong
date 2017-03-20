package com.multipong.utility;

import java.util.HashMap;
import java.util.Map;

public class BytesLoggingUtility {
    private static Map<String, Integer> logs = new HashMap<>();

    public static synchronized void addLogsFor(String what, Integer howMany) {
        if(!logs.containsKey(what))
            logs.put(what, howMany);
        Integer oldValue = logs.get(what);
        logs.put(what, oldValue + howMany);
    }

    public static synchronized Integer getLogsFor(String what) {
        if(!logs.containsKey(what)) return 0;
        else                        return logs.get(what);
    }
}
