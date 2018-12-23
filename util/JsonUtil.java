package org.android.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joe on 16/5/18.
 */
public class JsonUtil {
    private static ObjectMapper json = new ObjectMapper();
    //private static Log log=frameworks.utils.Log.getInstance();

    public static HashMap<String,Object> decodeToHashMap(File stream){
        try{
            return (HashMap<String,Object>)json.readValue(stream, HashMap.class);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public static HashMap<String,Object> decodeToHashMap(String str){
        try{
            return (HashMap<String,Object>)json.readValue(str, HashMap.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String,String> decodeToHashMapStringString(String str){
        try{
            return (HashMap<String,String>)json.readValue(str, HashMap.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String,Integer> decodeToHashMapStringInt(String str){
        try{
            return (HashMap<String,Integer>)json.readValue(str, HashMap.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String,Object> decodeToHashMapStringObject(String str){
        try{
            return (HashMap<String,Object>)json.readValue(str, HashMap.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<Integer,Integer> decodeToHashMapIntInt(String str){
        try{
            HashMap<String,Integer> obj=(HashMap<String,Integer>)json.readValue(str, HashMap.class);
            HashMap<Integer, Integer> retHash=new HashMap<Integer, Integer>();
            for (String k : obj.keySet()) {
                int key=Integer.parseInt(k);
                retHash.put(key, obj.get(k));
            }

            return retHash;
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<Integer,Object> decodeToHashMapIntObject(String str){
        try{
            HashMap<String,Object> obj=(HashMap<String,Object>)json.readValue(str, HashMap.class);
            HashMap<Integer, Object> retHash=new HashMap<Integer, Object>();
            for (String k : obj.keySet()) {
                int key=Integer.parseInt(k);
                retHash.put(key, obj.get(k));
            }

            return retHash;
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<?> decodeToArrayList(String str){
        try{
            return json.readValue(str, ArrayList.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Integer> decodeToArrayListInt(String str){
        try{
            return (ArrayList<Integer>)json.readValue(str, ArrayList.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Long> decodeToArrayListLong(String str){
        try{
            return (ArrayList<Long>)json.readValue(str, ArrayList.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<ArrayList<Integer>> decodeToArrayListArrayListInt(String str){
        try{
            return (ArrayList<ArrayList<Integer>>)json.readValue(str, ArrayList.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<?> decodeToArrayList(File stream){
        try{
            return json.readValue(stream, ArrayList.class);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }



    public static Object decodeToObject(String str){
        try{
            if(str==null)
                return null;
            return json.readValue(str, Object.class);
        }
        catch(Exception e)
        {
            LogUtil.e("give str is %s",str);
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
            e.printStackTrace();
        }
        return null;
    }

    public static String encode(Object obj){
        try {
            if(obj==null)
                return "";
            return json.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
        }
        return "";
    }
}
