package com.ppmessage.sdk.core.query;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.common.Conversation;

/**
 * Created by ppmessage on 5/9/16.
 */
class DBQuery implements IQuery {

    private PPMessageSDKDBHelper dbHelper;
    private static final String SQL_SELECT_CONVERSATION = "SELECT " + PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_NAME + "," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_ICON + "," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_TYPE + "," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_UUID +
            " FROM " + PPMessageSDKContract.ConversationEntry.TABLE_NAME + " WHERE " +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_UUID + " = ?";
    private static final String SQL_SELECT_USER = "SELECT " + PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_ICON + "," +
            PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_NAME + "," +
            PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_UUID +
            " FROM " + PPMessageSDKContract.UserEntry.TABLE_NAME + " WHERE " +
            PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_UUID + " = ?";

    public DBQuery(Context context) {
        dbHelper = PPMessageSDKDBHelper.getInstance(context);
    }

    @Override
    public void queryConversation(String conversationUUID, OnQueryCallback queryCallback) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(SQL_SELECT_CONVERSATION, new String[]{conversationUUID});
        Conversation conversation = null;
        if (cursor.moveToFirst()) {
            conversation = new Conversation();
            conversation.setConversationIcon(cursor.getString(cursor.getColumnIndex(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_ICON)));
            conversation.setConversationName(cursor.getString(cursor.getColumnIndex(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_NAME)));
            conversation.setConversationUUID(cursor.getString(cursor.getColumnIndex(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_UUID)));
            conversation.setConversationType(cursor.getString(cursor.getColumnIndex(PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_TYPE)));
        }
        cursor.close();

        if (queryCallback != null) {
            queryCallback.onCompleted(conversation);
        }
    }

    @Override
    public void queryUser(String userUUID, OnQueryCallback queryCallback) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_USER, new String[]{userUUID});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setUuid(cursor.getString(cursor.getColumnIndex(PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_UUID)));
            user.setIcon(cursor.getString(cursor.getColumnIndex(PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_ICON)));
            user.setName(cursor.getString(cursor.getColumnIndex(PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_NAME)));
        }

        cursor.close();

        if (queryCallback != null) {
            queryCallback.onCompleted(user);
        }
    }

}
