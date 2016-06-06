package com.ppmessage.sdk.core.utils;

import com.ppmessage.sdk.core.PPMessageException;

import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Large text uploader
 *
 * Created by ppmessage on 5/6/16.
 */
public class TxtUploader extends Uploader {

    private static final String DEFAULT_FILE_NAME = "TXT_FILE";

    /**
     * Consider text as a file, and upload it to server
     *
     * @param text
     * @param uploadingListener
     */
    public void upload(String text, OnUploadingListener uploadingListener) {
        byte[] b = text.getBytes(Charset.forName("UTF-8"));
        upload(b, DEFAULT_FILE_NAME, uploadingListener);
    }

}
