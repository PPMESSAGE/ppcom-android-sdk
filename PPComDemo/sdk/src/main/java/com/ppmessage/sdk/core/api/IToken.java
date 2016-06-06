package com.ppmessage.sdk.core.api;

/**
 * Created by ppmessage on 5/5/16.
 */
public interface IToken {

    interface OnRequestTokenEvent {

        void onGetToken(String accessToken);

    }

    /**
     * Get api token by appUUID, generally this is used by `PPCOM`
     *
     * @param appUUID
     * @param completedCallback
     */
    void getApiToken(String appUUID, OnRequestTokenEvent completedCallback);

    /**
     * Get api token by userEmail and userPassword, generally this is used by `PPKEFU`
     *
     * @param userEmail
     * @param userPassword
     * @param completedCallback
     */
    void getApiToken(String userEmail, String userPassword, OnRequestTokenEvent completedCallback);

}
