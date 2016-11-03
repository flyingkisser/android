package org.android.util;

import java.util.TreeMap;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.Context;
import bbwordruntime.bbdc.com.wxapi.WXPayEntryActivity;
import org.android.util.LogUtil;

/**
 * Created by joe on 16/8/10.
 */
public class WxPay {
    public static String _appid;
    public static String _mch_id;
    public static String _key;
    public static String _activity_classname;
    public static WxPay _instance=null;
    public static IWXAPI _wxapi=null;
    public WxPay(String contextClassName){
        try {
            Context c=(Context)Class.forName(contextClassName).getMethod("getContext",new Class[]{}).invoke("",new Object[]{});
            _wxapi = WXAPIFactory.createWXAPI(c, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //fullActivityClassNameWithPkg:for exp, com.bbdc.test1.MainActivity
    public static void Init(String fullActivityClassNameWithPkg,String appid,String mchid,String key){
        if(_instance==null)
            _instance=new WxPay(fullActivityClassNameWithPkg);
        _appid=appid;
        _mch_id=mchid;
        _key=key;
        _activity_classname=fullActivityClassNameWithPkg;

        LogUtil.d("WxPay.Init:pkgName %s appid %s mchid %s key %s", fullActivityClassNameWithPkg,appid,mchid,key);
        if(_wxapi.registerApp(_appid))
        	LogUtil.d("_wxapi.registerApp ok");
        else {
        	LogUtil.d("_wxapi.registerApp failed");
		}
    }

    public static WxPay getInstance() {
        return _instance;
    }

    private String _sign(TreeMap<String, String> value){
        String stringA="";
        for(String key:value.keySet() ){
            stringA+=key+"="+value.get(key)+"&";
        }
        String stringSignTemp=stringA+"key="+_key;
        return StringUtil.md5(stringSignTemp).toUpperCase();

    }
    
    public static int isNeedCheckPayState(){
    	return WXPayEntryActivity.getState();
    	//return 0;
    }

    //弹出微信支付的界面，完成微信内支付流程
    public static void Pay(String prepayID){
        if(getInstance()==null) {
            LogUtil.d("WxPay.Pay:getInstance is null,you need call init method first!");
            return;
        }
        PayReq request = new PayReq();
        request.appId = _appid;
        request.partnerId = _mch_id;
        request.prepayId= prepayID;
        request.packageValue = "Sign=WXPay";
        request.nonceStr= RandUtil.getRandStr(20);
        request.timeStamp=String.valueOf(TimeUtil.getTime());
       
        TreeMap<String, String> forSignMap=new TreeMap<String, String>();
        forSignMap.put("appid", request.appId);
        forSignMap.put("partnerid", request.partnerId);
        forSignMap.put("prepayid", request.prepayId);
        forSignMap.put("package", request.packageValue);
        forSignMap.put("noncestr", request.nonceStr);
        forSignMap.put("timestamp", request.timeStamp);
       
        request.sign= getInstance()._sign(forSignMap);

        WXPayEntryActivity.resetState();
        
        if(_wxapi.sendReq(request)){
            LogUtil.d("WxPay.Pay:sendReq with prepayid %s ok",prepayID);
        }else{
            LogUtil.e("WxPay.Pay:sendReq with prepayid %s failed!!!",prepayID);
        }

    }
    
    public static boolean isWxInstalled(){
    	if(getInstance()==null) {
            LogUtil.d("WxPay.Pay:getInstance is null,you need call init method first!");
            return false;
        }
    	
    	if(_wxapi.isWXAppInstalled()){
    		LogUtil.d("wx is installed!");
    		return true;
    	}else{
    		LogUtil.d("wx is not installed!");
    		return false;
    	}
    }
}
