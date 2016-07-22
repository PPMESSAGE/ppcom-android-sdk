package com.ppmessage.sdk.core.bean.message;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.utils.Uploader;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.logging.FileHandler;

/**
 * Created by ppmessage on 5/10/16.
 */
public class PPMessageImageMediaItem implements IPPMessageMediaItem {

    private static final int DEFAULT_IMAGE_WIDTH = 300;
    private static final int DEFAULT_IMAGE_HEIGHT = 400;

    private File file;
    private String mime;
    private String thumId;
    private String origId;
    private String thumUrl;
    private String origUrl;
    private String localPathUrl;
    private int origWidth;
    private int origHeight;
    private int thumWidth;
    private int thumHeight;

    public PPMessageImageMediaItem() {

    }


    protected PPMessageImageMediaItem(Parcel in) {
        mime = in.readString();
        thumId = in.readString();
        origId = in.readString();
        thumUrl = in.readString();
        origUrl = in.readString();
        localPathUrl = in.readString();
        origWidth = in.readInt();
        origHeight = in.readInt();
        thumWidth = in.readInt();
        thumHeight = in.readInt();
    }

    public static final Creator<PPMessageImageMediaItem> CREATOR = new Creator<PPMessageImageMediaItem>() {
        @Override
        public PPMessageImageMediaItem createFromParcel(Parcel in) {
            return new PPMessageImageMediaItem(in);
        }

        @Override
        public PPMessageImageMediaItem[] newArray(int size) {
            return new PPMessageImageMediaItem[size];
        }
    };

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getThumId() {
        return thumId;
    }

    public void setThumId(String thumId) {
        this.thumId = thumId;
    }

    public String getOrigId() {
        return origId;
    }

    public void setOrigId(String origId) {
        this.origId = origId;
    }

    public String getThumUrl() {
        return thumUrl;
    }

    public void setThumUrl(String thumUrl) {
        this.thumUrl = thumUrl;
    }

    public String getOrigUrl() {
        return origUrl;
    }

    public void setOrigUrl(String origUrl) {
        this.origUrl = origUrl;
    }

    public int getOrigWidth() {
        return origWidth;
    }

    public void setOrigWidth(int origWidth) {
        this.origWidth = origWidth;
    }

    public int getOrigHeight() {
        return origHeight;
    }

    public void setOrigHeight(int origHeight) {
        this.origHeight = origHeight;
    }

    public int getThumWidth() {
        return thumWidth;
    }

    public void setThumWidth(int thumWidth) {
        this.thumWidth = thumWidth;
    }

    public int getThumHeight() {
        return thumHeight;
    }

    public void setThumHeight(int thumHeight) {
        this.thumHeight = thumHeight;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLocalPathUrl() {
        return localPathUrl;
    }

    public void setLocalPathUrl(String localPathUrl) {
        this.localPathUrl = localPathUrl;
    }

    public static IPPMessageMediaItem parse(JSONObject jsonObject) {
        PPMessageImageMediaItem imageMediaItem = new PPMessageImageMediaItem();
        try {
            imageMediaItem.setMime(jsonObject.getString("mime"));
            imageMediaItem.setThumId(jsonObject.getString("thum"));
            imageMediaItem.setOrigId(jsonObject.getString("orig"));
            imageMediaItem.setThumUrl(Utils.getFileDownloadUrl(imageMediaItem.getThumId()));
            imageMediaItem.setOrigUrl(Utils.getFileDownloadUrl(imageMediaItem.getOrigId()));

            if (jsonObject.has("orig_width")) {
                imageMediaItem.setOrigWidth(jsonObject.getInt("orig_width"));
                imageMediaItem.setOrigHeight(jsonObject.getInt("orig_height"));
                imageMediaItem.setThumWidth(jsonObject.getInt("thum_width"));
                imageMediaItem.setThumHeight(jsonObject.has("thum_height") ? jsonObject.getInt("thum_height") :
                        (jsonObject.has("_thum_height") ? jsonObject.getInt("_thum_height") : DEFAULT_IMAGE_HEIGHT));
            } else {
                imageMediaItem.setOrigWidth(DEFAULT_IMAGE_WIDTH);
                imageMediaItem.setOrigHeight(DEFAULT_IMAGE_HEIGHT);
                imageMediaItem.setThumWidth(DEFAULT_IMAGE_WIDTH);
                imageMediaItem.setThumHeight(DEFAULT_IMAGE_HEIGHT);
            }

            return imageMediaItem;
        } catch (JSONException e) {
            L.e(e);
        }
        return null;
    }

    @Override
    public String getType() {
        return PPMessage.TYPE_IMAGE;
    }

    @Override
    public void asyncGetAPIJsonObject(final OnGetJsonObjectEvent event) {
        if (this.getLocalPathUrl() == null) {
            if (event != null) {
                event.onError(new Exception("localPath == null"));
            }
            return;
        }

        Utils.getFileUploader().uploadFile(Uri.parse(this.getLocalPathUrl()), new Uploader.OnUploadingListener() {
            @Override
            public void onError(Exception e) {
                if (event != null) {
                    event.onError(e);
                }
            }

            @Override
            public void onComplected(JSONObject response) {
                if (event != null) {

                    String fid = response.optString("fuuid", null);
                    PPMessageImageMediaItem.this.setOrigId(fid);
                    PPMessageImageMediaItem.this.setOrigUrl(Utils.getFileDownloadUrl(fid));

                    event.onCompleted(buildJsonObject(fid));
                }
            }
        });
    }

    private JSONObject buildJsonObject(String fid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fid", fid);
            jsonObject.put("mime", "image/jpeg");
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mime);
        dest.writeString(thumId);
        dest.writeString(origId);
        dest.writeString(thumUrl);
        dest.writeString(origUrl);
        dest.writeInt(origWidth);
        dest.writeInt(origHeight);
        dest.writeInt(thumWidth);
        dest.writeInt(thumHeight);
    }
}
