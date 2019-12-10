package com.test.k.mondayfairy;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlramReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            WakeLockUtil.acquireCpuWakeLock(context);
            Log.d("TAG","Alarm Receive");
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                // Set the alarm here.
            }
            intent = new Intent(context, CallPlayerActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            pi.send();

        } catch (PendingIntent.CanceledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
