package org.android.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by joe on 20/5/8.
 */
public class LogFileUtil {
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss::SSS", Locale.SIMPLIFIED_CHINESE);
    public static String _rootPath;

    private LogFileUtil() {
    }

    public static void setLogRootPath(String rootPath) {
        _rootPath=rootPath;
    }

    public static void log2File(String fileName1,String fileName2,String fmt,Object...args) {
        if(_rootPath==null)
            return;
        String outString=String.format(fmt, args);
        //Log.d("cocos2d-x", outString);
        save(_rootPath+fileName1,outString);
        save(_rootPath+fileName2,outString);
    }

    public static void save(String path, String msg) {
        String time = formatter.format(new Date());
        File file = new File(path);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(time + " " + msg + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}