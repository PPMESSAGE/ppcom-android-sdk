package com.ppmessage.sdk.core.utils;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageException;
import com.ppmessage.sdk.core.api.BaseHttpRequest;
import com.ppmessage.sdk.core.api.ErrorInfo;
import com.ppmessage.sdk.core.api.FileUploaderObject;
import com.ppmessage.sdk.core.api.HostConstants;
import com.ppmessage.sdk.core.api.OnHttpRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
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


    public static final String FILE_UPLOAD_HOST = HostConstants.HTTP_HOST + "/upload/upload/";
    FileUploaderObject uploadObj = new FileUploaderObject();
    public void uploadFile(File file, String fromUserId, final OnUploadingListener callback) {
        uploadObj.clear();
        uploadObj.put("upload_type", "file");
        uploadObj.put("subtype", "FILE");
        uploadObj.put("user_uuid", fromUserId);
        uploadObj.put("file",file);
        post(FILE_UPLOAD_HOST, uploadObj, new OnHttpRequestCompleted() {

            @Override
            public void onResponse(String response) {
                if (callback != null) {
                    try {
                        callback.onComplected(new JSONObject(response));
                    } catch (JSONException e) {
                        L.e(e);
                        callback.onError(e);
                    }
                }
            }

            @Override
            public void onCancelled() {
                if (callback != null) {
                    callback.onError(new PPMessageException("Canceled"));
                }
            }

            @Override
            public void onError(int errorCode) {
                if (callback != null) {
                    callback.onError(new PPMessageException(ErrorInfo.getErrorString(errorCode)));
                }
            }
        });
    }

    @Override
    protected void setup(HttpURLConnection conn) {
        super.setup(conn);
        uploadObj.buildBody(conn);
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
