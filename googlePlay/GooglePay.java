package org.android.googlePlay;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;

import org.android.util.FileUtil;
import org.android.util.JSUtil;
import org.android.util.JsonUtil;
import org.android.util.LogFileUtil;
import org.android.util.UIUtil;
import org.cocos2dx.lib.Cocos2dxActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GooglePay {
    public String TAG;
    public Activity mActivity;
    public boolean mInited;
    public String mStrGetUnCheckedCountJSCB;
    public String mStrBuyJSCB;
    public String mStrConsumeJSCB;
    public BillingManager mBillingManager;

    private static int ERROR_INIT_NOT_FINISHED =1;
    private static int ERROR_INIT_ERROR =2;
    private String mPubkey;
    private String mFileRoot;
    private int mInitCode =-1;
    public HashMap mBillingResponseError=new HashMap<Integer,String>();
    public HashMap mBillingResponseErrorDetail=new HashMap<Integer,String>();

    public GooglePay(Activity a,String tag,String pubkey){
        if(tag.isEmpty())
            TAG=a.getPackageName()+" GooglePay";
        else
            TAG=tag+" GooglePay";
        mActivity=a;
        mInited=false;
        mPubkey=pubkey;

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

        mBillingManager = new BillingManager(mActivity, new BillingListener(),pubkey);
    }

    public void setWritableRootPath(String rootPath) {
        mFileRoot=rootPath;
    }

    public void SavePayload(String skuID,String payload){
        if(mFileRoot==null)
            return;
        if(skuID==null || skuID.length()<=0)
            return;
        if(payload==null || payload.length()<=0)
            return;
        String fileName=mFileRoot+"payload.json";
        String str=FileUtil.readStr(fileName);
        HashMap<String,String> json=null;
        if(str!=null && str.length()>0)
            json=JsonUtil.decodeToHashMapStringString(str);
        if(json==null)
            json=new HashMap();
        json.put(skuID,payload);
        FileUtil.writeFile(fileName,JsonUtil.encode((Object)json).getBytes());
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]SavePayload:save skuID %s paylaod %s to payload.json ok",skuID,payload);
        Log.d(TAG,"SavePayload:save skuID "+skuID+" payload "+payload+" to payload.json ok");
    }

    public void ConsumePayload(String skuID){
        if(mFileRoot==null)
            return;
        if(skuID==null || skuID.length()<=0)
            return;
        String fileName=mFileRoot+"payload.json";
        if(!FileUtil.isExist(fileName))
            return;
        String str=FileUtil.readStr(fileName);
        if(str==null || str.length()<=0)
            return;
        HashMap<String,String> json=JsonUtil.decodeToHashMapStringString(str);
        if(json==null)
            return;
        String payload=json.get(skuID);
        if(payload==null)
            return;
        json.remove(skuID);
        FileUtil.writeFile(fileName,JsonUtil.encode((Object)json).getBytes());
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]ConsumePayload:remove skuID %s paylaod %s from payload.json ok",skuID,payload);
        Log.d(TAG,"consumePayload:remove skuID "+skuID+" payload "+payload+" from payload.json ok");
    }

    public String GetPayload(String skuID){
        if(mFileRoot==null)
            return "";
        if(skuID==null || skuID.length()<=0)
            return "";
        String fileName=mFileRoot+"payload.json";
        if(!FileUtil.isExist(fileName))
            return "";
        String str=FileUtil.readStr(fileName);
        if(str==null || str.length()<=0)
            return "";
        HashMap<String,String> json=JsonUtil.decodeToHashMapStringString(str);
        if(json==null)
            return "";
        String payload=json.get(skuID);
        Log.d(TAG,"GetPayload:return skuID "+skuID+" payload "+payload+" from payload.json");
        return  payload;
    }

    public boolean isServiceAvailable(){
        return  mInited;
    }

    public String getPubkey(){return mPubkey;}

    public String getErrorMsg(int errCode){
        return (String)mBillingResponseError.get(errCode)+' '+(String)mBillingResponseErrorDetail.get(errCode);
    }

    public int GetUncheckedOrderCount(String jsCallBack){
        Log.d(TAG, "GetUncheckedOrderCount:begin");
        mInitCode =mBillingManager.getBillingClientResponseCode();
        if(mInitCode != BillingResponseCode.OK){
            Log.d(TAG, "GetUncheckedOrderCount:BillingManager init error:"+mBillingResponseError.get(mInitCode));
            return ERROR_INIT_ERROR;
        }
        if(!mInited){
            Log.d(TAG, "GetUncheckedOrderCount:BillingManager init not finished");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]GetUncheckedOrderCount:BillingManager init not finished");
            return ERROR_INIT_NOT_FINISHED;
        }
        mStrGetUnCheckedCountJSCB=jsCallBack;
        mBillingManager.queryPurchases();
        Log.d(TAG, "GetUncheckedOrderCount:return");
        return  0;
    }

    public int GetAllSkuInfo(String allIDJsonStr,String jsCallBack){
        Log.d(TAG, "GetAllSkuInfo:begin");
//        mInitCode =mBillingManager.getBillingClientResponseCode();
//        if(mInitCode != BillingResponseCode.OK){
//            Log.d(TAG, "GetAllSkuInfo:BillingManager init error:"+mBillingResponseError.get(mInitCode));
//            return ERROR_INIT_ERROR;
//        }
        if(!mInited){
            Log.d(TAG, "GetAllSkuInfo:BillingManager init not finished");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]GetUncheckedOrderCount:BillingManager init not finished");
            return ERROR_INIT_NOT_FINISHED;
        }
        mBillingManager.getAllSkuInfo((ArrayList<String>)JsonUtil.decodeToArrayList(allIDJsonStr),jsCallBack);
        Log.d(TAG, "GetAllSkuInfo:return");
        return 0;
    }


    public int BuyItem(String IDInStr,String payload,String jsCallBack,String jsStrSkuCB){
        //mBillingManager.
        Log.d(TAG, "BuyItem:begin "+IDInStr);
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuyItem:begin "+IDInStr);
        mInitCode =mBillingManager.getBillingClientResponseCode();
//        if(mInitCode != BillingResponseCode.OK){
//            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuyItem:getBillingClientResponseCode is not ok, "+mBillingResponseErrorDetail.get(mInitCode));
//            mActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    UIUtil.Toast(mActivity,(String)mBillingResponseErrorDetail.get(mInitCode),0);
//                }
//            });
//            return ERROR_INIT_ERROR;
//        }
        if(!mInited){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,"BillingManager init not finished yet",0);
                }
            });
            Log.d(TAG, "BuyItem:BillingManager init not finished yet");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuyItem:BillingManager init not finished yet");
            return ERROR_INIT_NOT_FINISHED;
        }
        mStrBuyJSCB=jsCallBack;
        mBillingManager.initiatePurchaseFlow(IDInStr,"inapp",jsCallBack,jsStrSkuCB);
        if(payload!=null && payload.length()>=0)
            SavePayload(IDInStr,payload);
        Log.d(TAG, "BuyItem:return");
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuyItem:return");
        return  0;
    }

    public int BuySubscribe(String IDInStr,String jsCallBack,String jsStrSkuCB){
        //mBillingManager.
        Log.d(TAG, "BuySubscribe:begin "+IDInStr);
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuySubscribe:begin "+IDInStr);
         mInitCode =mBillingManager.getBillingClientResponseCode();
        if(mInitCode != BillingResponseCode.OK){
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuySubscribe:getBillingClientResponseCode is not ok, "+mBillingResponseErrorDetail.get(mInitCode));
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,(String)mBillingResponseErrorDetail.get(mInitCode),0);
                }
            });
            return ERROR_INIT_ERROR;
        }
        if(!mInited){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,"BillingManager init not finished yet",0);
                }
            });
            Log.d(TAG, "BuySubscribe:BillingManager init not finished");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuySubscribe:BillingManager init not finished yet");

            return ERROR_INIT_NOT_FINISHED;
        }
        mStrBuyJSCB=jsCallBack;
        mBillingManager.initiatePurchaseFlow(IDInStr,"subs",jsCallBack,jsStrSkuCB);
        Log.d(TAG, "BuySubscribe:return");
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BuySubscribe:return");
        return  0;
    }

    public int ParseUncheckedOrder(String jsCallBack){
        Log.d(TAG, "ParseUncheckedOrder:begin");
        mInitCode =mBillingManager.getBillingClientResponseCode();
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]ParseUncheckedOrder:entry");

        if(mInitCode != BillingResponseCode.OK){
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]ParseUncheckedOrder:getBillingClientResponseCode is not ok, "+mBillingResponseErrorDetail.get(mInitCode));
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,(String)mBillingResponseErrorDetail.get(mInitCode),0);
                }
            });
            return ERROR_INIT_ERROR;
        }
        if(!mInited){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    UIUtil.Toast(mActivity,"BillingManager init not finished yet",0);
                }
            });
            Log.d(TAG, "ParseUncheckedOrder:BillingManager init not finished");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]ParseUncheckedOrder:BillingManager init not finished yet");
            return ERROR_INIT_NOT_FINISHED;
        }
        mStrBuyJSCB=jsCallBack;
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]ParseUncheckedOrder:queryPurchases begin");
        mBillingManager.queryPurchases();
        Log.d(TAG, "ParseUncheckedOrder:return");
        return  0;
    }

    public int ConsumePurchase(String token,String jsCallBack){
        Log.d(TAG, "ConsumePurchase:begin");
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]ConsumePurchase:return");
        mStrConsumeJSCB=jsCallBack;
        mBillingManager.consumeAsync(token);
        Log.d(TAG, "ConsumePurchase:return");
        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]ConsumePurchase:return");
        return  0;
    }

    private class BillingListener implements BillingManager.BillingUpdatesListener {
        public String TAG2;

        public BillingListener(){
            TAG2=TAG+" BillingListener";
        }

        @Override
        //与google play store的连接初始化完成
        //如果初始化失败，这个回调不会被调用，通过mBillingManager.getBillingClientResponseCode()返回错误码
        public void onBillingClientSetupFinished() {
            Log.d(TAG2, "onBillingClientSetupFinished");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BillingListener:onBillingClientSetupFinished");
            mInited=true;
        }

        @Override
        //道具消耗完成的回调
        public void onConsumeFinished(String token, BillingResult result) {
            int code=result.getResponseCode();
            Log.d(TAG2, "onConsumeFinished, result code is "+code+", "+mBillingResponseError.get(code)+" "+mBillingResponseErrorDetail.get(code));
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onConsumeFinished, result code is "+code+", "+mBillingResponseError.get(code)+" "+mBillingResponseErrorDetail.get(code));

            if(code!=0)
                UIUtil.Toast(mActivity,"Consume Error Code "+code+" "+mBillingResponseError.get(code)+" "+mBillingResponseErrorDetail.get(code),1);

            if(mStrConsumeJSCB==null || mStrConsumeJSCB.isEmpty()){
                Log.d(TAG2, "onPurchasesUpdated mStrConsumeJSCB is empty,do nothing");
                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onPurchasesUpdated mStrConsumeJSCB is empty,do nothing");
            }else{
                Log.d(TAG2, "onPurchasesUpdated: call mStrConsumeJSCB");
                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onPurchasesUpdated: call mStrConsumeJSCB");
                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrConsumeJSCB,token,
                        String.valueOf(code),mBillingResponseError.get(code),
                        mBillingResponseErrorDetail.get(code)));
            }
        }

        @Override
        //已经购买的但没有消耗的sku，或者订阅，在这里进行处理
        //用户取消购买，不会进行回调！！
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            Log.d(TAG2, "onPurchasesUpdated called,purchaseList size "+purchaseList.size());
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]onPurchasesUpdated called,purchaseList size "+purchaseList.size());
            if(mStrGetUnCheckedCountJSCB!=null && !mStrGetUnCheckedCountJSCB.isEmpty()){
                Log.d(TAG2, "onPurchasesUpdated: call mStrGetUnCheckedCountJSCB");
                String callBackStr=mStrGetUnCheckedCountJSCB+'('+purchaseList.size()+')';
                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onPurchasesUpdated: call "+callBackStr);
                if(purchaseList.size()>0){
                    LogFileUtil.log2File("pay.log","pay_backup.log",callBackStr);
                }
                JSUtil.eval((Cocos2dxActivity)mActivity,callBackStr);

//                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onPurchasesUpdated: call mStrGetUnCheckedCountJSCB( %d )",purchaseList.size());
//                if(purchaseList.size()>0){
//                    LogFileUtil.log2File("pay.log","pay_backup.log",String.format(mStrGetUnCheckedCountJSCB,purchaseList.size()));
//                }
//                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrGetUnCheckedCountJSCB,purchaseList.size()));

                mStrGetUnCheckedCountJSCB=null;
                return;
            }

            if(mStrBuyJSCB==null || mStrBuyJSCB.isEmpty()){
                Log.d(TAG2, "onPurchasesUpdated mStrBuyJSCB is empty,do nothing");
                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onPurchasesUpdated mStrBuyJSCB is empty,do nothing");
            }else{
                for (Purchase purchase : purchaseList) {
                    Log.d(TAG2, "onPurchasesUpdated:"+purchase.getSkus()+", call mStrBuyJSCB");
                    LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]onPurchasesUpdated:"+purchase.getSkus()+", call mStrBuyJSCB");
                    String jsonStr=purchase.getOriginalJson();
                    HashMap<String,String> json=JsonUtil.decodeToHashMapStringString(jsonStr);
                    String skuID=json.get("productId");
                    String payload=GetPayload(skuID);
                    JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrBuyJSCB,purchase.getOriginalJson(),
                            purchase.getSignature(),payload));
//                    Log.d(TAG2, "onPurchasesUpdated:only parse one purchase,break!");
//                    break;
                }
            }
            Log.d(TAG2, "onPurchasesUpdated return");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onPurchasesUpdated return");
        }
    }
}
