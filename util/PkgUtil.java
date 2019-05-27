package org.android.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import org.cocos2dx.javascript.AppActivity;
public class PkgUtil {
    public static String getPkgName(){
        Activity mActivity= (Activity)AppActivity.getInstance();
        return  mActivity.getApplicationContext().getPackageName();
    }

    public static void goToMarket(String packageName) {
        Activity mActivity= (Activity)AppActivity.getInstance();
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goMarketIntent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            mActivity.getApplicationContext().startActivity(goMarketIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void goToMarketList(){
        Activity mActivity= (Activity)AppActivity.getInstance();
        Intent intent=new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mActivity.getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
