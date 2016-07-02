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
    private MemoryQuery memoryQuery;

    public DataCenter(PPMessageSDK sdk, Context context) {
        dbQuery = new DBQuery(context);
        apiQuery = new APIQuery(sdk);
        dbUpdate = new DBUpdate(context);
        memoryQuery = new MemoryQuery();
    }

    @Override
    public void queryConversation(final String conversationUUID, final OnQueryCallback queryCallback) {
        // Query Memory
        final Conversation memoryConversation = memoryQuery.queryConversation(conversationUUID);
        if (memoryConversation != null) {
            if (queryCallback != null) {
                queryCallback.onCompleted(memoryConversation);
            }
            return;
        }

        // Query DB
        dbQuery.queryConversation(conversationUUID, new OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                if (object == null) {
                    apiQuery.queryConversation(conversationUUID, new OnQueryCallback() {
                        @Override
                        public void onCompleted(Object object) {
                            if (object != null) {
                                // Cache to DB
                                updateConversation((Conversation)object, null);
                            }
                            if (queryCallback != null) {
                                queryCallback.onCompleted(object);
                            }
                        }
                    });
                } else {
                    // Cache to Memory
                    if (object instanceof Conversation) {
                        memoryQuery.cacheConversation((Conversation) object);
                    }
                    if (queryCallback != null) {
                        queryCallback.onCompleted(object);
                    }
                }
            }
        });
    }

    @Override
    public void queryUser(final String userUUID, final OnQueryCallback queryCallback) {
        // Query Memory
        User userInMemory = memoryQuery.queryUser(userUUID);
        if (userInMemory != null) {
            if (queryCallback != null) {
                queryCallback.onCompleted(userInMemory);
            }
            return;
        }

        // Query DB
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
                    // Cache to Memory
                    if (object instanceof User) {
                        memoryQuery.cacheUser((User) object);
                    }
                    if (queryCallback != null) {
                        queryCallback.onCompleted(object);
                    }
                }
            }
        });
    }

    @Override
    public void updateConversation(Conversation conversation, OnUpdateCallback updateCallback) {
        memoryQuery.cacheConversation(conversation);
        dbUpdate.updateConversation(conversation, updateCallback);
    }

    @Override
    public void updateUser(User user, OnUpdateCallback updateCallback) {
        memoryQuery.cacheUser(user);
        dbUpdate.updateUser(user, updateCallback);
    }

}
