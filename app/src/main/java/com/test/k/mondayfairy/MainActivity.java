package com.test.k.mondayfairy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private MonDayFairyPicker monDayFairyPicker;
    private int[] dateValues = new int[3];
    private int[] timeValues = new int[2];
    private TextView datePickerValueTextView;
    private TextView timePickerValueTextView;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);
        preferences = getSharedPreferences("setting", MODE_PRIVATE);
        this.showDatePickerDialog();
        this.showTimePickerDialog();
        monDayFairyPicker = findViewById(R.id.member_pick_check);
        Button saveBtn = findViewById(R.id.btn_setting_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자 설정 값 모두 저장하고
                //설정값 중 날짜, 시간 값으로 알람매니저설정.
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("year",dateValues[0]);//year
                editor.putInt("month",dateValues[1]);//month
                editor.putInt("day",dateValues[2]);//day
                editor.putInt("hour",timeValues[0]);//hour
                editor.putInt("min",timeValues[1]);//min
                editor.putStringSet("member",monDayFairyPicker.getPickerChecked());//pickup member
                editor.apply();
                setCallingAlram(preferences);
                createSaveConfirmDialog();
            }
        });
    }
    public void setCallingAlram(SharedPreferences sharedPreferences){
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

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
        //사용자가 설정을 완료한 시각
        long currentTime = System.currentTimeMillis();
        //if(currentTime<=calendar.getTimeInMillis()) {//현재 시스템 시간이 사용자가 설정한 알람 시간보다 미래일때 알람을 설정한다
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        //}

    }
    public void createSaveConfirmDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.save_confirm_dialog_title)
                .setMessage(R.string.save_confirm_dialog_message)
                .setPositiveButton(R.string.positive_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    // Create and show a DatePickerDialog when click button.
    private void showDatePickerDialog(){
        //설정값이 있을 경우와 없을경우
        datePickerValueTextView = findViewById(R.id.date_picker_value);
        if(preferences.contains("year")) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append(preferences.getInt("year", 0));
            strBuf.append("-");
            strBuf.append(preferences.getInt("month", 0) + 1);
            strBuf.append("-");
            strBuf.append(preferences.getInt("day", 0));
            datePickerValueTextView.setText(strBuf.toString());
        }
        // Get open DatePickerDialog button.
        Button datePickerDialogButton = findViewById(R.id.btn_date_picker);
        datePickerDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        StringBuffer strBuf = new StringBuffer();
                        //strBuf.append("You select date is ");
                        strBuf.append(year);
                        strBuf.append("-");
                        strBuf.append(month+1);
                        strBuf.append("-");
                        strBuf.append(dayOfMonth);
                        dateValues[0] = year;
                        dateValues[1] = month;
                        dateValues[2] = dayOfMonth;
                        datePickerValueTextView.setText(strBuf.toString());
                    }
                };

                // Get current year, month and day.
                Calendar now = Calendar.getInstance();
                int year = now.get(java.util.Calendar.YEAR);
                int month = now.get(java.util.Calendar.MONTH);
                int day = now.get(java.util.Calendar.DAY_OF_MONTH);

                // Create the new DatePickerDialog instance.
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, onDateSetListener, year, month, day);

                // Set dialog icon and title.
                //datePickerDialog.setIcon(R.drawable.if_snowman);
                datePickerDialog.setTitle("Please select date.");

                // Popup the dialog.
                datePickerDialog.show();
            }
        });
    }

    // Create and show a TimePickerDialog when click button.
    private void showTimePickerDialog(){
        //설정값이 있을 경우와 없을경우
        timePickerValueTextView = findViewById(R.id.time_picker_value);
        if(preferences.contains("hour")) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append(preferences.getInt("hour", 0));
            strBuf.append(":");
            //분(minute)의 자릿수는 십의 자릿수로 맞춰준다
            if(preferences.getInt("min", 0)<10){
                strBuf.append("0");
            }
            strBuf.append(preferences.getInt("min", 0));
            timePickerValueTextView.setText(strBuf.toString());
        }
        // Get open TimePickerDialog button.
        Button timePickerDialogButton = findViewById(R.id.btn_time_picker);
        timePickerDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        //strBuf.append("You select time is ");
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        timeValues[0] = hour;
                        timeValues[1] = minute;
                        timePickerValueTextView.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, onTimeSetListener, hour, minute, is24Hour);

               //timePickerDialog.setIcon(R.drawable.if_snowman);
                timePickerDialog.setTitle("Please select time.");

                timePickerDialog.show();
            }
        });
    }
}
