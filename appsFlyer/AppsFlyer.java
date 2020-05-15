package org.android.appsFlyer;

import android.app.Activity;
import android.util.Log;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.*;

import java.util.HashMap;
import java.util.Map;

public class AppsFlyer {
    public  static Activity _activity;

    public static String _strInstallConversionData =  "";
    public static int sessionCount = 0;
    public static void setInstallData(Map<String, String> conversionData){
        if(sessionCount == 0){
            final String install_type = "Install Type: " + conversionData.get("af_status") + "\n";
            final String media_source = "Media Source: " + conversionData.get("media_source") + "\n";
            final String install_time = "Install Time(GMT): " + conversionData.get("install_time") + "\n";
            final String click_time = "Click Time(GMT): " + conversionData.get("click_time") + "\n";
            final String is_first_launch = "Is First Launch: " + conversionData.get("is_first_launch") + "\n";
            _strInstallConversionData += install_type + media_source + install_time + click_time + is_first_launch;
            sessionCount++;
        }
    }

    public static String GetInstallationData(){
        return  _strInstallConversionData;
    }
    public static String GetAppsFlyerUID(){
        return AppsFlyerLib.getInstance().getAppsFlyerUID(_activity);
    }

    public static void Init(Activity a,String apiKey ,String channel){
        _activity=a;
        if(channel.length()>0){
            AppsFlyer.SetPublisher(channel);
        }
        AppsFlyer.EnableTrack(apiKey,false);
    }

    public static void SetUID(int uid){
        AppsFlyerLib.getInstance().setCustomerUserId(String.valueOf(uid));
    }

    public static void SetPublisher(String channel){
        AppsFlyerLib.getInstance().setOutOfStore(channel);
    }

    public static void EnableTrack(String key,boolean bDebug){
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            /* Returns the attribution data. Note - the same conversion data is returned every time per install */
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    //Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
                setInstallData(conversionData);
            }

            @Override
            public void onInstallConversionFailure(String errorMessage) {
                //Log.d(AppsFlyerLib.LOG_TAG, "error getting conversion data: " + errorMessage);
            }

            /* Called only when a Deep Link is opened */
            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    //Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                //Log.d(AppsFlyerLib.LOG_TAG, "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init(key, conversionListener, _activity.getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(_activity.getApplication(),key);
        AppsFlyerLib.getInstance().setDebugLog(bDebug);
    }

    public static void LogEventByString(String key, String value){
        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("value",value);
        AppsFlyerLib.getInstance().trackEvent(_activity.getApplicationContext(), key, eventMap);
    }

    public static void LogEventByMap(String key, Map<String,Object> valueMap){
        AppsFlyerLib.getInstance().trackEvent(_activity.getApplicationContext(), key, valueMap);
    }

    public  static void LogRevenue(String skuName,String currency,float price,String tab,String orderID){
        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put(AFInAppEventParameterName.REVENUE,price);
        eventMap.put(AFInAppEventParameterName.CONTENT_TYPE,tab);
        eventMap.put(AFInAppEventParameterName.CONTENT_ID,skuName);
        eventMap.put(AFInAppEventParameterName.CURRENCY,currency);
        eventMap.put(AFInAppEventParameterName.DESCRIPTION,orderID);
        AppsFlyerLib.getInstance().trackEvent(_activity.getApplicationContext(), AFInAppEventType.PURCHASE, eventMap);
    }

    public static void EnableDeepLink(){
        AppsFlyerLib.getInstance().sendDeepLinkData(_activity);
    }

}
