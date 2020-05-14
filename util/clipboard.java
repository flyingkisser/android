package org.android.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.cocos2dx.javascript.AppActivity;

public class clipboard {
    static public void setString(final String text)
    {
        try
        {
            Activity mActivity= (Activity)AppActivity.getInstance();
            mActivity.runOnUiThread( new Runnable() {
                public void run() {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) AppActivity.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
                    clipboard.setPrimaryClip(clip);
                    UIUtil.Toast((Activity)AppActivity.getInstance(),text+" Copied To Clipboard!",1);
                }
            });
        }catch(Exception e){
            Log.d("cocos2d-x","copyToClipboard error");
            e.printStackTrace();
        }
    }
}
