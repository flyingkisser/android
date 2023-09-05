package org.android.firebase;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
// import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.android.util.JSUtil;
import org.cocos2dx.javascript.AppActivity;
import org.cocos2dx.lib.Cocos2dxActivity;

public class FireBase {
    public static String TAG="Firebase";
    public static String token="";
    public static String mStrJSCB="";
    public static void Enable(int v){
        // ((AppActivity) AppActivity.getInstance()).getFirebaseAnalytics().setAnalyticsCollectionEnabled(v>0);
    }
    public static void logKV(String k,String v){

        // Bundle params = new Bundle();
        // params.putString("value", v);
        // ((AppActivity) AppActivity.getInstance()).getFirebaseAnalytics().logEvent(k, params);

    }

    public static void logConvention(String value){
        // Bundle params = new Bundle();
        // params.putString(FirebaseAnalytics.Param.LEVEL_NAME, value);
        // ((AppActivity) AppActivity.getInstance()).getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.LEVEL_START, params);
    }

    public static void logRevenue(String productName,String skuName,int number,float price,String currency,String transactionID,String jsonStr){
//         Bundle item = new Bundle();
//         item.putString(FirebaseAnalytics.Param.ITEM_ID, skuName);
//         item.putString(FirebaseAnalytics.Param.ITEM_NAME, productName);
// //        item.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "pants");
// //        item.putString(FirebaseAnalytics.Param.ITEM_VARIANT, "black");
//         item.putString(FirebaseAnalytics.Param.ITEM_BRAND, "Google");
//         item.putDouble(FirebaseAnalytics.Param.PRICE, price);

//         Bundle purchaseParams = new Bundle();
//         purchaseParams.putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionID);
//         purchaseParams.putString(FirebaseAnalytics.Param.AFFILIATION, "Google Store");
//         purchaseParams.putString(FirebaseAnalytics.Param.CURRENCY, currency);
//         purchaseParams.putDouble(FirebaseAnalytics.Param.VALUE, price);
//         purchaseParams.putDouble(FirebaseAnalytics.Param.TAX, 0);
//         purchaseParams.putDouble(FirebaseAnalytics.Param.SHIPPING, 0);
//         purchaseParams.putString(FirebaseAnalytics.Param.COUPON, "SUMMER_FUN");
// //        purchaseParams.putParcelableArray(FirebaseAnalytics.Param.ITEM, new Parcelable[]{ item });
// //        ((AppActivity) AppActivity.getInstance()).getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.PURCHASE, purchaseParams);
//         purchaseParams.putParcelableArray("item", new Parcelable[]{ item });
//         ((AppActivity) AppActivity.getInstance()).getFirebaseAnalytics().logEvent("purchase", purchaseParams);
    }

    public static void setUserId(String id){
        // ((AppActivity) AppActivity.getInstance()).getFirebaseAnalytics().setUserId(id);
    }
    public static void setUserKV(String k,String v){
        // ((AppActivity) AppActivity.getInstance()).getFirebaseAnalytics().setUserProperty(k,v);
    }

    public static String GetRemoteConfigValue(String key){
        return "";
        // return ((AppActivity) AppActivity.getInstance()).getFirebaseRemoteConfig().getString(key);
    }

    public static  void getPushToken(String strJSCB){
        mStrJSCB=strJSCB;
        if(token!="" && mStrJSCB!=""){
            JSUtil.eval((Cocos2dxActivity)AppActivity.getInstance(),String.format(mStrJSCB,token));
            return;
        }
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getPushToken:getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, "get push token ok:"+token);
                        //Toast.makeText(AppActivity.getInstance(), token, Toast.LENGTH_SHORT).show();

                        if(mStrJSCB!=null && !mStrJSCB.isEmpty()){
                            Log.d(TAG, "FireBaseJSB.getToken: call mStrJSCB");
                            JSUtil.eval((Cocos2dxActivity)AppActivity.getInstance(),String.format(mStrJSCB,token));
                        }
                    }
                });
    }
}