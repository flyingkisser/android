package org.android.util;

import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.TreeMap;

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
    public static void init(String fullActivityClassNameWithPkg,String appid,String mchid,String key){
        if(_instance==null)
            _instance=new WxPay(fullActivityClassNameWithPkg);
        _appid=appid;
        _mch_id=mchid;
        _key=key;
        _activity_classname=fullActivityClassNameWithPkg;

        _wxapi.registerApp(_appid);
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
        forSignMap.put("appId", request.appId);
        forSignMap.put("partnerId", request.partnerId);
        forSignMap.put("prepayId", request.prepayId);
        forSignMap.put("packageValue", request.packageValue);
        forSignMap.put("nonceStr", request.nonceStr);
        forSignMap.put("timeStamp", request.timeStamp);
        request.sign= getInstance()._sign(forSignMap);

        if(_wxapi.sendReq(request)){
            LogUtil.d("WxPay.Pay:sendReq with prepayid %s ok",prepayID);
        }else{
            LogUtil.e("WxPay.Pay:sendReq with prepayid %s failed!!!",prepayID);
        }

    }
}
