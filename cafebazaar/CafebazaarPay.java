//package org.android.cafebazaar;
//
//import android.app.Activity;
//import android.app.PendingIntent;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.android.vending.billing.IInAppBillingService;
//
//import org.android.util.FileUtil;
//import org.android.util.JSUtil;
//import org.android.util.JsonUtil;
//import org.android.util.LogFileUtil;
//import org.android.util.UIUtil;
//import org.cocos2dx.lib.Cocos2dxActivity;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
////import com.android.vending.billing.IInAppBillingService;
//
//public class CafebazaarPay {
//    public String TAG;
//    public Activity mActivity;
//    public String mStrGetUnCheckedCountJSCB;
//    public String mStrBuyJSCB;
//    public String mStrConsumeJSCB;
//    private String mFileRoot;
//
//    private static int ERROR_INIT_NOT_FINISHED =1;
////    private static int ERROR_INIT_ERROR =2;
//    private static int ERROR_BUY_EXCEPTION =3;
////    private int mInitReturnCodeFromBillingMgr=-1;
//    public HashMap mBillingResponseError=new HashMap<Integer,String>();
//    public HashMap mBillingResponseErrorDetail=new HashMap<Integer,String>();
//
//    IInAppBillingService mService;
//    ServiceConnection mServiceConn;
//
//    public ServiceConnection getBillingServiceConnection(){
//        return mServiceConn;
//    }
//
//    public CafebazaarPay(Activity a, String tag){
//        if(tag.isEmpty())
//            TAG=a.getPackageName()+" CafebazaarPay";
//        else
//            TAG=tag+" CafebazaarPay";
//        mActivity=a;
//        mBillingResponseError.put(-2,"FEATURE_NOT_SUPPORTED");
//        mBillingResponseError.put(-1,"SERVICE_DISCONNECTED");
//        mBillingResponseError.put(0,"OK");
//        mBillingResponseError.put(1,"USER_CANCELED");
//        mBillingResponseError.put(2,"SERVICE_UNAVAILABLE");
//        mBillingResponseError.put(3,"BILLING_UNAVAILABLE");
//        mBillingResponseError.put(4,"ITEM_UNAVAILABLE");
//        mBillingResponseError.put(5,"DEVELOPER_ERROR");
//        mBillingResponseError.put(6,"ERROR");
//        mBillingResponseError.put(7,"ITEM_ALREADY_OWNED");
//        mBillingResponseError.put(8,"ITEM_NOT_OWNED");
//
//        mBillingResponseErrorDetail.put(-2,"Requested feature is not supported by Play Store on the current device");
//        mBillingResponseErrorDetail.put(-1,"Play Store service is not connected now - potentially transient state");
//        mBillingResponseErrorDetail.put(0,"Success");
//        mBillingResponseErrorDetail.put(1,"User pressed back or canceled a dialog");
//        mBillingResponseErrorDetail.put(2,"Network connection is down");
//        mBillingResponseErrorDetail.put(3,"Billing API version is not supported for the type requested");
//        mBillingResponseErrorDetail.put(4,"Requested product is not available for purchase");
//        mBillingResponseErrorDetail.put(5,"Invalid arguments provided to the API. This error can also indicate that the application was not correctly signed or properly set up for In-app Billing in Google Play, or does not have the necessary permissions in its manifest");
//        mBillingResponseErrorDetail.put(6,"Fatal error during the API action,please satisfy all the rights needed by the game and google play and google services");
//        mBillingResponseErrorDetail.put(7,"Failure to purchase since item is already owned");
//        mBillingResponseErrorDetail.put(8,"Failure to consume since item is not owned");
//
//        mServiceConn = new ServiceConnection() {
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                mService = null;
//            }
//            @Override
//            public void onServiceConnected(ComponentName name,IBinder service) {
//                mService = IInAppBillingService.Stub.asInterface(service);
//            }
//
//            @Override
//            public void onBindingDied(ComponentName name) {
//                Log.d(TAG, "onBindingDied");
//                mService = null;
//            }
//        };
//
//        Intent intent=new Intent("ir.cafebazaar.pardakht.InAppBillingService.BIND");
//        //intent.setPackage(mActivity.getPackageName());
//        intent.setPackage("com.farsitel.bazaar");
//        a.bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
//    }
//
//    public boolean isServiceAvailable(){
//        return  mService!=null;
//    }
//
//    public void setWritableRootPath(String rootPath) {
//        mFileRoot=rootPath;
//    }
//
//    public void SavePayload(String skuID,String payload){
//        if(mFileRoot==null)
//            return;
//        if(skuID==null || skuID.length()<=0)
//            return;
//        if(payload==null || payload.length()<=0)
//            return;
//        String fileName=mFileRoot+"payload.json";
//        String str=FileUtil.readStr(fileName);
//        HashMap<String,String> json=null;
//        if(str!=null && str.length()>0)
//            json=JsonUtil.decodeToHashMapStringString(str);
//        if(json==null)
//            json=new HashMap();
//        json.put(skuID,payload);
//        FileUtil.writeFile(fileName,JsonUtil.encode((Object)json).getBytes());
//        LogFileUtil.log2File("pay.log","pay_backup.log","[cafebazaarPay]SavePayload:save skuID %s paylaod %s to payload.json ok",skuID,payload);
//        Log.d(TAG,"SavePayload:save skuID "+skuID+" payload "+payload+" to payload.json ok");
//    }
//
//    public void ConsumePayload(String skuID){
//        if(mFileRoot==null)
//            return;
//        if(skuID==null || skuID.length()<=0)
//            return;
//        String fileName=mFileRoot+"payload.json";
//        if(!FileUtil.isExist(fileName))
//            return;
//        String str=FileUtil.readStr(fileName);
//        if(str==null || str.length()<=0)
//            return;
//        HashMap<String,String> json=JsonUtil.decodeToHashMapStringString(str);
//        if(json==null)
//            return;
//        String payload=json.get(skuID);
//        if(payload==null)
//            return;
//        json.remove(skuID);
//        FileUtil.writeFile(fileName,JsonUtil.encode((Object)json).getBytes());
//        LogFileUtil.log2File("pay.log","pay_backup.log","[cafebazaarPay]ConsumePayload:remove skuID %s paylaod %s from payload.json ok",skuID,payload);
//        Log.d(TAG,"consumePayload:remove skuID "+skuID+" payload "+payload+" from payload.json ok");
//    }
//
//    public String GetPayload(String skuID){
//        if(mFileRoot==null)
//            return "";
//        if(skuID==null || skuID.length()<=0)
//            return "";
//        String fileName=mFileRoot+"payload.json";
//        if(!FileUtil.isExist(fileName))
//            return "";
//        String str=FileUtil.readStr(fileName);
//        if(str==null || str.length()<=0)
//            return "";
//        HashMap<String,String> json=JsonUtil.decodeToHashMapStringString(str);
//        if(json==null)
//            return "";
//        String payload=json.get(skuID);
//        Log.d(TAG,"GetPayload:return skuID "+skuID+" payload "+payload+" from payload.json");
//        return  payload;
//    }
//
//    public int BuyItem(String skuName,String payload,String jsCallBack){
//        Log.d(TAG, "BuyItem:begin "+skuName);
//        if(mService==null){
//            mActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    UIUtil.Toast(mActivity,"cafeBazaar not connected yet",0);
//                }
//            });
//            Log.d(TAG, "BuyItem:cafeBazaar init not finished yet");
//            return ERROR_INIT_NOT_FINISHED;
//        }
//        mStrBuyJSCB=jsCallBack;
//        try {
//            Bundle buyIntentBundle = mService.getBuyIntent(3, mActivity.getPackageName(),
//                    skuName, "inapp", "sendBackStr");
//            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
//            mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
//                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
//                    Integer.valueOf(0));
//            if(payload!=null && payload.length()>=0)
//                SavePayload(skuName,payload);
//        }catch (Exception e){
//            Log.d(TAG,"call startIntentSenderForResult exception "+e);
//            return  ERROR_BUY_EXCEPTION;
//        }
//        return  0;
//    }
//
//    public int BuySubscribe(String skuName,String jsCallBack){
//        Log.d(TAG, "BuySubscribe:begin "+skuName);
//        if(mService==null){
//            mActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    UIUtil.Toast(mActivity,"cafeBazaar not connected yet",0);
//                }
//            });
//            Log.d(TAG, "BuyItem:cafeBazaar init not finished yet");
//            return ERROR_INIT_NOT_FINISHED;
//        }
//        mStrBuyJSCB=jsCallBack;
//        try {
//            Bundle buyIntentBundle = mService.getBuyIntent(3, mActivity.getPackageName(),
//                    skuName, "subs", "sendBackStr");
//            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
//            mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
//                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
//                    Integer.valueOf(0));
//        }catch (Exception e){
//            Log.d(TAG,"call startIntentSenderForResult exception "+e);
//            return  ERROR_BUY_EXCEPTION;
//        }
//        return  0;
//    }
//
//    //this is a sync call, will connect to network and block calling thread
//    public int ConsumePurchase(String token,String jsCallBack){
//        Log.d(TAG, "ConsumePurchase:begin");
//        mStrConsumeJSCB=jsCallBack;
//        try{
//            int result = mService.consumePurchase(3, mActivity.getPackageName(), token);
//            Log.d(TAG, "ConsumePurchase:return "+result);
//            if(mStrConsumeJSCB==null || mStrConsumeJSCB.isEmpty()){
//                Log.d(TAG, "ConsumePurchase: mStrConsumeJSCB is empty,do nothing");
//            }else{
//                Log.d(TAG, "ConsumePurchase: call mStrConsumeJSCB");
//                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrConsumeJSCB,token,
//                        result,mBillingResponseError.get(result),
//                        mBillingResponseErrorDetail.get(result)));
//            }
//        }catch (Exception e){
//            Log.d(TAG, "ConsumePurchase: exception!"+e);
//            int result=-2;
//            JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrConsumeJSCB,token,
//                    result,mBillingResponseError.get(result),
//                    mBillingResponseErrorDetail.get(result)));
//        }
//        return  0;
//    }
//
//    public void onPurchasesEnd(String jsonInStr,String strSignature) {
//        Log.d(TAG, "onPurchasesEnd called");
//        if(mStrBuyJSCB==null || mStrBuyJSCB.isEmpty()){
//            Log.d(TAG, "onPurchasesEnd mStrBuyJSCB is empty,do nothing");
//            return;
//        }
//        Log.d(TAG, "onPurchasesEnd, call mStrBuyJSCB");
//
//        HashMap<String,String> json=JsonUtil.decodeToHashMapStringString(jsonInStr);
//        String skuID=json.get("productId");
//        String payload=GetPayload(skuID);
//
//        JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrBuyJSCB,jsonInStr,strSignature,payload));
//        Log.d(TAG, "onPurchasesEnd return");
//    }
//
//    public int GetUncheckedOrderCount(String jsCallBack){
//        Log.d(TAG, "GetUncheckedOrderCount:begin");
//        if(mService==null){
//            mActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    UIUtil.Toast(mActivity,"cafeBazaar not connected yet",0);
//                }
//            });
//            return ERROR_INIT_NOT_FINISHED;
//        }
//        mStrGetUnCheckedCountJSCB=jsCallBack;
//        ParseUncheckedOrder("");
//        Log.d(TAG, "GetUncheckedOrderCount:return");
//        return  0;
//    }
//
//    public int ParseUncheckedOrder(String jsCallBack){
//        Log.d(TAG, "ParseUncheckedOrder:begin");
//        if(mService==null){
//            mActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    UIUtil.Toast(mActivity,"cafeBazaar not connected yet",0);
//                }
//            });
//            Log.d(TAG, "ParseUncheckedOrder:cafeBazaar init not finished");
//            return ERROR_INIT_NOT_FINISHED;
//        }
//        mStrBuyJSCB=jsCallBack;
//
//        try{
//            Bundle ownedItems = mService.getPurchases(3, mActivity.getPackageName(), "inapp", null);
//            int response = ownedItems.getInt("RESPONSE_CODE");
//            Log.d(TAG, "ParseUncheckedOrder:getPurchases response "+response);
//            if (response != 0)
//                return  0;
//
//            ArrayList ownedSku = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
//            ArrayList purchaseList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
//            ArrayList signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
////          String continueToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
//
//            if(mStrGetUnCheckedCountJSCB!=null && !mStrGetUnCheckedCountJSCB.isEmpty()){
//                Log.d(TAG, "ParseUncheckedOrder: call mStrGetUnCheckedCountJSCB");
//                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrGetUnCheckedCountJSCB,purchaseList.size()));
//                mStrGetUnCheckedCountJSCB=null;
//                return 0;
//            }
//
//            Log.d(TAG, "ParseUncheckedOrder: begin to check INAPP_PURCHASE_ITEM_LIST count "+ownedSku.size());
//            Log.d(TAG, "ParseUncheckedOrder: begin to check INAPP_PURCHASE_DATA_LIST count "+purchaseList.size());
//            Log.d(TAG, "ParseUncheckedOrder: begin to check INAPP_DATA_SIGNATURE_LIST count "+signatureList.size());
//            for (int i = 0; i < purchaseList.size(); i++) {
//                String purchaseData = (String)purchaseList.get(i);
//                String signature = (String)signatureList.get(i);
//                Log.d(TAG, "ParseUncheckedOrder: need to check sku "+ownedSku.get(i));
//                onPurchasesEnd(purchaseData,signature);
//                return  0;
//            }
//        }catch (Exception e){
//            Log.d(TAG, "ParseUncheckedOrder: exception"+e);
//            if(mStrGetUnCheckedCountJSCB!=null && !mStrGetUnCheckedCountJSCB.isEmpty()){
//                Log.d(TAG, "ParseUncheckedOrder: call mStrGetUnCheckedCountJSCB");
//                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrGetUnCheckedCountJSCB,0));
//                mStrGetUnCheckedCountJSCB=null;
//            }
//        }
//        return  0;
//    }
//}
