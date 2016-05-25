package org.android.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joe on 16/5/18.
 */
//适用于策划类数据，如
//每行是一个英雄的描述数据
//array(
//1=>array("k1"=>value1,"k2"=>value2,...),
//2=>array("k1"=>value1,"k2"=>value2,...),
//...
//n=>array("k1"=>value1,"k2"=>value2,...),
//)
public class DataListConfig {
    private Config s_coredata;
    ArrayList<Integer> m_idList=null;
    HashMap<String, Object> m_transHash=new HashMap<String, Object>();

    public DataListConfig(String ConfigFileName){
        s_coredata=new Config(ConfigFileName);
    }

    //把key强转成int返回
    public ArrayList<Integer> getIDListAsInt(){
        HashMap<String, Object> objHashMap= s_coredata.dump();
        if(m_idList!=null)
            return m_idList;
        m_idList=new ArrayList<>();
        for (String key :objHashMap.keySet()) {
            try {
                int id=Integer.parseInt(key);
                if(id!=0)
                    m_idList.add(id);
            } catch (Exception e) {
                continue;
            }
        }
        return m_idList;
    }

    public  String getValueString(int id,String key){
        HashMap<String, Object> obj=s_coredata.getHashMap(id);
        if(obj==null)
        {
            return null;
        }
        Object ret=obj.get(key);
        if(ret==null)
            return null;
        return ret.toString();
    }

    public int getValueInt(int id,String key){
        HashMap<String, Object> obj=s_coredata.getHashMap(id);
        if(obj==null)
        {
            return 0;
        }
        Object ret=obj.get(key);
        if(ret==null)
            return 0;
        return Integer.parseInt(ret.toString());
    }
    public float getValueFloat(int id,String key){
        HashMap<String, Object> obj=s_coredata.getHashMap(id);
        if(obj==null)
        {
            return 0;
        }
        Object ret=obj.get(key);
        if(ret==null)
            return 0;
        return Float.parseFloat(ret.toString());
    }

    public int getValueInt(String key1,String key2){
        HashMap<String, Object> obj=s_coredata.getHashMap(key1);
        if(obj==null)
        {
            return 0;
        }
        Object ret=obj.get(key2);
        if(ret==null)
            return 0;
        return Integer.parseInt(ret.toString());
    }

    public int getValueInt(String key){
        return (int) s_coredata.getObject(key);
    }

    public ArrayList<Integer> getArrayListInt(int id,String key){
        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null)
        {
            return null;
        }
        Object ret=obj.get(key);
        if(ret==null)
            return new ArrayList<Integer>();
        return (ArrayList<Integer>)ret;
    }

    public ArrayList<Integer> getValueArrayListInt(String key){
        return (ArrayList<Integer>) s_coredata.getObject(key);
    }

    //public ArrayList<Float> getValueArrayListFloat(String key){
    //return (ArrayList<Float>) s_coredata.getObject(key);
    //}

    public ArrayList<Float> getValueArrayListFloat(String key){
        ArrayList<Double> valueList=(ArrayList<Double>) s_coredata.getObject(key);
        ArrayList<Float> retList=new ArrayList<>();
        for (double v:valueList) {
            float f=(float )v;
            retList.add(f);
        }
        return retList;
    }

    public HashMap<Integer, Integer> convertFromStringIntToIntInt(HashMap<String, Integer> hash){
        HashMap<Integer, Integer> retHash=new HashMap<>();
        for (String key : hash.keySet()) {
            retHash.put(Integer.parseInt(key), hash.get(key));
        }
        return retHash;
    }

    public HashMap<String,Object> getValueHashMap(String key1,String key2){
        HashMap<String,Object> obj= s_coredata.getHashMap(key1);
        if(obj==null)
        {
            return new HashMap<String, Object>();
        }
        return (HashMap<String,Object>)obj.get(key2);
    }

    public ArrayList<String> getArrayListString(int id,String key){
        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null)
        {
            return new ArrayList<String>();
        }
        Object ret=obj.get(key);
        return (ArrayList<String>)ret;
    }

    public ArrayList<ArrayList<Integer>> getArrayListArrayList(int id){
        Object obj= s_coredata.getObject(id);
        if(obj==null)
        {
            return new ArrayList<ArrayList<Integer>>();
        }
        return (ArrayList<ArrayList<Integer>>)obj;
    }

    public ArrayList<HashMap<String,Object>> getArrayListHashMap(String key){
        Object obj= s_coredata.getObject(key);
        if(obj==null)
        {
            return new ArrayList<HashMap<String,Object>>();
        }
        return (ArrayList<HashMap<String,Object>>)obj;
    }

    public HashMap<String, Object> getHashMapStringObject(int id){
        Object obj= s_coredata.getObject(String.valueOf(id));
        if(obj==null)
        {
            return new HashMap<String, Object>();
        }
        return (HashMap<String,Object>)obj;
    }


    public HashMap<Integer,Float> getHashMapIntFloat(int id,String key){
        String keyInMem=String.format("%d-%s", id,key);
        if(m_transHash.containsKey(keyInMem))
            return (HashMap<Integer,Float>)m_transHash.get(keyInMem);

        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null)
            return new HashMap<Integer, Float>();

        HashMap<Integer,Float> newHash=new HashMap<Integer, Float>();
        HashMap<String,Object> ret=(HashMap<String,Object>)obj.get(key);
        if(ret==null)
            return new HashMap<Integer, Float>();
        for (String k : ret.keySet()) {
            int newKey=Integer.parseInt(k);
            float newValue=Float.parseFloat(ret.get(k).toString());
            newHash.put(newKey, newValue);
        }
        m_transHash.put(keyInMem, newHash);
        return newHash;
    }

    public HashMap<Integer,Integer> getHashMapIntInt(int id,String key){
        String keyInMem=String.format("%d-%s", id,key);
        if(m_transHash.containsKey(keyInMem))
            return (HashMap<Integer,Integer>)m_transHash.get(keyInMem);

        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null)
            return new HashMap<Integer, Integer>();

        HashMap<Integer,Integer> newHash=new HashMap<Integer, Integer>();
        HashMap<String,Object> ret=(HashMap<String,Object>)obj.get(key);
        if(ret==null)
            return new HashMap<Integer, Integer>();
        for (String k : ret.keySet()) {
            int newKey=Integer.parseInt(k);
            int newValue=Integer.parseInt(ret.get(k).toString());
            newHash.put(newKey, newValue);
        }
        m_transHash.put(keyInMem, newHash);
        return newHash;
    }

    public HashMap<String,Integer> getHashMapStringInt(int id,String key){
        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null || obj.get(key)==null)
        {
            return new HashMap<String,Integer>();
        }
        Object ret=obj.get(key);
        return (HashMap<String,Integer>)ret;
    }

    public String getHashMapString(int id,String key){
        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null || obj.get(key)==null)
        {
            return "[]";
        }
        Object ret=obj.get(key);
        return JsonUtil.encode(ret);
    }

    public HashMap<String,Object> getHashMapStringObject(int id,String key){
        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null || obj.get(key)==null)
        {
            return null;
        }
        Object ret=obj.get(key);
        return (HashMap<String,Object>)ret;
    }

    public boolean idIsExist(int id){
        HashMap<String,Object> obj= s_coredata.getHashMap(id);
        if(obj==null){
            return false;
        }
        return true;
    }
}
