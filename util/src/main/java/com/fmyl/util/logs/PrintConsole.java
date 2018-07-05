package com.fmyl.util.logs;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Description:
 * @Author: fmyl
 * @Date: 2018/7/5
 */
public class PrintConsole {

    /**
     * @Description:
     * @Param: [log]
     * @return: void
     * @Author: fmyl
     * @Date: 2018/7/5
     */
    public static void out(Object log) {
        if (log instanceof List) {
            listLog((List) log);
            return;
        }
        if (log instanceof Enumeration) {
            enumLog((Enumeration) log);
            return;
        }
        if (log instanceof InputStream) {
            inputStreamLog((InputStream) log);
            return;
        }
        if (log instanceof Map) {
            mapLog((Map) log, 1);
            return;
        }
        System.out.println(log);
    }

    /**
     * List value log
     */
    private static void listLog(List l) {
        if (l == null) {
            return;
        }
        out(l.getClass().getName() + " : ");
        for (int i = 0; i < l.size(); i++) {
            out("        " + i + " : " + l.get(i));
        }
    }

    /**
     * Enumeration value log
     */
    private static void enumLog(Enumeration e) {
        if (e == null) {
            return;
        }
        out(e.getClass().getName() + " :");
        int i = 0;
        while (e.hasMoreElements()) {
            out("        " + i++ + " : " + e.nextElement().toString());
        }
    }

    private static void inputStreamLog(InputStream in) {
        if (in == null) {
            out("inputStream is null!!");
            return;
        }
        int len = 0;
        byte[] buffer = new byte[128];
        out(in + " : /r/n");
        try {
            while ((len = in.read(buffer)) != -1) {
                out(in.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Map key value log
     *
     * @param map
     * @param m   [1]   method 1:
     *            map.keySet()
     *            map.get(key)
     *            [2]  method 2:
     *            Iterator iter = map.entrySet().iterator();
     *            while(iter.hasNext()){.....}
     *            [3]  method 3:
     *            for(Map.Entry entry:map.entrySet()){......}
     *            [4]  method 4:
     *            Iterator iter2 = map.values().iterator();
     *            while(iter2.hasNext()){....}
     */
    private static void mapLog(Map map, Integer m) {
        if (m == null) {
            m = 0;
        }
        if (map == null) {
            return;
        }
        switch (m) {
            //方法一
            case 0:
                out("method one");
                Set<String> set = map.keySet();
                for (String key : set) {
                    out("key : " + key + "; value : " + map.get(key));
                }
                break;
            //方法二
            case 1:
                out("method two");
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry obj = (Map.Entry) iter.next();
                    out("key : " + obj.getKey() + "; value : " + obj.getValue());
                }
                break;
            //方法三
            case 2:
                out("method three");
                Set<Map.Entry> entrySet = map.entrySet();
                for (Map.Entry entry : entrySet) {
                    out("key : " + entry.getKey() + "; value : " + entry.getValue());
                }
                break;
            //方法四
            case 3:
                out("method four");
                Iterator iter2 = map.values().iterator();
                int i = 0;
                while (iter2.hasNext()) {
                    out(i++ + " ; value : " + iter2.next());
                }
                break;
        }
    }

    //test
    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("s1", 1);
        map.put("s3", 3);
        map.put("s2", 2);
        PrintConsole.mapLog(map, 0);
        PrintConsole.mapLog(map, 1);
        PrintConsole.mapLog(map, 2);
        PrintConsole.mapLog(map, 3);
    }
}
