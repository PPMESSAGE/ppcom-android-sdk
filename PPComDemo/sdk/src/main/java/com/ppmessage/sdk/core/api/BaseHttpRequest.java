package com.ppmessage.sdk.core.api;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.ppmessage.sdk.core.L;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ppmessage on 5/5/16.
 */
public class BaseHttpRequest {

    private static final int REQUEST_READ_TIMEOUT = 5000;
    private static final int REQUEST_CONNECT_TIMEOUT = 15000;

    private static final String REQUEST_LOG_FORMAT = "request with url: %s, with data: %s, with access token:%s";
    private static final String RESPONSE_LOG_FORMAT = "response with url: %s, with response code: %d, with data: %s";

    private final List<AsyncTask> taskList = new CopyOnWriteArrayList<>();

    public void get(String url,OnHttpRequestCompleted completedCallback) {
        request(url, null, "GET", completedCallback);
    }

    public void post(final String url, final String requestString, final OnHttpRequestCompleted completedCallback) {
        post(url, new PostStringWriter(requestString), completedCallback);
    }

    public void post(final String url, final PostObject any, final OnHttpRequestCompleted completedCallback) {
        request(url, any, "POST", completedCallback);
    }

    private void request(final String url, final PostObject anyObj, final String method, final OnHttpRequestCompleted completedCallback) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
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


    private String makeRequest(String url, PostObject anyObj, String method) throws Exception {
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        setup(conn);
        L.d(REQUEST_LOG_FORMAT, url, anyObj, conn.getRequestProperty("Authorization"));
        conn.setReadTimeout(REQUEST_READ_TIMEOUT);
        conn.setConnectTimeout(REQUEST_CONNECT_TIMEOUT);
        conn.setRequestMethod(method);
        conn.setDoInput(true);

        if (method.equals("POST") || anyObj != null) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            anyObj.writeBody(conn, os);
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

    class PostStringWriter implements PostObject{
        String body;
        public PostStringWriter(String stringBody) {
            this.body=stringBody;
        }

        public String getBody() {
            return this.body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        @Override
        public void buildBody(HttpURLConnection connection) {

        }

        @Override
        public boolean writeBody(HttpURLConnection connection, OutputStream bodyWriter) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(bodyWriter, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return  false;
            }
            try {
                writer.write(body);
                writer.flush();
                writer.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
