package com.test.k.mondayfairy;

import android.content.Context;
import android.os.PowerManager;

public class WakeLockUtil {
    private static PowerManager.WakeLock mCpuWakeLock;
    static public void acquireCpuWakeLock(Context context, int flag){
        if(mCpuWakeLock != null) return;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(flag, "MondayFairy:WakeLockTag" );
        mCpuWakeLock.acquire();
    }
    static public void releaseCpuWakeLock(){
        if(mCpuWakeLock != null){
            mCpuWakeLock.release();
            mCpuWakeLock = null;
        }
    }
}
