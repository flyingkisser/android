package org.android.util;

import java.util.HashMap;

/**
 * Created by joe on 16/5/18.
 */
public class HashMapUtil {
    public static int getFirstKey(HashMap<Integer, Integer> value)
    {
        int ret=0;
        for (int i:value.keySet()) {
            ret=i;
            break;
        }
        return ret;
    }

    public  static HashMap<Integer, Object> StrObj2IntObj ( HashMap<String, Object> valueHash){
        HashMap<Integer, Object> retHash=new HashMap<Integer, Object>();
        for (String k : valueHash.keySet()) {
            int key=Integer.parseInt(k);
            retHash.put(key, valueHash.get(k));
        }
        return retHash;
    }

    public static HashMap<Integer, Integer> StrObj2IntInt ( HashMap<String, Object> valueHash){
        HashMap<Integer, Integer> retHash=new HashMap<>();
        for (String k : valueHash.keySet()) {
            int key=Integer.parseInt(k);
            retHash.put(key, Integer.parseInt(  valueHash.get(k).toString()));
        }
        return retHash;
    }

    //把两个hashmap的结果相加，结果体现到h1中
    public static void addHash(HashMap<Integer, Integer> h1,HashMap<Integer, Integer>h2){
        if(h2==null || h1==null)
            return;
        for (int k : h2.keySet()) {
            if(h1.containsKey(k))
                h1.put(k, h1.get(k)+h2.get(k));
            else
                h1.put(k, h2.get(k));
        }
    }
}
