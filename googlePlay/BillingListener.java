//
//package org.android.googlePlay;
//
//import android.app.Activity;
//import android.util.Log;
//import com.android.billingclient.api.BillingClient.BillingResponse;
//import com.android.billingclient.api.Purchase;
//
//import java.util.List;
//
////Handler to billing updates
//public class BillingListener implements BillingManager.BillingUpdatesListener {
//    public Activity m_activity;
//    public static String TAG="BillingListener";
//
//    public BillingListener(Activity a){
//        m_activity=a;
//    }
//
//    @Override
//    //与google play store的连接初始化完成
//    public void onBillingClientSetupFinished() {
//        Log.d(TAG, "onBillingClientSetupFinished");
//       // m_activity.onBillingManagerSetupFinished();
//    }
//
//    @Override
//    //道具消耗完成的回调
//    public void onConsumeFinished(String token, @BillingResponse int result) {
//        Log.d(TAG, "onConsumeFinished:Purchase token: " + token + ", result: " + result);
//
//        // Note: We know this is the SKU_GAS, because it's the only one we consume, so we don't
//        // check if token corresponding to the expected sku was consumed.
//        // If you have more than one sku, you probably need to validate that the token matches
//        // the SKU you expect.
//        // It could be done by maintaining a map (updating it every time you call consumeAsync)
//        // of all tokens into SKUs which were scheduled to be consumed and then looking through
//        // it here to check which SKU corresponds to a consumed token.
//        if (result == BillingResponse.OK) {
//            // Successfully consumed, so we apply the effects of the item in our
//            // game world's logic, which in our case means filling the gas tank a bit
//            Log.d(TAG, "onConsumeFinished:Consumption successful. Provisioning.");
//            //mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
//            //saveData();
//            //m_activity.alert(R.string.alert_fill_gas, result);
//        } else {
//            Log.d(TAG, "onConsumeFinished:Consumption failed "+result);
//            //m_activity.alert(R.string.alert_error_consuming, result);
//        }
//
//        //m_activity.showRefreshedUi();
//        Log.d(TAG, "onConsumeFinished:end");
//    }
//
//    @Override
//    //已经购买的但没有消耗的sku，或者订阅，在这里进行处理
//    public void onPurchasesUpdated(List<Purchase> purchaseList) {
//        // mGoldMonthly = false;
//        // mGoldYearly = false;
//
//        for (Purchase purchase : purchaseList) {
//
//           Log.d(TAG, "onPurchasesUpdated:"+purchase.getSku());
//            //((AppActivity)m_activity).getBillingManager().consumeAsync(purchase.getPurchaseToken());
//
//
//            // switch (purchase.getSku()) {
//            //     case PremiumDelegate.SKU_ID:
//            //         Log.d(TAG, "You are Premium! Congratulations!!!");
//            //         mIsPremium = true;
//            //         break;
//            //     case GasDelegate.SKU_ID:
//            //         Log.d(TAG, "We have gas. Consuming it.");
//            //         // We should consume the purchase and fill up the tank once it was consumed
//            //         m_billingMananger.consumeAsync(purchase.getPurchaseToken());
//            //         break;
//            //     case GoldMonthlyDelegate.SKU_ID:
//            //         mGoldMonthly = true;
//            //         break;
//            //     case GoldYearlyDelegate.SKU_ID:
//            //         mGoldYearly = true;
//            //         break;
//            // }
//        }
//
//        //m_activity.showRefreshedUi();
//    }
//}
//
