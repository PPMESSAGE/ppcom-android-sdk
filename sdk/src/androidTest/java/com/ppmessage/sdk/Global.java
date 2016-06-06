package com.ppmessage.sdk;

import android.support.test.InstrumentationRegistry;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.PPMessageSDKConfiguration;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public class Global {

    public static final String TEST_APP_UUID = "c56adc0a-1b54-11e6-bc9f-acbc327f19e9";

    public static final String TEST_PPCOM_USER_UUID = "a69a5407-1bd7-11e6-9e10-acbc327f19e9";
    public static final String TEST_PPCOM_USER_DEVICE_UUID = "a6a68321-1bd7-11e6-ba05-acbc327f19e9";
    public static final String TEST_PPCOM_CONVERSATION_UUID = "eae07f02-1be1-11e6-922e-acbc327f19e9";

    public static final String TEST_PPKEFU_USER_UUID = "c5657363-1b54-11e6-aaea-acbc327f19e9"; // Guijin Ding

    public static PPMessageSDK getPPMessageSDK() {
        PPMessageSDK sdk = PPMessageSDK.getInstance();
        sdk.init(new PPMessageSDKConfiguration
                .Builder(InstrumentationRegistry.getContext())
                .setAppUUID(TEST_APP_UUID)
                .setEnableDebugLogging(true)
                .setEnableLogging(true)
                .build());

        sdk.getNotification().config(new INotification.Config() {

            private User activeUser;

            @Override
            public String getAppUUID() {
                return TEST_APP_UUID;
            }

            @Override
            public String getApiToken() {
                return null;
            }

            @Override
            public User getActiveUser() {
                if (activeUser == null) {
                    activeUser = new User();
                    activeUser.setUuid(TEST_PPCOM_USER_UUID);
                    activeUser.setDeviceUUID(TEST_PPCOM_USER_DEVICE_UUID);
                    activeUser.setServiceUser(false);
                }
                return activeUser;
            }

        });
        return sdk;
    }

    public static abstract class Message {

        public static JSONObject getWSTextMessageJsonObject() {
            JSONObject json = new JSONObject();

            try {
                json.put("bo", "ABC");
                json.put("ci", TEST_PPCOM_CONVERSATION_UUID);
                json.put("ct", "C2P");
                json.put("fi", TEST_PPCOM_USER_UUID);
                json.put("ft", "DU");
                json.put("id", Utils.randomUUID());
                json.put("ms", "TEXT");
                json.put("mt", "NOTI");
                json.put("pid", "7c4dbdcc-15ca-11e6-9c58-acbc327f19e9");
                json.put("ti", TEST_PPKEFU_USER_UUID);
                json.put("tl", null);
                json.put("ts", System.currentTimeMillis() * 1000);
                json.put("tt", "AP");

                JSONObject fromUser = new JSONObject();
                fromUser.put("updatetime", System.currentTimeMillis() * 1000);
                fromUser.put("user_email", "dingguijin@gmail.com");
                fromUser.put("user_fullname", "Guijin Ding");
                fromUser.put("user_icon", "875977d1-10e6-11e6-b932-acbc327f19e9");
                fromUser.put("uuid", TEST_PPKEFU_USER_UUID);

                json.put("from_user", fromUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

        public static JSONObject getWSTxtMessageJsonObject() {
            JSONObject wsTestJsonObject = getWSTextMessageJsonObject();
            try {
                wsTestJsonObject.put("ms", "TXT");

                JSONObject txtMediaJsonObject = new JSONObject();
                txtMediaJsonObject.put("fid", "5b746aba-1379-11e6-9918-acbc327f19e9");

                wsTestJsonObject.put("bo", txtMediaJsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return wsTestJsonObject;
        }

        public static JSONObject getWSImageMessageJsonObject() {
            JSONObject wsTextJsonObject = getWSTextMessageJsonObject();
            try {
                wsTextJsonObject.put("ms", "IMAGE");

                JSONObject imageJsonObject = new JSONObject();
                imageJsonObject.put("thum", "f4a54ffa-da18-11e5-8049-acbc327f19e9");
                imageJsonObject.put("mime", "image/jpeg");
                imageJsonObject.put("orig", "f490a53d-da18-11e5-9045-acbc327f19e9");
                imageJsonObject.put("orig_width", 300);
                imageJsonObject.put("orig_height", 400);
                imageJsonObject.put("thum_width", 150);
                imageJsonObject.put("thum_height", 200);

                wsTextJsonObject.put("bo", imageJsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return wsTextJsonObject;
        }

        public static JSONObject getWSFileMessageJsonObject() {
            JSONObject wsTextJsonObject = getWSTextMessageJsonObject();
            try {
                wsTextJsonObject.put("ms", "FILE");

                JSONObject fileJsonObject = new JSONObject();
                fileJsonObject.put("mime", "application/x-pkcs12");
                fileJsonObject.put("name", "dis.p12");
                fileJsonObject.put("fid", "1b2bdc0c-dc65-11e5-9251-acbc327f19e9");
                fileJsonObject.put("size", 3249);

                wsTextJsonObject.put("bo", fileJsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return wsTextJsonObject;
        }

    }

    public static User getUser() {
        User user = new User();
        user.setUuid(TEST_PPKEFU_USER_UUID);
        user.setIcon("875977d1-10e6-11e6-b932-acbc327f19e9");
        user.setName("Guijin Ding");
        return user;
    }

}
