package com.test.k.mondayfairy.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.test.k.mondayfairy.AlarmReceiver;

import java.util.Calendar;

public class CallAlarmManager {
    Context context;
    public CallAlarmManager(Context context){
        this.context = context;
    }
    public void setArlarm(SharedPreferences sharedPreferences){
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        int year = sharedPreferences.getInt("year", 0);
        int month = sharedPreferences.getInt("month", 0);
        int day = sharedPreferences.getInt("day", 0);
        int hour = sharedPreferences.getInt("hour", 0);
        int min = sharedPreferences.getInt("min", 0);
        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        Log.d(this.getClass().getSimpleName(),"예약시간"+calendar.getTimeInMillis());
        if(Build.VERSION.SDK_INT>=23) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }else{
            if(Build.VERSION.SDK_INT>=19){
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }else{
                alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }
        }
    }
}
