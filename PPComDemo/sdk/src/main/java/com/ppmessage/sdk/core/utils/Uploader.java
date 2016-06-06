package com.ppmessage.sdk.core.utils;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageException;
import com.ppmessage.sdk.core.api.BaseHttpRequest;
import com.ppmessage.sdk.core.api.ErrorInfo;
import com.ppmessage.sdk.core.api.HostConstants;
import com.ppmessage.sdk.core.api.OnHttpRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Upload bytes to server, consider it as a file
 *
 * Created by ppmessage on 5/6/16.
 */
public class Uploader extends BaseHttpRequest {

    public static final String FILE_UPLOAD_HOST = HostConstants.HTTP_HOST + "/upload";

    private static final String CRLF = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private static final String BOUNDARY = "*****";
    private static final String ATTACHMENT_NAME = "file";

    private static final String CANCEL_UPLOAD = "upload canceled";

    @Override
    protected void setup(HttpURLConnection conn) {
        super.setup(conn);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
    }

    @Override
    protected void write(HttpURLConnection conn, OutputStream os, Object anyObj) throws IOException {

        Wrapper obj = (Wrapper) anyObj;
        byte[] data = obj.dataArray;
        String fileName = obj.fileName;

        // Content Wrapper Begin
        DataOutputStream request = new DataOutputStream(os);
        request.writeBytes(TWO_HYPHENS + BOUNDARY + CRLF);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                ATTACHMENT_NAME + "\";filename=\"" +
                fileName + "\"" + CRLF);
        request.writeBytes(CRLF);

        // Content (file bytes)
        request.write(data);

        // Content Wrapper End
        request.writeBytes(CRLF);
        request.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + CRLF);
    }

    /**
     * Response: { fuuid: 'xxxxxx-xxx-xxx-xxx-xxx' }
     *
     * @param data
     * @param fileName
     * @param uploadingListener
     */
    public void upload(byte[] data, String fileName, final OnUploadingListener uploadingListener) {
        post(FILE_UPLOAD_HOST, new Wrapper(fileName, data), new OnHttpRequestCompleted() {

            @Override
            public void onResponse(String response) {
                if (uploadingListener != null) {
                    try {
                        uploadingListener.onComplected(new JSONObject(response));
                    } catch (JSONException e) {
                        L.e(e);
                        uploadingListener.onError(e);
                    }
                }
            }

            @Override
            public void onCancelled() {
                if (uploadingListener != null) {
                    uploadingListener.onError(new PPMessageException(CANCEL_UPLOAD));
                }
            }

            @Override
            public void onError(int errorCode) {
                if (uploadingListener != null) {
                    uploadingListener.onError(new PPMessageException(ErrorInfo.getErrorString(errorCode)));
                }
            }
        });
    }

    /**
     * Wrapper Obj to post
     */
    static class Wrapper {

        String fileName;
        byte[] dataArray;

        public Wrapper(String fileName, byte[] dataArray) {
            this.fileName = fileName;
            this.dataArray = dataArray;
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "<%s@%d, fileName:%s, dataLength:%d>",
                    this.getClass().getName(),
                    System.identityHashCode(this),
                    this.fileName,
                    this.dataArray.length);
        }
    }

    /**
     * Upload listener
     *
     * @author zhaokun
     *
     */
    public interface OnUploadingListener {

        /**
         * upload error
         *
         * @param e
         */
        void onError(Exception e);

        /**
         * upload complected
         *
         * @param response
         */
        void onComplected(JSONObject response);

    }

}
