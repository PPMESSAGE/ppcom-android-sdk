package com.ppmessage.sdk.core.query;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.common.Conversation;

/**
 * Created by ppmessage on 5/9/16.
 */
class DBUpdate implements IUpdate {

    private PPMessageSDKDBHelper dbHelper;

    public DBUpdate(Context context) {
        dbHelper = PPMessageSDKDBHelper.getInstance(context);
    }

    @Override
    public void updateConversation(Conversation conversation, OnUpdateCallback updateCallback) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_UUID, conversation.getConversationUUID());
        values.put(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_ICON, conversation.getConversationIcon());
        values.put(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_NAME, conversation.getConversationName());
        values.put(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_TYPE, conversation.getConversationType());

        db.insertWithOnConflict(PPMessageSDKContract.ConversationEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if (updateCallback != null) updateCallback.onCompleted(conversation);
    }

    @Override
    public void updateUser(User user, OnUpdateCallback updateCallback) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_ICON, user.getIcon());
        values.put(PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_NAME, user.getName());
        values.put(PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_UUID, user.getUuid());

        db.insertWithOnConflict(PPMessageSDKContract.UserEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if (updateCallback != null) updateCallback.onCompleted(user);
    }

}
