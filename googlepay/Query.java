package org.android.googlepay;

/**
 * Created by joe on 2018/7/13.
 */

public class Query {
    /**
     * 初始化IabHelper
     */
    private void setupIabHelper(String base64EncodedPublicKey) {
        // showLog("base64EncodedPublicKey--------" + base64EncodedPublicKey);
        // iabHelper = new IabHelper(this, base64EncodedPublicKey);
        // iabHelper.enableDebugLogging(true);
        // iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        //     @Override
        //     public void onIabSetupFinished(IabResult result) {

        //         showLog("Setup finished----------");

        //         if (!result.isSuccess()) {
        //             // Oh noes, there was a problem.
        //             alert("Problem setting up in-app billing: " + result);
        //             return;
        //         }

        //         // Have we been disposed of in the meantime? If so, quit.
        //         if (iabHelper == null) return;

        //         // IAB is fully set up. Now, let's get an inventory of stuff we own.
        //         showLog("Setup successful. Querying inventory");
        //         List<String> additionalSkuList = new ArrayList();
        //         additionalSkuList.add(SKU_80);
        //         additionalSkuList.add(SKU_500);
        //         additionalSkuList.add(SKU_1200);
        //         additionalSkuList.add(SKU_2500);
        //         additionalSkuList.add(SKU_6500);
        //         additionalSkuList.add(SKU_14000);
        //         iabHelper.queryInventoryAsync(true, additionalSkuList, queryInventoryFinishedListener);
        //     }
        // });
    }
}
