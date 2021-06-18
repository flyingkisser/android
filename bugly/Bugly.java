package org.android.bugly;

import android.app.Activity;

import com.tencent.bugly.crashreport.CrashReport;

public class Bugly {
    public static Activity mActivity;

    public static void Init(Activity a,String apiKey,boolean bDebug){
        CrashReport.initCrashReport(a.getApplicationContext(),apiKey,bDebug);
        mActivity=a;
    }

    public static void setUserId(String id){
        CrashReport.setUserId(id);
    }

    public static void setKV(String key,String value){
        if(key==null || value==null)
            return;
        CrashReport.putUserData(mActivity.getApplicationContext(), key, value);
    }

    public static void setSceneId(int id){
        CrashReport.setUserSceneTag(mActivity.getApplicationContext(),id);
    }

    public static void testCrash(){
        CrashReport.testJavaCrash();
    }
}
