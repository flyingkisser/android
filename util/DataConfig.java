package org.android.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joe on 16/5/18.
 */
//适用于key=>value类型的结构
//array("k1"=>value1,"k2"=>value2,...),
public class DataConfig {
    private Config s_coredata;

    public DataConfig(String ConfigFileName){
        s_coredata=new Config(ConfigFileName);
    }

    public int getCount(){
        return s_coredata.getCount();
    }

    public  String getValueString(String key){
        return s_coredata.getString(key);
    }
    public  int getValueInt(String key){
        return s_coredata.getInt(key);
    }

    public ArrayList<Integer> getKeysAsInt(){
        return s_coredata.getKeysAsInt();
    }

    public ArrayList<Integer> getArrayListInt(String key){
        return  (ArrayList<Integer>) s_coredata.getObject(key);
    }

    public ArrayList<Double> getArrayListDouble(String key){
        return  (ArrayList<Double>) s_coredata.getObject(key);
    }

    //new
    public ArrayList<ArrayList<ArrayList<Integer>>> getArrayList3Int(String key){
        return  (ArrayList<ArrayList<ArrayList<Integer>>>) s_coredata.getObject(key);
    }

    public ArrayList<HashMap<String, Object>> getArrayListHashMap(String key){
        return  (ArrayList<HashMap<String, Object>>) s_coredata.getObject(key);
    }
    public ArrayList<String> getArrayListString(String key){
        return  (ArrayList<String>) s_coredata.getObject(key);
    }

    public HashMap<String, Object>getHashMap(String key){
        return  (HashMap<String,Object>) s_coredata.getObject(key);
    }

    public HashMap<String, Object>dump(){
        return  s_coredata.dump();
    }
}
