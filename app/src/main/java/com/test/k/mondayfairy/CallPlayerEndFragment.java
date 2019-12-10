package com.test.k.mondayfairy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class CallPlayerEndFragment extends Fragment implements CallPlayerActivity.OnBackPressedListener {
    private ImageView talkUserImageView;
    private TextView talkTimeTextView;
    private TextView talkUserNameView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_call_player_end, container, false);
        talkTimeTextView = view.findViewById(R.id.text_talk_time_view);
        talkUserNameView = view.findViewById(R.id.text_talk_user_name_view);
        Bundle data = this.getArguments();
        if(data != null){
            talkTimeTextView.setText(data.getString("time"));
            talkUserNameView.setText(data.getString("name"));
            Bitmap userPicture = data.getParcelable("picture");
            talkUserImageView.setImageBitmap(userPicture);
        }
        return view;
    }
    //뒤로가기 버튼을 누르면 앱을 종료한다
    @Override
    public void onBack() {
        //Log.e(TAG, "onBack()");
        // 리스너를 설정하기 위해 Activity 를 받아옵니다.
        CallPlayerActivity activity = (CallPlayerActivity)getActivity();
        activity.onBackPressed();
    }

    // Fragment 호출 시 반드시 호출되는 오버라이드 메소드입니다.
    @Override
    //혹시 Context 로 안되시는분은 Activity 로 바꿔보시기 바랍니다.
    public void onAttach(Context context) {
        super.onAttach(context);
        //Log.e(TAG, "onAttach()");
        ((CallPlayerActivity)context).setOnBackPressedListener(this);
    }
}
