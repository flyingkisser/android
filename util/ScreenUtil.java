package org.android.util;

/**
 * Created by joe on 16/4/29.
 */

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

public class ScreenUtil {
    public static void printResolution(Activity activity){
        String str = "";
        DisplayMetrics dm = new DisplayMetrics();
        dm = activity.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float density = dm.density;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        str += "The absolute width:" + String.valueOf(screenWidth) + "pixels\n";
        str += "The absolute heightin:" + String.valueOf(screenHeight)
                + "pixels\n";
        str += "The logical density of the display.:" + String.valueOf(density)
                + "\n";
        str += "X dimension :" + String.valueOf(xdpi) + "pixels per inch\n";
        str += "Y dimension :" + String.valueOf(ydpi) + "pixels per inch\n";
        Log.d("system",str);
        //return str;
    }
}
