package com.test.k.mondayfairy.fragment;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import com.test.k.mondayfairy.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AppInformationDialog extends DialogFragment  {

    public static String TAG = "AppInformationDialog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_app_information_dialog, container, false);
        Toolbar toolbar = view.findViewById(R.id.dialog_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setTitle("설명서");
        setCancelable(false);
        TextView contentMessageView = view.findViewById(R.id.text_message_dialog_view);
        TextView copyrightTextView = view.findViewById(R.id.text_copyright_dialog_view);
        readTextFile(contentMessageView,"file_information");
        readTextFile(copyrightTextView,"file_copyright");
        return view;
    }
    private void readTextFile(TextView textView, String filePath){
        int resID = getResources().getIdentifier(filePath, "raw", this.getContext().getPackageName());
        InputStream inputStream = getResources().openRawResource(resID);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        try {
            while (( line = bufferedReader.readLine()) != null) {
                textView.append(line);
                Log.d(TAG,line);
                textView.append("\n");
            }
            Log.d(TAG,textView.getText().toString());
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            Log.d(TAG,"읽기 실패");
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}

