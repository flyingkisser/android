package org.android.util;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by joe on 16/5/10.
 * useage
 *
m_t1=TimerUtil.exec(new TimerTask() {
@Override
public void run() {
LogUtil.d("run1");
}
},1000);

 */

public class TimerUtil {
    public static ScheduledThreadPoolExecutor m_timer=new ScheduledThreadPoolExecutor(10);

    //每隔durationMS秒执行一次
    public static ScheduledFuture exec(Runnable r,long durationMS){
        return  m_timer.scheduleWithFixedDelay(r,0,durationMS, TimeUnit.MILLISECONDS);
    }

    //延迟delayMS秒，但只执行一次
    public static ScheduledFuture execOnceDelay(Runnable r,long delayMS){
        return m_timer.schedule(r,delayMS,TimeUnit.MILLISECONDS);
    }

}
