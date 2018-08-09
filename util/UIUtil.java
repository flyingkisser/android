package org.android.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by joe on 16/5/4.
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

    public static void Toast(Activity activity,String s,int ms){
        if(ms==0)
            ms=Toast.LENGTH_SHORT;
        Toast.makeText(activity,s,ms).show();
    }
}
