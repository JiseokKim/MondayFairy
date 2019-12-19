package com.test.k.mondayfairy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;
import com.test.k.mondayfairy.fragment.AppInformationDialog;
import com.test.k.mondayfairy.manager.CallAlarmManager;
import com.test.k.mondayfairy.MonDayFairyPicker;
import com.test.k.mondayfairy.R;

import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

/*사용자가 월요요정 알람을 설정할 수있는 액티비티 클래스*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private MonDayFairyPicker monDayFairyPicker;
    private int[] dateValues = new int[3];
    private int[] timeValues = new int[2];
    private TextView datePickerValueTextView;
    private TextView timePickerValueTextView;
    private SharedPreferences preferences;
    private MediaPlayer mediaPlayer;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, Crashlytics.getInstance());
        setContentView(R.layout.activity_alarm_setting);
        preferences = getSharedPreferences("setting", MODE_PRIVATE);
        this.showDatePickerDialog();
        this.showTimePickerDialog();
        mediaPlayer = MediaPlayer.create(this, R.raw.bgm_violeta);
        mediaPlayer.setVolume(1.0f, 1.0f);
        monDayFairyPicker = findViewById(R.id.member_pick_check);
        Button saveBtn = findViewById(R.id.btn_setting_save);
        saveBtn.setOnClickListener(this);
        Button nowCallBtn = findViewById(R.id.btn_now_fairy_call);
        nowCallBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bgm_btn:
                //버튼을 눌렀을때 재생중이면 재생중지
                if (mediaPlayer.isPlaying()) {
                    item.setIcon(R.drawable.ic_bgm_play_24dp);
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                } else {
                    item.setIcon(R.drawable.ic_bgm_pause_24dp);
                    mediaPlayer.start();
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        item.setIcon(R.drawable.ic_bgm_play_24dp);
                    }
                });
                break;
            case R.id.action_paper_btn:
                //create dialog
                AppInformationDialog dialog = new AppInformationDialog();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog.show(ft, AppInformationDialog.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean dateInvalidCheck() {
        boolean success = false;
        int year = preferences.getInt("year", 2018);
        int month = preferences.getInt("month", 10);
        int day = preferences.getInt("day", 29);
        Calendar now = Calendar.getInstance();
        Calendar inputDate = Calendar.getInstance();
        inputDate.set(year, month, day);
        //예약시간(미래or현재)-현재시간 = ?, ?가  >0 이면 미래, =0이면 현재, <0 과거시간
        long timeGap = inputDate.getTimeInMillis() - now.getTimeInMillis();
        if (timeGap < 0) {//사용자가 알람을 예약한 시간이 현재 시간보다 과거면 실패
            Log.d(TAG, "Date check:" + year + "," + month + "," + day);
            success = true;
        }
        return success;
    }

    private boolean timeInvalidCheck() {
        boolean success = false;
        int year = preferences.getInt("year", 2018);
        int month = preferences.getInt("month", 10);
        int day = preferences.getInt("day", 29);
        int hour = preferences.getInt("hour", 25);
        int min = preferences.getInt("min", 60);
        Calendar inputTime = Calendar.getInstance();
        inputTime.set(year, month, day, hour, min);
        Calendar now = Calendar.getInstance();
        long totalTime = inputTime.getTimeInMillis();
        long totalCurrentTime = now.getTimeInMillis();
        if (hour >= 25 || min >= 60) {
            success = true;
        } else if (totalCurrentTime >= totalTime) {//예약한 시간이 현재시간보다 과거면
            success = true;
        }
        return success;
    }

    public boolean createSaveConfirmDialog() {
        boolean saveSuccess = false;
        String title = getResources().getString(R.string.save_confirm_dialog_title);
        String message = getResources().getString(R.string.save_confirm_dialog_message);
        if (dateInvalidCheck()) {
            title = getResources().getString(R.string.fail_confirm_dialog_title);
            message = "날짜를 예약하지 않으셨네요." + getResources().getString(R.string.fail_confirm_dialog_message);

        } else if (timeInvalidCheck()) {
            title = getResources().getString(R.string.fail_confirm_dialog_title);
            message = "시간을 예약하지 않으셨네요." + getResources().getString(R.string.fail_confirm_dialog_message);
        } else if(monDayFairyPicker.getPickerChecked().size()<=0){//선택한 멤버가 아무도 없다면
            title = getResources().getString(R.string.fail_now_call_dialog_title);
            message = getResources().getString(R.string.fail_now_call_dialog_message);
        }else{
            saveSuccess = true;
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.positive_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        return saveSuccess;
    }
    public void createFailNowCallDialog() {
        String title = getResources().getString(R.string.fail_now_call_dialog_title);
        String message = getResources().getString(R.string.fail_now_call_dialog_message);
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.positive_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    // Create and show a DatePickerDialog when click button.
    private void showDatePickerDialog() {
        //설정값이 있을 경우와 없을경우
        datePickerValueTextView = findViewById(R.id.date_picker_value);
        if (preferences.contains("year")) {
            StringBuffer strBuf = setDateText(preferences.getInt("year", 2018),
                    preferences.getInt("month", 10),
                    preferences.getInt("day", 29));
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
                        StringBuffer strBuf = setDateText(year, month, dayOfMonth);
                        Log.d(TAG, "DatePicker select:" + year + "," + month + "," + dayOfMonth);
//                        dateValues[0] = year;
//                        dateValues[1] = month;
//                        dateValues[2] = dayOfMonth;
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

    private StringBuffer setDateText(int year, int month, int dayOfMonth) {
        dateValues[0] = year;
        dateValues[1] = month;
        dateValues[2] = dayOfMonth;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(year);
        strBuf.append("-");
        strBuf.append(month + 1);
        strBuf.append("-");
        strBuf.append(dayOfMonth);
        return strBuf;
    }

    // Create and show a TimePickerDialog when click button.
    private void showTimePickerDialog() {
        //설정값이 있을 경우와 없을경우
        timePickerValueTextView = findViewById(R.id.time_picker_value);
        if (preferences.contains("hour")) {
            StringBuffer strBuf = setTimeText(preferences.getInt("hour", 25),
                    preferences.getInt("min", 60));
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
                        StringBuffer strBuf = setTimeText(hour, minute);
//                        timeValues[0] = hour;
//                        timeValues[1] = minute;
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

    private StringBuffer setTimeText(int hour, int minute) {
        timeValues[0] = hour;
        timeValues[1] = minute;
        StringBuffer strBuf = new StringBuffer();
        //strBuf.append("You select time is ");
        if (hour < 10) {
            strBuf.append("0");
        }
        strBuf.append(hour);
        strBuf.append(":");
        if (minute < 10) {
            strBuf.append("0");
        }
        strBuf.append(minute);
        return strBuf;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_now_fairy_call:
                //사용자 설정 값 모두 저장하고
                //설정값 중 날짜, 시간 값으로 알람매니저설정.
                editor = preferences.edit();
                editor.putStringSet("member", monDayFairyPicker.getPickerChecked());//pickup member
                editor.apply();
                if(monDayFairyPicker.getPickerChecked().size()>0) {//사용자가 선택한 멤버가 있는지 확인
                    Intent callActivityIntent = new Intent(this, CallPlayerActivity.class);
                    startActivity(callActivityIntent);
                }else{//멤버선택이 안되어있을시 알림 Dialog 띄움
                    createFailNowCallDialog();
                }
                break;
            case R.id.btn_setting_save:
                //사용자 설정 값 모두 저장하고
                //설정값 중 날짜, 시간 값으로 알람매니저설정.
                editor = preferences.edit();
                editor.putInt("year", dateValues[0]);//year
                editor.putInt("month", dateValues[1]);//month
                editor.putInt("day", dateValues[2]);//day
                editor.putInt("hour", timeValues[0]);//hour
                editor.putInt("min", timeValues[1]);//min
                editor.putStringSet("member", monDayFairyPicker.getPickerChecked());//pickup member
                editor.apply();
                if (createSaveConfirmDialog()) {
                    CallAlarmManager callAlarm = new CallAlarmManager(getApplicationContext());
                    callAlarm.setArlarm(preferences);
                }
                break;
        }
    }
}
