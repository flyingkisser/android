package org.android.googlepay;

import android.app.Activity;

public class Util {
    public static Activity mActivity;
    public static boolean mInit;

    public static void init(Activity a){
        mActivity=a;
        mInit=false;
    }


}
