package org.android.util;

import com.squareup.okhttp.internal.framed.Variant;

/**
 * Created by joe on 16/5/18.
 */
public class RandUtil {
    static long seed = 1988;

    static int m = 1 << 30;
    static int a = 214013;
    static int c = 2531011;

    public static void seed(int s)
    {
        seed = s;
    }

    public static int rand()
    {
        seed = (seed * a + c) % m;
        return (int)seed;
    }

    public static int rand(int min, int max)
    {
        if (max <= min)
            return max;

        return rand() % (max - min) + min;
    }

    public static float randf()
    {
        return ((float)rand())/m;
    }
    
    public static String getRandStr(int len){
    	if(len==0)
    		len=32;
    	String chars="ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
    	int maxPos=chars.length();
    	String pwd="";
    	for(int i=0;i<len;i++){
    		pwd+=chars.indexOf(rand(0,maxPos-1));
    	}
    	return pwd;
    }
}
