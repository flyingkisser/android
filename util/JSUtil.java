package org.android.util;

import org.cocos2dx.javascript.AppActivity;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

public class JSUtil {
    public static String _s;
    public static void eval(Cocos2dxActivity activity,String jsString){
        _s=jsString;
        //Cocos2dxActivity app=(Cocos2dxActivity) AppActivity.getInstance();
        activity.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                int ret= Cocos2dxJavascriptJavaBridge.evalString(_s);
                LogUtil.d("java call js %d",ret);
            }
        });
    }
}
