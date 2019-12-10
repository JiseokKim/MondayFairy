package com.test.k.mondayfairy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.CompoundButton;
import android.widget.GridLayout;

import androidx.appcompat.widget.AppCompatCheckBox;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class MonDayFairyPicker extends GridLayout implements CompoundButton.OnCheckedChangeListener {
    private final int NUMBER_OF_MEMBER = 12;
    AppCompatCheckBox[] memberCheckedBox = new AppCompatCheckBox[NUMBER_OF_MEMBER + 1];
    int total = 14;
    int column = 2;
    int row = total / column;

    public MonDayFairyPicker(Context context) {
        super(context);
        initLayout(context);
    }

    public MonDayFairyPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public MonDayFairyPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    public void initLayout(Context context) {
        float textSize = 24;
        this.setColumnCount(column);
        this.setRowCount(row + 1);
        Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        Resources res = getResources();
        String[] names = res.getStringArray(R.array.array_member_names);
        int checkBoxSize = memberCheckedBox.length;
        for (int i = 0; i < checkBoxSize; i++) {
            memberCheckedBox[i] = new AppCompatCheckBox(this.getContext());
            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colspan);
            memberCheckedBox[i].setText(names[i]);
            memberCheckedBox[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            memberCheckedBox[i].setOnCheckedChangeListener(this);
            memberCheckedBox[i].setId(i);
            this.addView(memberCheckedBox[i], gridParam);
        }
        checkedPickerMember(context);
    }

    //설정에서 사용자가 선택한 멤버들을 체크박스에 표시한다
    private void checkedPickerMember(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("setting", MODE_PRIVATE);
        if (preferences.contains("member")) {
            Set<String> memberSet = preferences.getStringSet("member", new HashSet<String>());
            int checkBoxSize = memberCheckedBox.length;
            for(int i = 0; i< checkBoxSize; i++){
                //사용자가 과거에 체크했던 멤버들은 체크박스에 표시
                if(memberSet.contains(memberCheckedBox[i].getText().toString())){
                    memberCheckedBox[i].setChecked(true);
                }
            }
        }
    }
    //사용자가 체크박스에서 새로 선택한 멤버들을 가져온다
    public Set<String> getPickerChecked() {
        Set<String> memberSet = new HashSet<String>();
        int checkBoxSize = memberCheckedBox.length;
        for (int i = 0; i < checkBoxSize; i++) {
            if (memberCheckedBox[i].isChecked()) {
                memberSet.add(memberCheckedBox[i].getText().toString());
            }
        }
        return memberSet;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int checkBoxSize = memberCheckedBox.length;
        //사용자가 모두를 선택하고 나머지에 체크표시를 하면 모두 체크표시를 무효화한다
        if (!(buttonView.getId() == memberCheckedBox[checkBoxSize - 1].getId())) {
            if (isChecked) {
                memberCheckedBox[checkBoxSize - 1].setChecked(false);
            }
        } else {
            //사용자가 모두를 선택했을경우 나머지 체크표시를 무효화한다
            if (isChecked) {
                for (int i = 0; i < checkBoxSize - 1; i++) {
                    memberCheckedBox[i].setChecked(false);
                }
            }
        }
    }
}
