package org.android.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by joe on 16/5/18.
 */
public class FileUtil {
    public static byte[] readFile(String fullFileName){
        try {
            FileInputStream f1in = new FileInputStream(fullFileName);
            int nSize = f1in.available();
            byte[] bufRead = new byte[nSize];
            int nRead = f1in.read(bufRead);
            while (nRead < nSize) {
                 nRead += f1in.read(bufRead);
            }
            return  bufRead;
        }
        catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    //读/data/data/<package name>/files目录里的文件
    public static byte[] readFileInContext(Context context,String fullFileName){
        try{
            FileInputStream file = context.openFileInput(fullFileName);
            int nSize = file.available();
            byte[] bufRead = new byte[nSize];
            int nRead = file.read(bufRead);
            return bufRead;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] readFileInResRaw(Context context,int id){
        try{
            InputStream file = context.getResources().openRawResource(id);
            int nSize = file.available();
            byte[] bufRead = new byte[nSize];
            int nRead = file.read(bufRead);
            return bufRead;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] readFileInAssets(Context context,String fileName){
        try{
            InputStream file = context.getAssets().open(fileName);
            int nSize = file.available();
            byte[] bufRead = new byte[nSize];
            int nRead = file.read(bufRead);
            return bufRead;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeFile(String fullFileName,byte[] content){
        try {
            FileOutputStream f1 = new FileOutputStream(fullFileName);
            f1.write(content);
        } catch (Exception e){
            e.printStackTrace();
            return  false;
        }
        return  true;
    }

    //把文件写进/data/data/<package name>/files目录里
    public static boolean writeFileInContext(Context context,String fullFileName, byte[] content){
        try{
            FileOutputStream f2 = context.openFileOutput(fullFileName, context.MODE_PRIVATE);
            f2.write(content);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    public static int getSize(String fullFileName){
        try {
            FileInputStream f1in = new FileInputStream(fullFileName);
            return f1in.available();
        }catch (Exception e){
            e.printStackTrace();
            return  0;
        }
    }

    public static boolean delete(String fullFileName){
        File f=new File(fullFileName);
        return f.delete();
    }

    public static boolean copyFile(String fullFileNameSrc,String fullFileNameDst){
        byte[] content=readFile(fullFileNameSrc);
        if(content==null)
            return  false;
        return writeFile(fullFileNameDst,content);
    }

    public static boolean rename(String fullFileNameSrc,String fullFileNameDst){
        File f1=new File(fullFileNameSrc);
        File f2=new File(fullFileNameDst);
        return f1.renameTo(f2);
    }
}
