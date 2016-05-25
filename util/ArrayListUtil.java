package org.android.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joe on 16/5/18.
 */
public class ArrayListUtil {
    public static String formatStr(ArrayList<Integer> idList){
        if(idList.size()<=0)
            return "";
        String retStr="";
        for (int id : idList) {
            retStr+=String.format("%d,", id);
        }
        return retStr.substring(0, retStr.length()-1);
    }

    public static void setKeyValueAll(ArrayList<HashMap<String,Object>> valueList, String key, int v){
        int index=0;
        for (HashMap<String, Object> valueMap : valueList) {
            valueMap.put(key, v);
            valueList.set(index++, valueMap);
        }
    }
}
