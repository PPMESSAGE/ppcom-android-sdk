package com.ppmessage.sdk.core.bean.message;

import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.logging.FileHandler;

/**
 * Created by ppmessage on 5/10/16.
 */
public class PPMessageFileMediaItem implements IPPMessageMediaItem {

    private String mime;
    private String name;
    private String fid;
    private String fUrl;
    private long size;
    private String humanReadableSize;
    private File file;

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getfUrl() {
        return fUrl;
    }

    public void setfUrl(String fUrl) {
        this.fUrl = fUrl;
    }

    public String getHumanReadableSize() {
        return humanReadableSize;
    }

    public void setHumanReadableSize(String humanReadableSize) {
        this.humanReadableSize = humanReadableSize;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static IPPMessageMediaItem parse(JSONObject jsonObject) {
        PPMessageFileMediaItem fileMediaItem = new PPMessageFileMediaItem();
        try {
            fileMediaItem.setMime(jsonObject.getString("mime"));
            fileMediaItem.setName(jsonObject.getString("name"));
            fileMediaItem.setFid(jsonObject.getString("fid"));
            fileMediaItem.setfUrl(Utils.getFileDownloadUrl(fileMediaItem.getFid()));
            fileMediaItem.setSize(jsonObject.getLong("size"));
            fileMediaItem.setHumanReadableSize(Utils.humanReadableByteCount(fileMediaItem.getSize()));

            return fileMediaItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getType() {
        return PPMessage.TYPE_FILE;
    }

    @Override
    public void asyncGetAPIJsonObject(OnGetJsonObjectEvent event) {
        // Waiting for implementation
    }
}
