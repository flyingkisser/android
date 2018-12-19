package org.android.util;
import android.app.Activity;

import org.cocos2dx.javascript.AppActivity;
public class PkgUtil {
    public static String getPkgName(){
        Activity mActivity= (Activity)AppActivity.getInstance();
        return  mActivity.getApplicationContext().getPackageName();
    }
}
