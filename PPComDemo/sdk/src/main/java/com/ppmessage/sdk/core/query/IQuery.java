package com.ppmessage.sdk.core.query;

/**
 * Created by ppmessage on 5/9/16.
 */
public interface IQuery {

    interface OnQueryCallback {
        void onCompleted(Object object);
    }

    void queryConversation(String conversationUUID, OnQueryCallback queryCallback);
    void queryUser(String userUUID, OnQueryCallback queryCallback);

}
