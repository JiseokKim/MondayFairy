package com.test.k.mondayfairy.manager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.Keep;

import com.test.k.mondayfairy.CallUser;
import com.test.k.mondayfairy.R;

import java.lang.reflect.Field;
@Keep
public class CallUserManager {
    private final String TAG = getClass().getSimpleName();
    private CallUser callUser;
    private CallVideoManager videoManager;
    private Context context;
    public CallUserManager(Context context){
        this.context = context;
        callUser = CallUser.getInstance();
        videoManager = new CallVideoManager();
    }
    public void userInit(){
        callUser.setVideoUri(videoManager.getMemberVideoUri(context));
        String[] searchWord = videoManager.getVideoFilePath().split("_");
        //file 종류 Code: mov-전화화면이 없는동영상, call-전화용으로 편집된 동영상
        callUser.setVideoCode(searchWord[0]);
        //월요요정 이름(english)
        setCallUserName(searchWord[1]);
        //file unique ID : date
        setCallUserThumb(searchWord[2]);
    }
    //알파버전 완성뒤에 업데이트 예정
//    private void setVideoCode(String code){
//        callUser.setVideoCode(code);
//    }
    private void setCallUserName(String name){
        String[] names = context.getResources().getStringArray(R.array.array_call_users);
        String callName="";
        switch (name){
            case "wonyoung"://워뇽이
                callName = names[0];
                break;
            case "sakura"://꾸라
                callName = names[1];
                break;
            case "yuri"://조댕찌
                callName = names[2];
                break;
            case "yena"://최오리
                callName = names[3];
                break;
            case "yujin"://안유댕
                callName = names[4];
                break;
            case "nako"://나코나코땅땅
                callName = names[5];
                break;
            case "eunbi"://권땡모
                callName = names[6];
                break;
            case "hyewon"://강광배
                callName = names[7];
                break;
            case "hitomi"://히짱
                callName = names[8];
                break;
            case "chaewon"://자몽워니
                callName = names[9];
                break;
            case "minju"://밍주가또오~!
                callName = names[10];
                break;
            case "chaeyeon"://이째욘
                callName = names[11];
                break;
        }
        callUser.setCallName(callName);
    }
    private void setCallUserThumb(String id){
        Field[] thumbnailList = R.drawable.class.getFields();
        Drawable drawables;
        int size = thumbnailList.length;
        for (int i = 0; i < size - 1; i++) {
            //썸네일 파일에 같은 ID(날짜)가 있는지 검사
            if(thumbnailList[i].getName().contains(id)){
                Resources resources = context.getResources();
                //drawable folder 에 있는 파일은 ID 값으로만 불러올수 있어 파일이름을 ID 값으로 변환한다
                int resID = resources.getIdentifier(thumbnailList[i].getName(), "drawable", context.getPackageName());
                //drawables = context.getResources().getDrawable(resID);
                callUser.setThumbnailId(resID);
                break;
            }
            //do your thing here
            Log.d(TAG, "File Name:" + thumbnailList[i].getName());
        }
    }
}
