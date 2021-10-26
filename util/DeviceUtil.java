package org.android.util;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.cocos2dx.javascript.AppActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by joe on 18/6/26.
 */
public class DeviceUtil {
  public static int errorReturnFromGoogle;
  public static long freeMem=0;
  public static long totalMem=0;
  public static int getNumberOfCPUCores() {  
    int cores;  
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {  
      // Gingerbread doesn't support giving a single application access to both cores, but a  
      // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core  
      // chipset and Gingerbread; that can let an app in the background run without impacting  
      // the foreground application. But for our purposes, it makes them single core.  
      return 1;  
    }  
   
    try {  
      cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;  
    } catch (SecurityException e) {  
      cores = -1;  
    } catch (NullPointerException e) {  
      cores = -1;  
    }  
    return cores;  
  }

  public static String getDeviceStr(){
    String manufacturer = android.os.Build.MANUFACTURER;
    String brand = android.os.Build.BRAND;                //厂商
    String device = android.os.Build.DEVICE;
    String model = android.os.Build.MODEL;                //型号
    String version = android.os.Build.VERSION.RELEASE;  //系统型号
    String product = android.os.Build.PRODUCT;
    String display = android.os.Build.DISPLAY;
    String finderPrint = android.os.Build.FINGERPRINT;
    String serial = android.os.Build.ID;
    //String lang = Locale.getDefault().getLanguage();
    return String.format("%s %s %s %s %s %s %s %s %s",
            manufacturer,brand,device,
            model,version,product,
            display,finderPrint,serial
            );
  }

  public static String getPhoneName(){
    return  android.os.Build.BRAND+' '+android.os.Build.MODEL;
  }

  public static String getOSVersion(){
    return  android.os.Build.VERSION.RELEASE;
  }

  public static String getLang(){
    return  Locale.getDefault().getLanguage();
  }

  public static long getTotalMem() {
    if(totalMem!=0)
      return totalMem;
    try {
        FileReader fr = new FileReader("/proc/meminfo");
        BufferedReader br = new BufferedReader(fr);
        String text = br.readLine();
        String[] array = text.split("\\s+");
        Log.w("DeviceUtil", text);
        return Long.valueOf(array[1]); //单位为KB
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return 0;
  }

  public static long getFreeMem() {
    ActivityManager manager = (ActivityManager) AppActivity.getInstance()
            .getSystemService(Activity.ACTIVITY_SERVICE);
    ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
    manager.getMemoryInfo(info);
    return info.availMem;
  }

  public static String getFreeMemString() {
    return  String.valueOf(getFreeMem());
  }

  public static String getTotalMemString() {
    return  String.valueOf(getTotalMem());
  }

  public static long setFreeMem(Context context){
    ActivityManager manager = (ActivityManager) context
            .getSystemService(Activity.ACTIVITY_SERVICE);
    ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
    manager.getMemoryInfo(info);
    freeMem=info.availMem;
    totalMem=info.totalMem;
    return freeMem;
  }

    private static final FileFilter CPU_FILTER = new FileFilter() {  
        @Override  
        public boolean accept(File pathname) {  
          String path = pathname.getName();  
          //regex is slow, so checking char by char.  
          if (path.startsWith("cpu")) {  
            for (int i = 3; i < path.length(); i++) {  
              if (path.charAt(i) < '0' || path.charAt(i) > '9') {  
                return false;  
              }  
            }  
            return true;  
          }  
          return false;  
        }  
 };

  public  static boolean isGoogleAPIAvailable(){
    //获取GoogleApiAvailability的单例
    Activity mActivity= (Activity)AppActivity.getInstance();
    GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

    //利用接口判断device是否支持Google Play Service
    int ret = googleApiAvailability.isGooglePlayServicesAvailable(mActivity);

    //支持的话， 结果将返回SUCCESS
    if (ret == ConnectionResult.SUCCESS) {
      return  true;
    }
    errorReturnFromGoogle=ret;

    //不支持时，可以利用getErrorDialog得到一个提示框, 其中第2个参数传入错误信息
    //提示框将根据错误信息，生成不同的样式
    //例如，我自己测试时，第一次Google Play Service不是最新的，
    //对话框就会显示这些信息，并提供下载更新的按键
    //googleApiAvailability.getErrorDialog(mActivity, ret, 0).show();

    mActivity.runOnUiThread(new Runnable() {
      public void run() {
        //Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show();
        GoogleApiAvailability.getInstance().getErrorDialog(
                (Activity)AppActivity.getInstance(), errorReturnFromGoogle, 0).show();
      }
    });

    return  false;
  }

  public  static boolean iSCafeBazaarAPIAvailable(){
    return  false;
//    //获取GoogleApiAvailability的单例
//    Activity mActivity= (Activity)AppActivity.getInstance();
//    GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//
//    //利用接口判断device是否支持Google Play Service
//    int ret = googleApiAvailability.isGooglePlayServicesAvailable(mActivity);
//
//    //支持的话， 结果将返回SUCCESS
//    if (ret == ConnectionResult.SUCCESS) {
//      return  true;
//    }
//    errorReturnFromGoogle=ret;
//
//    //不支持时，可以利用getErrorDialog得到一个提示框, 其中第2个参数传入错误信息
//    //提示框将根据错误信息，生成不同的样式
//    //例如，我自己测试时，第一次Google Play Service不是最新的，
//    //对话框就会显示这些信息，并提供下载更新的按键
//    //googleApiAvailability.getErrorDialog(mActivity, ret, 0).show();
//
//    mActivity.runOnUiThread(new Runnable() {
//      public void run() {
//        //Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show();
//        GoogleApiAvailability.getInstance().getErrorDialog(
//                (Activity)AppActivity.getInstance(), errorReturnFromGoogle, 0).show();
//      }
//    });
//
//    return  false;
  }

}
