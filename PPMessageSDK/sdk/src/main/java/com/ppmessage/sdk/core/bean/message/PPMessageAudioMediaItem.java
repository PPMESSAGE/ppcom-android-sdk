package com.ppmessage.sdk.core.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONObject;

/**
 * Created by ppmessage on 7/18/16.
 */
public class PPMessageAudioMediaItem implements IPPMessageMediaItem {

    private float duration;
    private String fid;
    private String furl;
    private String fLocalPath;
    private boolean isPlaying;

    public PPMessageAudioMediaItem() {
    }

    protected PPMessageAudioMediaItem(Parcel in) {
        duration = in.readFloat();
        fid = in.readString();
        furl = in.readString();
        fLocalPath = in.readString();
        isPlaying = in.readByte() != 0;
    }

    public static final Creator<PPMessageAudioMediaItem> CREATOR = new Creator<PPMessageAudioMediaItem>() {
        @Override
        public PPMessageAudioMediaItem createFromParcel(Parcel in) {
            return new PPMessageAudioMediaItem(in);
        }

        @Override
        public PPMessageAudioMediaItem[] newArray(int size) {
            return new PPMessageAudioMediaItem[size];
        }
    };

    @Override
    public String getType() {
        return PPMessage.TYPE_AUDIO;
    }

    @Override
    public void asyncGetAPIJsonObject(OnGetJsonObjectEvent event) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(duration);
        parcel.writeString(fid);
        parcel.writeString(furl);
        parcel.writeString(fLocalPath);
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFurl() {
        return furl;
    }

    public void setFurl(String furl) {
        this.furl = furl;
    }

    public String getfLocalPath() {
        return fLocalPath;
    }

    public void setfLocalPath(String fLocalPath) {
        this.fLocalPath = fLocalPath;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public static PPMessageAudioMediaItem parse(JSONObject jsonObject) {
        PPMessageAudioMediaItem audioMediaItem = new PPMessageAudioMediaItem();
        audioMediaItem.setDuration((float) jsonObject.optDouble("dura", .0));
        audioMediaItem.setFid(jsonObject.optString("fid", null));
        audioMediaItem.setFurl(Utils.getFileDownloadUrl(audioMediaItem.getFid()));
        return audioMediaItem;
    }
}
