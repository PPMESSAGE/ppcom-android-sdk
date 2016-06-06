package com.ppmessage.sdk.core.utils;

import com.ppmessage.sdk.core.api.BaseHttpRequest;
import com.ppmessage.sdk.core.api.OnHttpRequestCompleted;

/**
 * Here is a sample code:
 *
 * <pre>
 *     TxtLoader txtLoader = new TxtLoader();
 *     txtLoader.loadTxt("b2cced63-06e0-11e6-b73b-acbc327f19e9", new TxtLoader.OnTxtLoadEvent() {
 *          @Override
 *          public void onCompleted(String text) {
 *              // get large text: text
 *          }
 *     });
 * </pre>
 *
 * Created by ppmessage on 5/6/16.
 */
public class TxtLoader extends BaseHttpRequest {

    /**
     * Txt load event
     */
    public interface OnTxtLoadEvent {

        /**
         * Txt load finished
         *
         * @param text null if load failed, else not null
         */
        void onCompleted(String text);

    }

    /**
     * Download url as large text
     *
     * @param url fileId or fileUrl
     * @param loadListener
     */
    public void loadTxt(String url, final OnTxtLoadEvent loadListener) {
        String downloadUrl = Utils.getFileDownloadUrl(url);
        get(downloadUrl, null, new OnHttpRequestCompleted() {

            @Override
            public void onResponse(String response) {
                if (loadListener != null) loadListener.onCompleted(response);
            }

            @Override
            public void onCancelled() {
                if (loadListener != null) loadListener.onCompleted(null);
            }

            @Override
            public void onError(int errorCode) {
                if (loadListener != null) loadListener.onCompleted(null);
            }
        });
    }

}
