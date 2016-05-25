package org.android.util;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by joe on 16/5/18.
 */
public class ForbidWordUtil {
    private ArrayList<String> m_wordList=null;

    public ForbidWordUtil(String strConfigFileName)
    {
        try {
            m_wordList=(ArrayList<String>)JsonUtil.decodeToArrayList(new File(strConfigFileName));
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
        }
    }

    public boolean check(String v){
        if(m_wordList.contains(v))
            return true;

        for(String s : m_wordList){
            if(v.contains(s))
                return true;
        }

        return false;
    }

}
