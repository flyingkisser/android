// package org.android.adjust;

// import android.app.Activity;

// import com.adjust.sdk.Adjust;
// import com.adjust.sdk.AdjustEvent;
// import com.adjust.sdk.AdjustConfig;
// import com.appsflyer.AFLogger;

// public class WAdjust {
//     public  static Activity _activity;
//     public static boolean mInit=false;

//     public static void Init(Activity a,String apiKey){
//         _activity=a;
//         String appToken = apiKey;
//         String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
//         AdjustConfig config = new AdjustConfig(_activity, appToken, environment);

// //        config.setLogLevel(Logger.LogLevel.VERBOSE); // enable all logs

//         Adjust.onCreate(config);
//         mInit=true;

// //        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
//     }

// //    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
// //        @Override
// //        public void onActivityResumed(Activity activity) {
// //            Adjust.onResume();
// //        }
// //
// //        @Override
// //        public void onActivityPaused(Activity activity) {
// //            Adjust.onPause();
// //        }
// //
// //        //...
// //    }

//     public static void onResume(){
//         if(mInit)
//             Adjust.onResume();
//     }

//     public static void onPause(){
//         if(mInit)
//             Adjust.onPause();
//     }

//     public static void SetPushToken(String token){
//         if(mInit)
//             Adjust.setPushToken(token, _activity.getApplicationContext());
//     }

//     public static void LogEventByString(String key){
//         AdjustEvent adjustEvent = new AdjustEvent(key);
//         Adjust.trackEvent(adjustEvent);
//     }

//     public  static void LogRevenue(String skuName,String currency,float price,String tab,String orderID){
//         AdjustEvent adjustEvent = new AdjustEvent(skuName);
//         adjustEvent.setRevenue(price, currency);
//         adjustEvent.setOrderId(orderID);
//         Adjust.trackEvent(adjustEvent);
//     }
// }

// //    public static void EnableDeepLink(){
// //
// //    }
// //    public static void SetUID(int uid){
// //
// //    }
// //    public static void EnableTrack(String key,boolean bDebug){
// //
// //    }
// //    public static void LogEventByMap(String key, Map<String,Object> valueMap){
// //
// //    }
