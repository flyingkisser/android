package org.android.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by joe on 16/5/4.
 * Revised on 20/5/8
 */
public class UIUtil {

    //必须事先在assets下创建一fonts文件夹 并放入要使用的字体文件(.ttf)
    //t             TextView对象
    //fontFileName  字体文件名，如"fonts/STXINGKA.TTF"
    public static void setFontTTF(Activity activity, TextView t, String fontFileName){

        Typeface fontFace = Typeface.createFromAsset(activity.getAssets(),fontFileName);
        t.setTypeface(fontFace);
    }

    public static void setFontTTF(Activity activity, EditText o, String fontFileName){
        Typeface fontFace = Typeface.createFromAsset(activity.getAssets(),fontFileName);
        o.setTypeface(fontFace);
    }

    public static void Toast(Activity activity,String str,int isLong){
        int sec=Toast.LENGTH_SHORT;
        if(isLong>0)
            sec=Toast.LENGTH_LONG;
        //其它的时长的值，并不会起作用!
        Toast toast=Toast.makeText(activity,str,sec);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
