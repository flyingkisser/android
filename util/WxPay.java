package org.android.util;

import java.util.TreeMap;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Created by joe on 16/8/10.
 */
public class WxPay {
    public String _appid;
    public String _mch_id;
    public String _key;
    public String _activity_classname;
    public String _jsCallBack;
    public static WxPay _instance=null;
    public IWXAPI _wxapi=null;
    
    public static WxPay getInstance (){
    	return _instance;
    }
    
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
        WxPay.getInstance()._appid=appid;
        WxPay.getInstance()._mch_id=mchid;
        WxPay.getInstance()._key=key;
        WxPay.getInstance()._activity_classname=fullActivityClassNameWithPkg;

        LogUtil.d("WxPay.Init:pkgName %s appid %s mchid %s key %s", fullActivityClassNameWithPkg,appid,mchid,key);
        if(getInstance()._wxapi.registerApp(appid))
        	LogUtil.d("WxPay.Init:_wxapi.registerApp ok");
        else {
        	LogUtil.d("WxPay.Init:_wxapi.registerApp failed");
		}
    }

    @SuppressLint("DefaultLocale")
	private String _sign(TreeMap<String, String> value){
        String stringA="";
        for(String key:value.keySet() ){
            stringA+=key+"="+value.get(key)+"&";
        }
        String stringSignTemp=stringA+"key="+_key;
        return StringUtil.md5(stringSignTemp).toUpperCase();

    }

    //弹出微信支付的界面，完成微信内支付流程
    public static void Pay(String prepayID,String jsCallBack){
        if(getInstance()==null) {
            LogUtil.d("WxPay.Pay:getInstance is null,you need call init method first!");
            return;
        }
        getInstance()._jsCallBack=jsCallBack;
        
        PayReq request = new PayReq();
        request.appId = getInstance()._appid;
        request.partnerId = getInstance()._mch_id;
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
        
        if(getInstance()._wxapi.sendReq(request)){
            LogUtil.d("WxPay.Pay:sendReq with prepayid %s ok",prepayID);
        }else{
            LogUtil.e("WxPay.Pay:sendReq with prepayid %s failed!!!",prepayID);
        }

    }
    
    public static boolean isWxInstalled(){
    	if(getInstance()==null) {
            LogUtil.d("WxPay.isWxInstalled:getInstance is null,you need call init method first!");
            return false;
        }
    	
    	if(getInstance()._wxapi.isWXAppInstalled()){
    		LogUtil.d("WxPay.isWxInstalled:wx is installed!");
    		return true;
    	}else{
    		LogUtil.d("WxPay.isWxInstalled:wx is not installed!");
    		return false;
    	}
    }
}
