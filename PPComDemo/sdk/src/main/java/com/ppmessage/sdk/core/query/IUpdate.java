package com.ppmessage.sdk.core.query;

import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.common.Conversation;

/**
 * Created by ppmessage on 5/9/16.
 */
public interface IUpdate {

    interface OnUpdateCallback {
        void onCompleted(Object object);
    }

    void updateConversation(Conversation conversation, OnUpdateCallback updateCallback);
    void updateUser(User user, OnUpdateCallback updateCallback);

}
