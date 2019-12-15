package com.test.k.mondayfairy;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;


/*Singleton Class*/
@Keep
public class CallUser implements Parcelable {
    private int thumbnailId;
    private String callName;
    private String videoCode;
    private Uri videoUri;
    // Private constructor prevents instantiation from other classes
    private CallUser() { }

    /**
     * SingletonHolder is loaded on the first execution of CallUser.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final CallUser INSTANCE = new CallUser();
    }
    public static CallUser getInstance() {
        return SingletonHolder.INSTANCE;
    }
    public int getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(int thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public void setVideoCode(String code){ videoCode = code; }

    public String getVideoCode(){
        return videoCode;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(thumbnailId);
        dest.writeString(callName);
        dest.writeString(videoCode);
        dest.writeParcelable(videoUri,flags);
    }
    public static final Parcelable.Creator<CallUser> CREATOR
            = new Parcelable.Creator<CallUser>() {
        public CallUser createFromParcel(Parcel in) {
            return new CallUser(in);
        }

        public CallUser[] newArray(int size) {
            return new CallUser[size];
        }
    };

    private CallUser(Parcel in) {
        thumbnailId = in.readInt();
        callName = in.readString();
        videoCode = in.readString();
        videoUri = in.readParcelable(getClass().getClassLoader());

    }
}
