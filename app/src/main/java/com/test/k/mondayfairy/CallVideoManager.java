package com.test.k.mondayfairy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.widget.Switch;

import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
/*가짜 영상통화때 재생할 영상 URI를 관리하는 클래스*/
public class CallVideoManager {
    private final String TAG = "CallVideoManager";
    public CallVideoManager(){

    }
    //설정파일에서 사용자가 선택한 멤버정보를 가져온다
    public ArrayList<String> setMember(Context context){
        SharedPreferences preferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        Set<String> memberSet = preferences.getStringSet("member", new HashSet<String>());
        if(!memberSet.isEmpty()) {
            return new ArrayList<>(memberSet);
        }
        return null;
    }
    //재생영상 파일이름들을 모두 불러온다
    private ArrayList<String> getVideoList() {
        Field[] fields = R.raw.class.getFields();
        ArrayList<String> videoList = new ArrayList<>();
        int size = fields.length;
        for (int i = 0; i < size - 1; i++) {
            videoList.add(fields[i].getName());
            //do your thing here
            Log.d(TAG, "File Name:" + videoList.get(i));
        }
        return videoList;
    }
    private String[] getVideo(String searchText){
        ArrayList<String> videoList = getVideoList();
        String[] fileNames = new String[2];
        int count = 0;
        int listSize = videoList.size();
        //사용자가 모두를 선택했을 경우 전체 파일중 2개를 선택한다
        if(searchText.equals("izone")){
            Random random = new Random();
            int arraySize = fileNames.length;
            int videoListSize = videoList.size();
            for(int i =  0; i<arraySize; i++){
                fileNames[i] = videoList.get(random.nextInt(videoListSize));
            }
            return fileNames;
        }
        for(int i = 0; i< listSize; i++){
            if(videoList.get(i).contains(searchText)){
                fileNames[count] = videoList.get(i);
                count++;
            }
        }
        return fileNames;
    }
    //재생할 영상 URI를 반환한다
    public Uri getMemberVideoUri(Context context){
        ArrayList<String> member = setMember(context);
        String[] memberNames = context.getResources().getStringArray(R.array.array_member_names_eng);
        String memberName;
        Random random = new Random();
        if(member.size()>0) {
            memberName = member.get(random.nextInt(member.size()));
        }else{
            memberName = member.get(0);
        }
        String[] files = new String[2];
        switch(memberName){
            case "원영":
                files = getVideo(memberNames[0]);
                break;
            case "꾸라":
                files = getVideo(memberNames[1]);
                break;
            case "유리":
                files = getVideo(memberNames[2]);
                break;
            case "예나":
                files = getVideo(memberNames[3]);
                break;
            case "유진":
                files = getVideo(memberNames[4]);
                break;
            case "나코":
                files = getVideo(memberNames[5]);
                break;
            case "은비":
                files = getVideo(memberNames[6]);
                break;
            case "혜원":
                files = getVideo(memberNames[7]);
                break;
            case "히짱":
                files = getVideo(memberNames[8]);
                break;
            case "채원":
                files = getVideo(memberNames[9]);
                break;
            case "민주":
                files = getVideo(memberNames[10]);
                break;
            case "채연":
                files = getVideo(memberNames[11]);
                break;
            case "모두":
                files = getVideo(memberNames[12]);
                break;
        }
        String filePath = files[random.nextInt(2)];
        Resources resources = context.getResources();
        int resID = resources.getIdentifier(filePath, "raw", context.getPackageName());
        //Uri.parse(rawresource:///rawResourceId)
        return Uri.parse ("rawresource"+ File.pathSeparator+File.separator+File.separator
                +File.separator+resID);
 //       return RawResourceDataSource.buildRawResourceUri(resID);

    }
}
