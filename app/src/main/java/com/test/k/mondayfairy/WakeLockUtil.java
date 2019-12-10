package com.test.k.mondayfairy;

import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

public class WakeLockUtil {
    private static PowerManager.WakeLock mCpuWakeLock;
    static void acquireCpuWakeLock(Context context){
        if(mCpuWakeLock != null) return;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getClass().getSimpleName() );
        mCpuWakeLock.acquire();
    }
    static void releaseCpuWakeLock(){
        if(mCpuWakeLock != null){
            mCpuWakeLock.release();
            mCpuWakeLock = null;
        }
    }
}
