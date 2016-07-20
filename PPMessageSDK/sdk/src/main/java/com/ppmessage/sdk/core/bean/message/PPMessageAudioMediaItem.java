package com.ppmessage.sdk.core.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.utils.Uploader;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 7/18/16.
 */
public class PPMessageAudioMediaItem implements IPPMessageMediaItem {

    /** in seconds **/
    private float duration;
    private String fid;
    private String furl;
    private String fLocalPath;
    private boolean isPlaying;
    private String mime;

    public PPMessageAudioMediaItem() {
    }

    protected PPMessageAudioMediaItem(Parcel in) {
        duration = in.readFloat();
        fid = in.readString();
        furl = in.readString();
        fLocalPath = in.readString();
        isPlaying = in.readByte() != 0;
        mime = in.readString();
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
    public void asyncGetAPIJsonObject(final OnGetJsonObjectEvent event) {
        Utils.getFileUploader().uploadFile(this.fLocalPath, new Uploader.OnUploadingListener() {
            @Override
            public void onError(Exception e) {
                if (event != null) {
                    event.onError(e);
                }
            }

            @Override
            public void onComplected(JSONObject response) {
                if (event != null) {
                    String fuuid = response.optString("fuuid", null);
                    PPMessageAudioMediaItem.this.fid = fuuid;

                    event.onCompleted(buildAPIJSONObject());
                }
            }
        });
    }

    private JSONObject buildAPIJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fid", fid);
            jsonObject.put("mime", mime);
            jsonObject.put("dura", duration);
        } catch (JSONException e) {
            L.e(e);
        }
        return jsonObject;
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

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public static PPMessageAudioMediaItem parse(JSONObject jsonObject) {
        PPMessageAudioMediaItem audioMediaItem = new PPMessageAudioMediaItem();
        JSONObject amrJSONObject = jsonObject.optJSONObject("amr");
        if (amrJSONObject != null) {
            audioMediaItem.setFid(amrJSONObject.optString("fid", null));
            audioMediaItem.setDuration((float) amrJSONObject.optDouble("dura", .0));
            audioMediaItem.setFurl(Utils.getFileDownloadUrl(audioMediaItem.getFid()));
            audioMediaItem.setMime("audio/amr");
        }
        return audioMediaItem;
    }
}
