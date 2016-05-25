package org.android.util;

import java.io.File;

/**
 * Created by joe on 16/5/18.
 */
public class DirUtil {
    public static boolean create(String fullDirName){
        File file=new File(fullDirName);
        return file.mkdir();
    }

    public static boolean exists(String fullDirName){
        File file=new File(fullDirName);
        return file.exists();
    }

    public static boolean del(String fullDirName){
        File file=new File(fullDirName);
        return file.delete();
    }

    public static boolean rename(String fullDirName1,String fullDirName2){
        File file1=new File(fullDirName1);
        File file2=new File(fullDirName2);
        return file1.renameTo(file2);
    }

    public static String[] list(String fullDirName){
        File file=new File(fullDirName);
        return file.list();
    }
}
