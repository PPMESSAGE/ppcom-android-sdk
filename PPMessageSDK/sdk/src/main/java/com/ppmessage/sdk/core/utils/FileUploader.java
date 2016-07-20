package com.ppmessage.sdk.core.utils;

import android.net.Uri;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ppmessage on 7/19/16.
 */
public class FileUploader extends Uploader {

    private static final String LOG_FILEPATH_IS_NULL = "[FileUploader] filePath == null";
    private static final String LOG_FILEPATH_URI_IS_NULL = "[FileUploader] filePath uri == null";
    private static final String LOG_FILE_IS_NULL = "[FileUploader] file == null";
    private static final String LOG_FILE_NOT_EXISTS = "[FileUploader] file not exists";

    public FileUploader(PPMessageSDK messageSDK) {
        super(messageSDK);
    }

    public void uploadFile(Uri fileUri, final OnUploadingListener uploadingListener) {
        if (fileUri == null) {
            L.w(LOG_FILEPATH_URI_IS_NULL);
            makeErrorCallback(LOG_FILEPATH_URI_IS_NULL, uploadingListener);
            return;
        }
        uploadFile(fileUri.getPath(), uploadingListener);
    }

    public void uploadFile(String filePath, final OnUploadingListener uploadingListener) {
        if (filePath == null) {
            L.w(LOG_FILEPATH_IS_NULL);
            makeErrorCallback(LOG_FILEPATH_IS_NULL, uploadingListener);
            return;
        }
        uploadFile(new File(filePath), uploadingListener);
    }

    public void uploadFile(File file, final OnUploadingListener uploadingListener) {
        if (file == null) {
            L.w(LOG_FILE_IS_NULL);
            makeErrorCallback(LOG_FILE_IS_NULL, uploadingListener);
            return;
        }

        if (!file.exists()) {
            L.w(LOG_FILE_NOT_EXISTS);
            makeErrorCallback(LOG_FILE_NOT_EXISTS, uploadingListener);
            return;
        }

        final String fileName = file.getName();
        try {
            InputStream fileInputStream = new FileInputStream(file);
            try {
                byte[] fileBytes = Utils.toByteArray(fileInputStream);
                super.upload(fileBytes, fileName, uploadingListener);
            } catch (IOException e) {
                L.e(e);
                if (uploadingListener != null) {
                    uploadingListener.onError(e);
                }
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        L.e(e);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            L.e(e);
            if (uploadingListener != null) {
                uploadingListener.onError(e);
            }
        }
    }

    private void makeErrorCallback(String errorInfo, final OnUploadingListener uploadingListener) {
        if (uploadingListener != null) {
            uploadingListener.onError(new Exception(errorInfo));
        }
    }

}
