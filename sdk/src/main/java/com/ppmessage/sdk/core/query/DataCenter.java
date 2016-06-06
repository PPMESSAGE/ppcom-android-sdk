package com.ppmessage.sdk.core.query;

import android.content.Context;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.common.Conversation;

/**
 * Created by ppmessage on 5/9/16.
 */
public class DataCenter implements IDataCenter {

    private IQuery dbQuery;
    private IQuery apiQuery;
    private IUpdate dbUpdate;

    public DataCenter(PPMessageSDK sdk, Context context) {
        dbQuery = new DBQuery(context);
        apiQuery = new APIQuery(sdk);
        dbUpdate = new DBUpdate(context);
    }

    @Override
    public void queryConversation(final String conversationUUID, final OnQueryCallback queryCallback) {
        dbQuery.queryConversation(conversationUUID, new OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                if (object == null) {
                    apiQuery.queryConversation(conversationUUID, new OnQueryCallback() {
                        @Override
                        public void onCompleted(Object object) {
                            if (object != null) {
                                updateConversation((Conversation)object, null);
                            }
                            if (queryCallback != null) {
                                queryCallback.onCompleted(object);
                            }
                        }
                    });
                } else {
                    if (queryCallback != null) {
                        queryCallback.onCompleted(object);
                    }
                }
            }
        });
    }

    @Override
    public void queryUser(final String userUUID, final OnQueryCallback queryCallback) {
        dbQuery.queryUser(userUUID, new OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                if (object == null) {
                    apiQuery.queryUser(userUUID, new OnQueryCallback() {
                        @Override
                        public void onCompleted(Object object) {
                            if (object != null) {
                                updateUser((User)object, null);
                            }
                            if (queryCallback != null) {
                                queryCallback.onCompleted(object);
                            }
                        }
                    });
                } else {
                    if (queryCallback != null) {
                        queryCallback.onCompleted(object);
                    }
                }
            }
        });
    }

    @Override
    public void updateConversation(Conversation conversation, OnUpdateCallback updateCallback) {
        dbUpdate.updateConversation(conversation, updateCallback);
    }

    @Override
    public void updateUser(User user, OnUpdateCallback updateCallback) {
        dbUpdate.updateUser(user, updateCallback);
    }

}
