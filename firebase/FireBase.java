package org.android.firebase;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.android.util.JSUtil;
import org.cocos2dx.javascript.AppActivity;
import org.cocos2dx.lib.Cocos2dxActivity;

public class FireBase {
    public static String TAG="Firebase";
    public static String token="";
    public static String mStrJSCB="";
    public static void Enable(Boolean v){
        ((AppActivity) AppActivity.getInstance()).getFirebase().setAnalyticsCollectionEnabled(v);
    }
    public static void CheckPoint(String id,String name){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CHECKOUT_STEP, id);
        bundle.putString(FirebaseAnalytics.Param.CHECKOUT_OPTION, name);
        ((AppActivity) AppActivity.getInstance()).getFirebase().logEvent(
                FirebaseAnalytics.Event.CHECKOUT_PROGRESS,bundle);
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