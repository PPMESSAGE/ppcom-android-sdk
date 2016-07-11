package com.ppmessage.sdk.core.api;

import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by zoulinlin on 16/7/11.
 */
public class FileUploaderObject extends HashMap<String, Object> implements PostObject{


    final String mPostBoundary = "----WebKitFormBoundaryamroXVL2GK8a89eH";
    HashMap<String,File> mPostFilesList=new HashMap<>();
    byte[]mParamsBuf=null;
    byte[]mEndBuf = null;
    @Override
    public void buildBody(HttpURLConnection connection) {

        //set header
        String contentType = "multipart/form-data; boundary="+mPostBoundary;
        connection.addRequestProperty("Content-Type",contentType);
        connection.addRequestProperty("Connection","keep-alive");

        //ini body
        StringBuffer sb = new StringBuffer();
        StringBuffer sbFile = new StringBuffer();
        Object val=null;
        int totalLength=0;
        mPostFilesList.clear();
        for (String key : this.keySet()) {
            val=get(key);
            if (val instanceof String) {
                sb.append(generateKeyParamString(key));
                sb.append(val);
            }
            else if (val instanceof File){
                File valFile = (File)val;
                String tmpParam=generateFileParamString(key, valFile.getName());
                String mimiType =getMimeType(valFile.getAbsolutePath());
                if (mimiType != null) {
                    tmpParam += "\r\nContent-Type: " + mimiType + "\r\n\r\n";
                }
                mPostFilesList.put(tmpParam,valFile);
                sbFile.append(tmpParam);
                totalLength+=valFile.length();
            }
        }

        Log.i("TestLen", "len file1 is" + totalLength);
        try {
            mParamsBuf = sb.toString().getBytes("UTF-8");
            if (mEndBuf == null) {
                mEndBuf= ("\r\n--" + mPostBoundary + "--").getBytes("UTF-8");
            }
            Log.i("TestLen", "len mParamsBuf is" + mParamsBuf.length);
            Log.i("TestLen", "len mEndBuf is" + mEndBuf.length);
            Log.i("TestLen", "len sbFile is" + sbFile.toString().getBytes("UTF-8").length);
            totalLength+= mParamsBuf.length + mEndBuf.length + sbFile.toString().getBytes("UTF-8").length;
            connection.addRequestProperty("Content-Length", ""+ totalLength);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ;
    }

    @Override
    public boolean writeBody(HttpURLConnection connection, OutputStream bodyWriter) {
        try {
            bodyWriter.write(mParamsBuf);
            Log.i("TestLen", "len mParamsBuf is" + mParamsBuf.length);

            for (String fileParam : mPostFilesList.keySet()) {
                byte[ ]fp = fileParam.getBytes("UTF-8");
                bodyWriter.write(fp);
                Log.i("TestLen", "len fp is" + fp.length);
                FileInputStream in =new FileInputStream(mPostFilesList.get(fileParam));
                int len=-1;
                int lenT=0;
                byte[] bt = new byte[2048]; //可以根据实际情况调整，建议使用1024，即每次读1KB
                while((len=(in.read(bt)))!= -1) {
                    lenT +=len;
                    bodyWriter.write(bt,0,len); //建议不要直接用os.write(bt)
                }
                Log.i("TestLen", "len lenT is" + lenT);
                in.close();
            }
            bodyWriter.write(mEndBuf);
            Log.i("TestLen", "len mEndBuf is" + mEndBuf.length);
            bodyWriter.flush();
            bodyWriter.close();
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = null;
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    protected String generateKeyParamString(String key) {
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n--"+mPostBoundary+"\r\n");
        sb.append("Content-Disposition: form-data; name=\""+key+"\"\r\n\r\n");
        return sb.toString();
    }


    protected String generateFileParamString(String key, String fileName) {
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n--"+mPostBoundary+"\r\n");
        sb.append("Content-Disposition: form-data; name=\""+key+"\"; filename=\""+fileName+"\"");
        return sb.toString();
    }
}
