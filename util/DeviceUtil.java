package org.android.util;

import android.annotation.TargetApi;  
import android.app.ActivityManager;  
import android.content.Context;  
import android.os.Build;
import java.io.*;
/**
 * Created by joe on 18/6/26.
 */
public class DeviceUtil {
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
   
}
