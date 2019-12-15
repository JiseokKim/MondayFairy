package com.test.k.mondayfairy;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.test.k.mondayfairy.activity.CallPlayerActivity;
import com.test.k.mondayfairy.manager.CallAlarmManager;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlramReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d("TAG","Alarm Receive");
            WakeLockUtil.acquireCpuWakeLock(context);
            if (intent!=null&& Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
                //재부팅을 하면 알람 설정이 지워지므로 다시 알람을 재설정해준다
                Log.d("TAG","Alarm Receive Boot");
                SharedPreferences preferences = context.getSharedPreferences("setting", MODE_PRIVATE);
                CallAlarmManager callAlarm = new CallAlarmManager(context);
                callAlarm.setArlarm(preferences);
            }else {
                intent = new Intent(context, CallPlayerActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                pendingIntent.send();
            }
            Log.d("TAG","Alarm Receive success");

        } catch (PendingIntent.CanceledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (NullPointerException ne){
            ne.printStackTrace();
        }
    }

}
