package com.ppmessage.sdk.core.query;

import android.provider.BaseColumns;

/**
 * Created by ppmessage on 5/9/16.
 */
final class PPMessageSDKContract {

    public PPMessageSDKContract() {}

    public static abstract class ConversationEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversations";

        public static final String COLUMN_NAME_CONVERSATION_UUID = "conversation_uuid";
        public static final String COLUMN_NAME_CONVERSATION_NAME = "conversation_name";
        public static final String COLUMN_NAME_CONVERSATION_ICON = "conversation_icon";
        public static final String COLUMN_NAME_CONVERSATION_TYPE = "conversation_type";
        public static final String COLUMN_NAME_GROUP_UUID = "group_uuid";
        public static final String COLUMN_NAME_USER_UUID = "user_uuid";
        public static final String COLUMN_NAME_ASSIGNED_UUID = "assigned_uuid";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_LATEST_TASK_UUID = "latest_task";

    }

    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "device_users";

        public static final String COLUMN_NAME_USER_UUID = "user_uuid";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_USER_ICON = "user_icon";

    }

}
