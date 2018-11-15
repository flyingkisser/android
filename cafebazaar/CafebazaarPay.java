package org.android.cafebazaar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.vending.billing.*;

import org.android.util.JSUtil;
import org.android.util.UIUtil;
import org.cocos2dx.lib.Cocos2dxActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class CafebazaarPay {
    public String TAG;
    public Activity mActivity;
    public String mStrGetUnCheckedCountJSCB;
    public String mStrBuyJSCB;
    public String mStrConsumeJSCB;

    private static int ERROR_INIT_NOT_FINISHED =1;
//    private static int ERROR_INIT_ERROR =2;
    private static int ERROR_BUY_EXCEPTION =3;
//    private int mInitReturnCodeFromBillingMgr=-1;
    public HashMap mBillingResponseError=new HashMap<Integer,String>();
    public HashMap mBillingResponseErrorDetail=new HashMap<Integer,String>();

    IInAppBillingService mService;
    ServiceConnection mServiceConn;

    public ServiceConnection getBillingServiceConnection(){
        return mServiceConn;
    }

    public CafebazaarPay(Activity a, String tag){
        if(tag.isEmpty())
            TAG=a.getPackageName()+" CafebazaarPay";
        else
            TAG=tag+" CafebazaarPay";
        mActivity=a;
        mBillingResponseError.put(-2,"FEATURE_NOT_SUPPORTED");
        mBillingResponseError.put(-1,"SERVICE_DISCONNECTED");
        mBillingResponseError.put(0,"OK");
        mBillingResponseError.put(1,"USER_CANCELED");
        mBillingResponseError.put(2,"SERVICE_UNAVAILABLE");
        mBillingResponseError.put(3,"BILLING_UNAVAILABLE");
        mBillingResponseError.put(4,"ITEM_UNAVAILABLE");
        mBillingResponseError.put(5,"DEVELOPER_ERROR");
        mBillingResponseError.put(6,"ERROR");
        mBillingResponseError.put(7,"ITEM_ALREADY_OWNED");
        mBillingResponseError.put(8,"ITEM_NOT_OWNED");

        mBillingResponseErrorDetail.put(-2,"Requested feature is not supported by Play Store on the current device");
        mBillingResponseErrorDetail.put(-1,"Play Store service is not connected now - potentially transient state");
        mBillingResponseErrorDetail.put(0,"Success");
        mBillingResponseErrorDetail.put(1,"User pressed back or canceled a dialog");
        mBillingResponseErrorDetail.put(2,"Network connection is down");
        mBillingResponseErrorDetail.put(3,"Billing API version is not supported for the type requested");
        mBillingResponseErrorDetail.put(4,"Requested product is not available for purchase");
        mBillingResponseErrorDetail.put(5,"Invalid arguments provided to the API. This error can also indicate that the application was not correctly signed or properly set up for In-app Billing in Google Play, or does not have the necessary permissions in its manifest");
        mBillingResponseErrorDetail.put(6,"Fatal error during the API action,please satisfy all the rights needed by the game and google play and google services");
        mBillingResponseErrorDetail.put(7,"Failure to purchase since item is already owned");
        mBillingResponseErrorDetail.put(8,"Failure to consume since item is not owned");

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
            @Override
            public void onServiceConnected(ComponentName name,IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Log.d(TAG, "onBindingDied");
                mService = null;
            }
        };

        Intent intent=new Intent("ir.cafebazaar.pardakht.InAppBillingService.BIND");
        //intent.setPackage(mActivity.getPackageName());
        intent.setPackage("com.farsitel.bazaar");
        a.bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public boolean isServiceAvailable(){
        return  mService!=null;
    }

    public int BuyItem(String skuName,String jsCallBack){
        Log.d(TAG, "BuyItem:begin "+skuName);
        if(mService==null){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,"cafeBazaar init not finished yet",0);
                }
            });
            Log.d(TAG, "BuyItem:cafeBazaar init not finished yet");
            return ERROR_INIT_NOT_FINISHED;
        }
        mStrBuyJSCB=jsCallBack;
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, mActivity.getPackageName(),
                    skuName, "inapp", "sendBackStr");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0));
        }catch (Exception e){
            Log.d(TAG,"call startIntentSenderForResult exception "+e);
            return  ERROR_BUY_EXCEPTION;
        }
        return  0;
    }

    public int BuySubscribe(String skuName,String jsCallBack){
        Log.d(TAG, "BuySubscribe:begin "+skuName);
        if(mService==null){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,"cafeBazaar init not finished yet",0);
                }
            });
            Log.d(TAG, "BuyItem:cafeBazaar init not finished yet");
            return ERROR_INIT_NOT_FINISHED;
        }
        mStrBuyJSCB=jsCallBack;
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, mActivity.getPackageName(),
                    skuName, "subs", "sendBackStr");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0));
        }catch (Exception e){
            Log.d(TAG,"call startIntentSenderForResult exception "+e);
            return  ERROR_BUY_EXCEPTION;
        }
        return  0;
    }

    //this is a sync call, will connect to network and block calling thread
    public int ConsumePurchase(String token,String jsCallBack){
        Log.d(TAG, "ConsumePurchase:begin");
        mStrConsumeJSCB=jsCallBack;
        try{
            int result = mService.consumePurchase(3, mActivity.getPackageName(), token);
            Log.d(TAG, "ConsumePurchase:return "+result);
            if(mStrConsumeJSCB==null || mStrConsumeJSCB.isEmpty()){
                Log.d(TAG, "ConsumePurchase: mStrConsumeJSCB is empty,do nothing");
            }else{
                Log.d(TAG, "ConsumePurchase: call mStrConsumeJSCB");
                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrConsumeJSCB,token,
                        result,mBillingResponseError.get(result),
                        mBillingResponseErrorDetail.get(result)));
            }
        }catch (Exception e){
            Log.d(TAG, "ConsumePurchase: exception!"+e);
            int result=-2;
            JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrConsumeJSCB,token,
                    result,mBillingResponseError.get(result),
                    mBillingResponseErrorDetail.get(result)));
        }
        return  0;
    }

    public void onPurchasesEnd(String jsonInStr,String strSignature) {
        Log.d(TAG, "onPurchasesEnd called");
        if(mStrBuyJSCB==null || mStrBuyJSCB.isEmpty()){
            Log.d(TAG, "onPurchasesEnd mStrBuyJSCB is empty,do nothing");
            return;
        }
        Log.d(TAG, "onPurchasesEnd, call mStrBuyJSCB");
        JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrBuyJSCB,jsonInStr,strSignature));
        Log.d(TAG, "onPurchasesEnd return");
        return;
    }

    public int GetUncheckedOrderCount(String jsCallBack){
        Log.d(TAG, "GetUncheckedOrderCount:begin");
        if(mService==null){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,"cafeBazaar init not finished yet",0);
                }
            });
            return ERROR_INIT_NOT_FINISHED;
        }
        mStrGetUnCheckedCountJSCB=jsCallBack;
        ParseUncheckedOrder("");
        Log.d(TAG, "GetUncheckedOrderCount:return");
        return  0;
    }

    public int ParseUncheckedOrder(String jsCallBack){
        Log.d(TAG, "ParseUncheckedOrder:begin");
        if(mService==null){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,"cafeBazaar init not finished yet",0);
                }
            });
            Log.d(TAG, "ParseUncheckedOrder:cafeBazaar init not finished");
            return ERROR_INIT_NOT_FINISHED;
        }
        mStrBuyJSCB=jsCallBack;

        try{
            Bundle ownedItems = mService.getPurchases(3, mActivity.getPackageName(), "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            Log.d(TAG, "ParseUncheckedOrder:return "+response);
            if (response != 0)
                return  0;

            ArrayList ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList purchaseList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
//          String continueToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

            if(mStrGetUnCheckedCountJSCB!=null && !mStrGetUnCheckedCountJSCB.isEmpty()){
                Log.d(TAG, "ParseUncheckedOrder: call mStrGetUnCheckedCountJSCB");
                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrGetUnCheckedCountJSCB,purchaseList.size()));
                mStrGetUnCheckedCountJSCB=null;
                return 0;
            }

            for (int i = 0; i < purchaseList.size(); ++i) {
                String purchaseData = (String)purchaseList.get(i);
                String signature = (String)signatureList.get(i);
                Log.d(TAG, "ParseUncheckedOrder: need to check sku "+ownedSkus.get(i));
                onPurchasesEnd(purchaseData,signature);
                return  0;
            }
        }catch (Exception e){
            Log.d(TAG, "ParseUncheckedOrder: exception"+e);
            if(mStrGetUnCheckedCountJSCB!=null && !mStrGetUnCheckedCountJSCB.isEmpty()){
                Log.d(TAG, "ParseUncheckedOrder: call mStrGetUnCheckedCountJSCB");
                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrGetUnCheckedCountJSCB,0));
                mStrGetUnCheckedCountJSCB=null;
            }
        }
        return  0;
    }
}
