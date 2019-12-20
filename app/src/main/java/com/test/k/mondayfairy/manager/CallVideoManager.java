package com.test.k.mondayfairy.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.test.k.mondayfairy.R;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/*가짜 영상통화때 재생할 영상 URI를 관리하는 클래스*/
public class CallVideoManager {
    private final String TAG = "CallVideoManager";
    private final int FILE_COUNT_OF_MEMBER = 4;
    private String filePath;
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
        int i,j=0;
        String videoPattern = "^(call|mov)+[_]";//파일이름이 call_이나 mov_로 시작하는 정규표현식
        Pattern pattern = Pattern.compile(videoPattern);
        for (i = 0; i < size; i++) {
            if(pattern.matcher(fields[i].getName()).find()) {//영상파일만 가져옴
                videoList.add(fields[i].getName());
                //do your thing here
                Log.d(TAG, "File Name:" + videoList.get(j));
                j++;
            }
        }
        return videoList;
    }
    //사용자가 선택한 월요요정의 영상파일들을 불러온다
    private String[] getVideo(String searchText){
        ArrayList<String> videoList = getVideoList();
        //현재 각 멤버당 영상이 4개씩 존재
        String[] fileNames = new String[FILE_COUNT_OF_MEMBER];
        int count = 0;
        int listSize = videoList.size();
        Log.d(TAG,"전체파일 갯수:"+listSize);
        //사용자가 모두를 선택했을 경우 전체 파일중 2개를 선택한다
        if(searchText.equals("izone")){
            Random random = new Random();
            int arraySize = fileNames.length;
            //모든 영상 파일 수
            for(int i =  0; i<arraySize; i++){
                fileNames[i] = videoList.get(random.nextInt(listSize));
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
    //CallPㅣayerActivity에서 재생할 영상 URI를 반환한다
    public Uri getMemberVideoUri(Context context){
        ArrayList<String> member = setMember(context);
        String[] memberNames = context.getResources().getStringArray(R.array.array_member_names_eng);
        String memberName;
        Random random = new Random();
        //사용자가 선택한 월요요정이 2명이상이라면
        if(member.size()>1) {
            memberName = member.get(random.nextInt(member.size()));
        }else{
            memberName = member.get(0);
        }
        String[] files = new String[FILE_COUNT_OF_MEMBER];
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
        filePath = files[random.nextInt(FILE_COUNT_OF_MEMBER)];
        Log.d(TAG, "filePath:"+filePath);
        //최종결정된 영상이름과 관련된 월요요정 이름과, 썸네일을 지정한다(아직 구현안함)
        Resources resources = context.getResources();
//        String sample = "mov_minju_190121";
//        filePath = sample;
//        int resID = resources.getIdentifier(sample, "raw", context.getPackageName());
        //raw folder 에 있는 파일은 ID 값으로만 불러올수 있어 파일이름을 ID 값으로 변환한다
        int resID = resources.getIdentifier(filePath, "raw", context.getPackageName());
        //Uri.parse(rawresource:///rawResourceId)
        return Uri.parse ("rawresource"+ File.pathSeparator+File.separator+File.separator
                +File.separator+resID);
 //       return RawResourceDataSource.buildRawResourceUri(resID);

    }
    public String getVideoFilePath(){
        return filePath;
    }
}
