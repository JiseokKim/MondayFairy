package com.test.k.mondayfairy.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.test.k.mondayfairy.R;
import com.test.k.mondayfairy.activity.CallPlayerActivity;


public class CallPlayerEndFragment extends Fragment implements CallPlayerActivity.OnBackPressedListener {
    private final String TAG = getClass().getSimpleName();
    private ImageView talkUserImageView;
    private TextView talkTimeTextView;
    private TextView talkUserNameView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_call_player_end, container, false);
        talkTimeTextView = view.findViewById(R.id.text_talk_time_view);
        talkUserNameView = view.findViewById(R.id.text_talk_user_name_view);
        talkUserImageView = view.findViewById(R.id.image_talk_user_view);
        Bundle data = this.getArguments();
        if(data != null){
            talkTimeTextView.setText(data.getString("time"));
            talkUserNameView.setText(data.getString("name"));
            Bitmap userPicture = data.getParcelable("picture");
            Log.d(TAG,"picture:"+userPicture.getByteCount());
            talkUserImageView.setImageBitmap(userPicture);
            LinearLayout background = view.findViewById(R.id.layout_call_end_text);
            background.setBackgroundColor(setBackgroundColor(data.getString("name")));
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
            talkTimeTextView.startAnimation(anim);
        }
        return view;
    }
    private int setBackgroundColor(String name){
        int color = 0;
        switch (name){
            case "워뇽이"://워뇽이
                color = getResources().getColor(R.color.wonwyoung);
                break;
            case "꾸라"://꾸라
                color = getResources().getColor(R.color.sakura);
                break;
            case "조댕찌"://조댕찌
                color = getResources().getColor(R.color.yuri);
                break;
            case "최오리"://최오리
                color = getResources().getColor(R.color.yena);
                break;
            case "안유댕"://안유댕
                color = getResources().getColor(R.color.yujin);
                break;
            case "나코나코땅땅"://나코나코땅땅
                color = getResources().getColor(R.color.nako);
                break;
            case "권땡모"://권땡모
                color = getResources().getColor(R.color.eunbi);
                break;
            case "강광배"://강광배
                color = getResources().getColor(R.color.hyewon);
                break;
            case "히짱"://히짱
                color = getResources().getColor(R.color.hitomi);
                break;
            case "자몽워니"://자몽워니
                color = getResources().getColor(R.color.chaewon);
                break;
            case "밍주가또오~!"://밍주가또오~!
                color = getResources().getColor(R.color.minju);
                talkUserNameView.setTextColor(Color.GRAY);
                talkTimeTextView.setTextColor(Color.GRAY);
                break;
            case "이째욘"://이째욘
                color = getResources().getColor(R.color.chaeyeon);
                break;
        }
        return color;
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
