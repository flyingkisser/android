// package org.android.flurry;

// import android.util.Log;

// import com.flurry.android.FlurryAgent;
// import com.flurry.android.FlurryEventRecordStatus;

// import org.android.util.JsonUtil;
// import org.cocos2dx.javascript.AppActivity;

// import java.util.HashMap;

// public class Flurry {
//     public static boolean mInit=false;
//     public static void Init(String apiKey,boolean bLog,boolean bEnableCatchException,String clientVersion){
//         FlurryAgent.setVersionName(clientVersion);
//         new FlurryAgent.Builder()
//                 .withLogEnabled(bLog)
//                 .withCaptureUncaughtExceptions(bEnableCatchException)
//                 .withContinueSessionMillis(10000)
//                 .withLogLevel(Log.VERBOSE)
//                 .build(AppActivity.getInstance().getApplicationContext(), apiKey);
//         mInit=true;
//     }

//     public static boolean isInit(){return mInit;}

//     public static void setUserId(String id){
//         FlurryAgent.setUserId(id);
//     }

//     public static void logEventByName(String key){
//         FlurryAgent.logEvent(key);
//     }

//     public static void logEventByJsonString(String key,String jsonStr){
//         if(jsonStr==null)
//             return;
//         HashMap<String,String> jsonMap= JsonUtil.decodeToHashMapStringString(jsonStr);
//         FlurryAgent.logEvent(key,jsonMap);
//     }

//     public static void startTime(String key){
//         FlurryAgent.logEvent(key,true);
//     }
//     public static void endTime(String key){
//         FlurryAgent.endTimedEvent(key);
//     }
//     public static void incPageView(){
//         FlurryAgent.onPageView();
//     }

//     public static void logRevenue(String productName,String skuName,int number,float price,String currency,String transactionID,String jsonStr){
//         HashMap<String,String> jsonMap;
//         if(jsonStr!=null)
//             jsonMap= JsonUtil.decodeToHashMapStringString(jsonStr);
//         else
//             jsonMap=new HashMap<>();
//         FlurryEventRecordStatus recordStatus = FlurryAgent.logPayment(productName, skuName, number, (double)price, currency, transactionID, jsonMap);
//     }

//     public static void enableReportLocation(boolean b){
//         FlurryAgent.setReportLocation(b);
//     }
// }
