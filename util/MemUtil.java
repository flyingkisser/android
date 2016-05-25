package org.android.util;

/**
 * Created by joe on 16/5/18.
 */
public class MemUtil {
    private static final Runtime s_runtime = Runtime.getRuntime();


    private static long usedMemory ()
    {
        return s_runtime.totalMemory () - s_runtime.freeMemory ();
    }

    public static void printMem(){
        //	runGC();
        long num=usedMemory()/1024/1024;
        System.out.printf("memory used is "+num+"M");

    }

    public  static void runGC ()
    {
        try{
            long usedMem1 = usedMemory (), usedMem2 = Long.MAX_VALUE;
            for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++ i)
            {
                s_runtime.runFinalization ();
                s_runtime.gc ();
                //Thread.currentThread ().yield ();
                //usedMem2 = usedMem1;
                //usedMem1 = usedMemory ();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null) {
                for (int i = 0; i < stackElements.length; i++)
                    LogUtil.e(stackElements[i].toString());
            }
        }
    }
}
