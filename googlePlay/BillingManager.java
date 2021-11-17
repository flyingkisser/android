/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.android.googlePlay;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.FeatureType;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.android.util.JSUtil;
import org.android.util.JsonUtil;
import org.android.util.LogFileUtil;
import org.android.util.UIUtil;
import org.cocos2dx.javascript.AppActivity;
import org.cocos2dx.lib.Cocos2dxActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
public class BillingManager implements PurchasesUpdatedListener {
    // Default value of mBillingClientResponseCode until BillingManager was not yeat initialized
    public static final int BILLING_MANAGER_NOT_INITIALIZED  = -1;

    private static final String TAG = "BillingManager";

    /** A reference to BillingClient **/
    private BillingClient mBillingClient;
    private SkuDetails mCurrentSkuDetail=null;

    /**
     * True if billing service is connected now.
     */
    private boolean mIsServiceConnected;

    private final BillingUpdatesListener mBillingUpdatesListener;

    private final Activity mActivity;

    private final List<Purchase> mPurchases = new ArrayList<>();

    private Set<String> mTokensToBeConsumed;

    private int mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED;

    private String mStrJsCb;

    private Map<String,SkuDetails> mSkuDetailsMap=new HashMap<>();

    /* BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    // private static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtpOYeS2jAeKmh6V5trhjDbsEKChjqSaDvkKYOS5Fk/ErtvCXtVdZB9vY3/3ydukyry+W+p30cs/XB01aWKutbjdm6lYdsuOQhR4+Du9fzj+rmdnRdmZjEQ/y5YtfrMLGZEbweLWD+kbszR/iNinBeFUbtXi+5EFtJvpgmQh6Qqe0unofN8j+ZQisE5+qnfWpMMo2NufVe75McKi1r6GdXz0+I2ThMzy8yX/POj2O1PFWdTmeT+5vfOOMhjMYC3ggxTbASkrOWCkm/ZP7Ir8iY29822ZFbKZuyQpQ8Yk450+wWBdHyInkSXU7nmULswLV7HL/tk53f3W8bVeGHO92OwIDAQAB";
    private String BASE_64_ENCODED_PUBLIC_KEY;
    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token, BillingResult result);
        void onPurchasesUpdated(List<Purchase> purchases);
    }

    /**
     * Listener for the Billing client state to become connected
     */
    public interface ServiceConnectedListener {
        void onServiceConnected(BillingResponseCode code);
    }

    public void setJSCallback(String strJsCb){
        mStrJsCb=strJsCb;
    }

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener,String pubkey) {
        Log.d(TAG, "Creating Billing client.");
        mActivity = activity;
        BASE_64_ENCODED_PUBLIC_KEY=pubkey;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).enablePendingPurchases().build();

        Log.d(TAG, "Starting setup.");

        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                // Notifying the listener that billing client is ready
                mBillingUpdatesListener.onBillingClientSetupFinished();
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "startServiceConnection: Setup successful. Querying inventory.");
                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]startServiceConnection: Setup successful. Querying inventory.");
                queryPurchases();
            }
        });
    }

    public void getAllSkuInfo(ArrayList<String> allSkuList,final String jsStrCallback) {
//        List<String> skuList = new ArrayList<> ();
//        skuList.add("premium_upgrade");
//        skuList.add("gas");
        Log.d(TAG, "getAllSkuInfo: begin to query sku length "+allSkuList.size());
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(allSkuList).setType(SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        int resultCode=billingResult.getResponseCode();
                        String errMsg="";
                        if(resultCode!=BillingResponseCode.OK){
                            //如果获取不到sku的信息，把错误码写进mBillingClientResponseCode，则上层函数调用购买代码时，会直接返回此错误码
                            mBillingClientResponseCode=resultCode;
                            errMsg=((AppActivity)mActivity).mGooglePay.getErrorMsg(resultCode);
                            Log.w(TAG, "getAllSkuInfo: onSkuDetailsResponse() got an error, resultCode: " + resultCode+" errMsg: "+errMsg);
                            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]onSkuDetailsResponse: got an error, resultCode: " + resultCode+" errMsg: "+errMsg);
                            //转成字符串，通知js端
                            JSUtil.eval((Cocos2dxActivity)mActivity,String.format(jsStrCallback,resultCode,errMsg,"","",""));
//                            UIUtil.Toast(mActivity,errMsg,1);
                            return;
                        }

                        if(skuDetailsList==null){
                            Log.d(TAG, "getAllSkuInfo: querySkuDetailsAsync return null!");
                            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]getAllSkuInfo: querySkuDetailsAsync return null!");
                            return;
                        }

                        //状态码设置回ok
                        mBillingClientResponseCode=0;
                        //把所有的sku放进skuAllMap里
                        ArrayList<String> idArr=new ArrayList<>();
                        ArrayList<String> priceArr=new ArrayList<>();
                        ArrayList<String> currencyArr=new ArrayList<>();
                        for (SkuDetails skuDetails : skuDetailsList) {
                            String id=skuDetails.getSku();
                            idArr.add(id);
                            priceArr.add(skuDetails.getPrice());
                            currencyArr.add(skuDetails.getPriceCurrencyCode());
                            // 缓存
                            if(!mSkuDetailsMap.containsKey(id))
                                mSkuDetailsMap.put(id, skuDetails);
                        }
                        Log.d(TAG, "getAllSkuInfo: successful,  get sku length "+idArr.size());
                        //转成字符串，通知js端
                        String jsStr=String.format(jsStrCallback,String.valueOf(resultCode),errMsg,JsonUtil.encode(idArr),JsonUtil.encode(priceArr),JsonUtil.encode(currencyArr));
                        JSUtil.eval((Cocos2dxActivity)mActivity,jsStr);
                    }
                });
    }

    public void getOneSkuInfo(final String skuName, final Runnable cb, final String jsStrCallback) {
        List<String> skuList = new ArrayList<> ();
        skuList.add(skuName);

        Log.d(TAG, "getOneSkuInfo: begin to query "+skuName);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        int resultCode=billingResult.getResponseCode();
                        if(resultCode!=BillingResponseCode.OK){
                            String errMsg=((AppActivity)mActivity).mGooglePay.getErrorMsg(resultCode);
                            Log.w(TAG, "getOneSkuInfo:onSkuDetailsResponse() got an error, resultCode: " + resultCode+" errMsg: "+errMsg);
                            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]getOneSkuInfo:onSkuDetailsResponse: got an error, resultCode: " + resultCode+" errMsg: "+errMsg);
                            cb.run();
                            UIUtil.Toast(mActivity,errMsg,1);
                            return;
                        }
                        if(skuDetailsList==null){
                            Log.d(TAG, "getOneSkuInfo: querySkuDetailsAsync return null!");
                            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]getOneSkuInfo: querySkuDetailsAsync return null!");
                            return;
                        }
                        //把所有的sku放进skuAllMap里
                        ArrayList<String> idArr=new ArrayList<>();
                        ArrayList<String> priceArr=new ArrayList<>();
                        ArrayList<String> currencyArr=new ArrayList<>();
                        Log.d(TAG, "getOneSkuInfo: get sku length "+skuDetailsList.size());
                        for (SkuDetails skuDetails : skuDetailsList) {
                            String id=skuDetails.getSku();
                            idArr.add(id);
                            priceArr.add(skuDetails.getPrice());
                            currencyArr.add(skuDetails.getPriceCurrencyCode());
                            // 缓存
                            if(!mSkuDetailsMap.containsKey(id)){
                                mSkuDetailsMap.put(id, skuDetails);
                                Log.d(TAG, "getOneSkuInfo: mSkuDetailsMap.put "+id);
                            }
                            if(id.equals(skuName))
                                mCurrentSkuDetail=skuDetails;
                        }
                        if(jsStrCallback!=null){
                            //转成字符串，通知js端
                            Log.d(TAG, "getOneSkuInfo: call js callback");
                            JSUtil.eval((Cocos2dxActivity)mActivity,String.format(jsStrCallback,"","",JsonUtil.encode(idArr),JsonUtil.encode(priceArr),JsonUtil.encode(currencyArr)));
                        }
                        Log.d(TAG, "getOneSkuInfo: call cb and return!");
                        cb.run();
                      }
                });
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    @Override
    public void onPurchasesUpdated(BillingResult result, List<Purchase> purchases) {
        int resultCode=result.getResponseCode();
        if (resultCode == BillingResponseCode.OK && purchases!=null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
            //mBillingUpdatesListener.onPurchasesUpdated(mPurchases);
            mBillingUpdatesListener.onPurchasesUpdated(purchases);
            return;
        }

        String errMsg=((AppActivity)mActivity).mGooglePay.getErrorMsg(resultCode);
        if (resultCode == BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
            LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]onPurchasesUpdated: user cancelled");
            if(mStrJsCb!=null)
                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrJsCb,String.valueOf(resultCode),errMsg));
            UIUtil.Toast(mActivity,"User Cancelled!",1);
        } else {
            Log.w(TAG, "onPurchasesUpdated() got an error, resultCode: " + resultCode+" errMsg: "+errMsg);
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]onPurchasesUpdated: got an error, resultCode: " + resultCode+" errMsg: "+errMsg);
            if(mStrJsCb!=null)
                JSUtil.eval((Cocos2dxActivity)mActivity,String.format(mStrJsCb,String.valueOf(resultCode),errMsg));
            UIUtil.Toast(mActivity,errMsg,1);
        }
    }

    /**
     * Start a purchase or subscription replace flow
     */
    public void initiatePurchaseFlow(final String skuId, final @SkuType String billingType,final String jsStrCallback,final String jsStrSkuCB) {
        if(jsStrCallback!=null)
            setJSCallback(jsStrCallback);
        Log.i(TAG, "initiatePurchaseFlow:begin to find sku detail");
        SkuDetails skuDetails=mSkuDetailsMap.get(skuId);
        if (skuDetails==null){
            Log.i(TAG, "initiatePurchaseFlow:cannot find this sku "+skuId+",will query it first!");
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]initiatePurchaseFlow:cannot find this sku "+skuId);
            //JSUtil.eval((Cocos2dxActivity)mActivity,String.format(jsStrCallback,21,skuId+" is not defined!"));
            Runnable onGetSkuDetail = new Runnable() {
                @Override
                public void run() {
                    if(mCurrentSkuDetail==null){
                        Log.d(TAG, "initiatePurchaseFlow:getOneSkuInfo ok, but mCurrentSkuDetail is null,return!");
                        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]initiatePurchaseFlow:getOneSkuInfo ok, but mCurrentSkuDetail is null,return!");
                        JSUtil.eval((Cocos2dxActivity)mActivity,String.format(jsStrCallback,"21",skuId+" query failed!"));
                        return;
                    }
                    Log.d(TAG, "initiatePurchaseFlow:getOneSkuInfo ok, Launching in-app purchase flow.");
                    LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]initiatePurchaseFlow:getOneSkuInfo ok, Launching in-app purchase flow.");
                    BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(mCurrentSkuDetail).build();
                    mBillingClient.launchBillingFlow(mActivity, purchaseParams);
                }
            };
            getOneSkuInfo(skuId,onGetSkuDetail,jsStrSkuCB);
            return;
        }

        mCurrentSkuDetail=skuDetails;
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initiatePurchaseFlow:Launching in-app purchase flow.");
                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]initiatePurchaseFlow:Launching in-app purchase flow.");
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(mCurrentSkuDetail).build();
                mBillingClient.launchBillingFlow(mActivity, purchaseParams);
            }
        };
        executeServiceRequest(purchaseFlowRequest);
    }

    public Context getContext() {
        return mActivity;
    }

    /**
     * Clear the resources
     */
    public void destroy() {
        Log.d(TAG, "Destroying the manager.");

        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
    }

    public void querySkuDetailsAsync(@SkuType final String itemType, final List<String> skuList,
                                     final SkuDetailsResponseListener listener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                // Query the purchase async
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(itemType);
                mBillingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult result,List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(result,skuDetailsList);
                            }
                        });
            }
        };

        executeServiceRequest(queryRequest);
    }

    public void consumeAsync(final String purchaseToken) {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()

        if (mTokensToBeConsumed == null) {
            mTokensToBeConsumed = new HashSet<>();
        } else if (mTokensToBeConsumed.contains(purchaseToken)) {
            Log.i(TAG, "BillingManager consumeAsync:Token was already scheduled to be consumed - skipping...");
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]BillingManager consumeAsync:Token was already scheduled to be consumed - skipping..."+purchaseToken);
            //调用下面的代码会crash，线程问题
            //UIUtil.Toast(mActivity,"Token was already scheduled to be consumed - skipping...",1);
            return;
        }
        mTokensToBeConsumed.add(purchaseToken);

        // Generating Consume Response listener
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult result, String purchaseToken) {
                mTokensToBeConsumed.remove(purchaseToken);
                Log.i(TAG, "BillingManager consumeAsync:onConsumeResponse code : "+result.getResponseCode());
                LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]BillingManager consumeAsync:onConsumeResponse code : "+result.getResponseCode());
                mBillingUpdatesListener.onConsumeFinished(purchaseToken, result);
            }
        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                // Consume the purchase async
                ConsumeParams.Builder params=ConsumeParams.newBuilder();
                params.setPurchaseToken(purchaseToken);
                Log.i(TAG, "BillingManager consumeAsync:begin to consume "+purchaseToken);
                LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]BillingManager consumeAsync:begin to consume "+purchaseToken);
                mBillingClient.consumeAsync(params.build(), onConsumeListener);
            }
        };

        executeServiceRequest(consumeRequest);
    }

    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * clien connection response was not received yet.
     */
    public int getBillingClientResponseCode() {
        return mBillingClientResponseCode;
    }

    /**
     * Handles the purchase
     * <p>Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * See {@link Security#verifyPurchase(String, String, String)}
     * </p>
     * @param purchase Purchase to be handled
     */
    private void handlePurchase(Purchase purchase) {
        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
            Log.i(TAG, "handlePurchase:Got a purchase: " + purchase + "; but signature is bad. Skipping...");
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]Got a purchase: " + purchase + "; but signature is bad. Skipping...");
            return;
        }

        Log.d(TAG, "Got a verified purchase: " + purchase);

        mPurchases.add(purchase);
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private void onQueryPurchasesFinished(PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result==null || result.getResponseCode() != BillingResponseCode.OK) {
            Log.w(TAG, "onQueryPurchasesFinished:Billing client was null or result code (" + result.getResponseCode()
                    + ") was bad - quitting");
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]Billing client was null or result code (" + result.getResponseCode()
                    + ") was bad - quitting");
            return;
        }

        Log.d(TAG, "Query inventory was successful.");

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear();
        BillingResult.Builder billingResult=BillingResult.newBuilder();
        billingResult.setResponseCode(result.getResponseCode());
        onPurchasesUpdated(billingResult.build(), result.getPurchasesList());
    }

    /**
     * Checks if subscriptions are supported for current client
     * <p>Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     * </p>
     */
    public boolean areSubscriptionsSupported() {
        BillingResult result = mBillingClient.isFeatureSupported(FeatureType.SUBSCRIPTIONS);
        int code=result.getResponseCode();
        if (code != BillingResponseCode.OK) {
            Log.w(TAG, "areSubscriptionsSupported: got an error response: " + code+" errMsg: "+result.getDebugMessage());
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]areSubscriptionsSupported() got an error response: " + code+" errMsg: "+result.getDebugMessage());
        }
        return code == BillingResponseCode.OK;
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    public void queryPurchases() {
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                PurchasesResult purchasesResult = mBillingClient.queryPurchases(SkuType.INAPP);
                Log.i(TAG, "queryPurchases:Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                        + "ms");
                // If there are subscriptions supported, we add subscription rows as well
                if (areSubscriptionsSupported()) {
                    PurchasesResult subscriptionResult
                            = mBillingClient.queryPurchases(SkuType.SUBS);
                    Log.i(TAG, "queryPurchases:Querying purchases and subscriptions elapsed time: "
                            + (System.currentTimeMillis() - time) + "ms");
                    LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]Querying purchases and subscriptions elapsed time: "
                            + (System.currentTimeMillis() - time) + "ms");
                    if(subscriptionResult.getPurchasesList()!=null)
                        Log.i(TAG, "queryPurchases: getPurchasesList size: "
                            + " res: " + subscriptionResult.getPurchasesList().size());

                    if (subscriptionResult.getResponseCode() == BillingResponseCode.OK) {
                        if(subscriptionResult.getPurchasesList()!=null)
                            purchasesResult.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                    } else {
                        Log.e(TAG, "queryPurchases:Got an error response trying to query subscription purchases");
                        LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]Got an error response trying to query subscription purchases");
                    }
                } else if (purchasesResult.getResponseCode() == BillingResponseCode.OK) {
                    Log.i(TAG, "queryPurchases:Skipped subscription purchases query since they are not supported");
                } else {
                    Log.w(TAG, "queryPurchases:got an error response code: "
                            + purchasesResult.getResponseCode());
                    LogFileUtil.log2File("pay.log","pay_backup.log","[googlePay]queryPurchases got an error response code: "
                            + purchasesResult.getResponseCode());
                }
                onQueryPurchasesFinished(purchasesResult);
            }
        };

        executeServiceRequest(queryToExecute);
    }

    public void startServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult result) {
                int code=result.getResponseCode();
                Log.d(TAG, "Setup finished. Response code: " + code);

                if (code == BillingResponseCode.OK) {
                    mIsServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
                mBillingClientResponseCode = code;
            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
            }
        });
    }

    private void executeServiceRequest(Runnable runnable) {
        if (mIsServiceConnected) {
            runnable.run();
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable);
        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please update your app's public key at: "
                    + "BASE_64_ENCODED_PUBLIC_KEY");
        }

        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(TAG, "verifyValidSignature:got an exception trying to validate a purchase: " + e);
            LogFileUtil.log2File("pay.log","pay_backup.log", "[googlePay]got an exception trying to validate a purchase: " + e);
            return false;
        }
    }
}

