package org.android.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joe on 16/5/18.
 */
public class Config {
    private HashMap<String,Object> jsonHashMap;
    private boolean bCast;
    private ArrayList<Object> keyList=null;
    private ArrayList<Integer> keyListAsInt=null;
    public Config(String strConfigFileName){
        try {

            jsonHashMap=JsonUtil.decodeToHashMap(new File(strConfigFileName));
            bCast=false;
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
        }
    }

    public HashMap<String, Object> dump(){
        return jsonHashMap;
    }

    public int getCount(){
        return jsonHashMap.size();
    }

    public ArrayList<Integer> getKeysAsInt(){
        if(keyList==null)
        {
            Object keys[]=jsonHashMap.keySet().toArray();
            keyList=new ArrayList<Object>();
            for (Object object : keys) {
                keyList.add(object);
            }
        }
        if(keyListAsInt==null)
        {
            keyListAsInt=new ArrayList<Integer>();

            for(Object obj:keyList){
                try{
                    int id=Integer.parseInt(obj.toString());
                    keyListAsInt.add(id);
                }
                catch(Exception e){
                    continue;
                }
            }

        }
        return keyListAsInt;
    }

    public String getString(String key){
        return  jsonHashMap.get(key).toString();
    }

    public int getInt(String key){
        return Integer.parseInt(jsonHashMap.get(key).toString());
    }

    public Object getObject(int id){
        return getObject(String.valueOf(id));
    }

    public Object getObject(String key){
        return jsonHashMap.get(key);
    }

    public HashMap<String, Object> getHashMap(int key){
        return getHashMap(String.valueOf(key));
    }

    public HashMap<String, Object> getHashMap(String key){
        if(bCast)
            return getHashMap1(key);
        else
            return getHashMap2(key);
    }

    public HashMap<String, Object> getHashMap1(String key){

        HashMap<String, Object> outputHashMap=new HashMap<String,Object>();
        HashMap<?, ?> ret= (HashMap<?, ?>)jsonHashMap.get(key);
        for(Object k : ret.keySet().toArray()){
            outputHashMap.put(k.toString(), ret.get(k));
        }
        return outputHashMap;
    }

    public HashMap<String, Object> getHashMap2(String key){

        HashMap<String, Object> ret= (HashMap<String, Object>)jsonHashMap.get(key);

        return (HashMap<String, Object>)ret;
    }

    public static void test(){
        Config obj=new Config("config/HeroConfig.json");
        ArrayList<Integer> heroidlist=obj.getKeysAsInt();
        LogUtil.e("hero id list %s", JsonUtil.encode(heroidlist));
    }
}
