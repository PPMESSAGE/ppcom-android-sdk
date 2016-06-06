package com.ppmessage.sdk.core.bean.message;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageException;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.utils.TxtUploader;
import com.ppmessage.sdk.core.utils.Uploader;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/10/16.
 */
public class PPMessageTxtMediaItem implements IPPMessageMediaItem {

    private static final String LOG_TXT_CONTENT_EMPTY = "Txt content can not be empty";

    private String textContent;
    private String txtUrl;
    private String txtFid;

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTxtUrl() {
        return txtUrl;
    }

    public void setTxtUrl(String txtUrl) {
        this.txtUrl = txtUrl;
    }

    public String getTxtFid() {
        return txtFid;
    }

    public void setTxtFid(String txtFid) {
        this.txtFid = txtFid;
    }

    public static IPPMessageMediaItem parse(JSONObject jsonObject) {
        PPMessageTxtMediaItem txtMediaItem = new PPMessageTxtMediaItem();
        try {
            String fid = jsonObject.getString("fid");
            String fUrl = Utils.getFileDownloadUrl(fid);
            txtMediaItem.setTxtFid(fid);
            txtMediaItem.setTxtUrl(fUrl);
            return txtMediaItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getType() {
        return PPMessage.TYPE_TXT;
    }

    @Override
    public void asyncGetAPIJsonObject(final OnGetJsonObjectEvent event) {
        String txtFid = getTxtFid();
        String txtContent = getTextContent();

        if (txtFid != null) {
            if (event != null) event.onCompleted(getAPIJSonObject(txtFid));
        } else {
            if (txtContent == null) {
                if (event != null) event.onError(new PPMessageException(LOG_TXT_CONTENT_EMPTY));
            } else {
                Utils.getTxtUploader().upload(txtContent, new Uploader.OnUploadingListener() {

                    @Override
                    public void onError(Exception e) {
                        if (event != null) event.onError(e);
                    }

                    @Override
                    public void onComplected(JSONObject response) {
                        if (event != null) {
                            try {
                                event.onCompleted(getAPIJSonObject(response.getString("fuuid")));
                            } catch (JSONException e) {
                                L.e(e);
                            }
                        }
                    }

                });
            }
        }
    }

    private JSONObject getAPIJSonObject(String txtFid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fid", txtFid);
        } catch (JSONException e) {
            L.e(e);
        }
        return jsonObject;
    }
}
