package com.ppmessage.sdk.core.api;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.ppmessage.sdk.core.L;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ppmessage on 5/5/16.
 */
public class BaseHttpRequest {

    private static final int REQUEST_READ_TIMEOUT = 15000;
    private static final int REQUEST_CONNECT_TIMEOUT = 5000;

    private static final String REQUEST_LOG_FORMAT = "request with url: %s, with data: %s";
    private static final String RESPONSE_LOG_FORMAT = "response with url: %s, with response code: %d, with data: %s";

    private final List<AsyncTask> taskList = new CopyOnWriteArrayList<>();

    public void get(String url, String requestString, OnHttpRequestCompleted completedCallback) {
        request(url, requestString, "GET", completedCallback);
    }

    public void post(final String url, final String requestString, final OnHttpRequestCompleted completedCallback) {
        post(url, (Object)requestString, completedCallback);
    }

    public void post(final String url, final Object any, final OnHttpRequestCompleted completedCallback) {
        request(url, any, "POST", completedCallback);
    }

    private void request(final String url, final Object anyObj, final String method, final OnHttpRequestCompleted completedCallback) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    L.d(REQUEST_LOG_FORMAT, url, anyObj);
                    return makeRequest(url, anyObj, method);
                } catch (Exception e) {
                    L.e(e);
                    return null;
                }
            }

            @Override
            protected void onCancelled(String str) {
                super.onCancelled(str);
                if (completedCallback != null) {
                    completedCallback.onCancelled();
                }
            }

            @Override
            protected void onPostExecute(String string) {
                super.onPostExecute(string);

                if (completedCallback != null) {
                    if (string != null) {
                        completedCallback.onResponse(string);
                    } else {
                        completedCallback.onError(ErrorInfo.ErrorCode.HTTP_ERROR);
                    }
                }
            }
        };
        task.execute();
        taskList.add(task);
    }

    public void cancelAll() {
        for (AsyncTask task: taskList) {
            if (task != null && !task.isCancelled()) {
                task.cancel(true);
            }
        }
        taskList.clear();
    }

    /**
     * Override this method to provide your own HTTP headers
     *
     * @param conn
     */
    protected void setup(HttpURLConnection conn) {
    }

    /**
     * Write data to server, Don't close os manually
     *
     * @param os
     */
    protected void write(HttpURLConnection conn, OutputStream os, Object anyObj) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write((String)anyObj);
        writer.flush();
        writer.close();
    }

    private String makeRequest(String url, Object anyObj, String method) throws Exception {
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        setup(conn);
        conn.setReadTimeout(REQUEST_READ_TIMEOUT);
        conn.setConnectTimeout(REQUEST_CONNECT_TIMEOUT);
        conn.setRequestMethod(method);
        conn.setDoInput(true);

        if (method.equals("POST") || anyObj != null) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            write(conn, os, anyObj);
            os.close();
        }

        String response = "";
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
            br.close();
        }

        L.d(RESPONSE_LOG_FORMAT, uri, responseCode, response);

        return TextUtils.isEmpty(response) ? null : response;
    }

}
