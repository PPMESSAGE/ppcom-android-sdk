package com.ppmessage.sdk.core.query;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ppmessage.sdk.core.L;

/**
 * Created by ppmessage on 5/9/16.
 */
class PPMessageSDKDBHelper extends SQLiteOpenHelper {

    private static final String LOG_DBPATH = "DB path: %s";

    // === TABLE: SQL_CONVERSATION ===

    private static final String SQL_CREATE_CONVERSATION_ENTRY = "CREATE TABLE IF NOT EXISTS " +
            PPMessageSDKContract.ConversationEntry.TABLE_NAME + "(" +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_UUID + " TEXT PRIMARY KEY," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_ICON + " TEXT," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_NAME + " TEXT," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_CONVERSATION_TYPE + " TEXT," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_ASSIGNED_UUID + " TEXT," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_GROUP_UUID + " TEXT," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_STATUS + " TEXT," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_USER_UUID + " TEXT," +
            PPMessageSDKContract.ConversationEntry.COLUMN_NAME_LATEST_TASK_UUID + " TEXT" +
            ")";
    private static final String SQL_DELETE_CONVERSATION_ENTRY = "DELETE TABLE IF EXISTS " + PPMessageSDKContract.ConversationEntry.TABLE_NAME;

    // === TABLE: SQL_USER ===

    private static final String SQL_CREATE_USER_ENTRY = "CREATE TABLE IF NOT EXISTS " +
            PPMessageSDKContract.UserEntry.TABLE_NAME + "(" +
            PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_UUID + " TEXT PRIMARY KEY," +
            PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_ICON + " TEXT," +
            PPMessageSDKContract.UserEntry.COLUMN_NAME_USER_NAME + " TEXT" +
            ")";
    private static final String SQL_DELETE_USER_ENTRY = "DELETE TABLE IF EXISTS " + PPMessageSDKContract.UserEntry.TABLE_NAME;

    private static final String DB_NAME = null; // In-memory database
    private static final int DB_VERSION = 1; // version: 1

    private static PPMessageSDKDBHelper ourInstance = null;

    public static PPMessageSDKDBHelper getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (PPMessageSDKDBHelper.class) {
                ourInstance = new PPMessageSDKDBHelper(context);
            }
        }
        return ourInstance;
    }

    private PPMessageSDKDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        L.d(LOG_DBPATH, db.getPath());
        db.execSQL(SQL_CREATE_CONVERSATION_ENTRY);
        db.execSQL(SQL_CREATE_USER_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CONVERSATION_ENTRY);
        db.execSQL(SQL_DELETE_USER_ENTRY);
    }

}
