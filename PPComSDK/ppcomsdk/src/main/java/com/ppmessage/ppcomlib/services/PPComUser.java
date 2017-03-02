package com.ppmessage.ppcomlib.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKException;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageException;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 1. get anonymous / email user
 * 2. update user info // We don't care the update result
 * 3. create and update device // We also don't care the result
 * <p></p>
 * Example:
 * <p></p>
 * <pre>
 *
 *     1. -Async get ppcom user
 *     PPComSDK sdk = PPComSDK.getInstance();
 *     PPComUser ppcomUser = new PPComUser(sdk);
 *
 *     ppcomUser.getPPComUser(new OnGetPPComUserEvent() {
 *
 *         void onCompleted(User user) {
 *             // success: user != null
 *             // error: user == null
 *         }
 *     } );
 *
 *     2. -get ppcom cached user
 *     User user = ppcomUser.getUser();
 * </pre>
 * <p></p>
 * Created by ppmessage on 5/13/16.
 */
public final class PPComUser {

    private static final String SHARED_PREF_NAME = "user_pref";
    private static final String SHARED_PREF_TRACE_ID_KEY = "anonymous_user_trace_id";

    private static final String DEVICE_OS_TYPE = "AND";

    private static final String LOG_UPDATE_USER_INFO_ERROR = "[PPComUser] update user info failed";
    private static final String LOG_GET_DEVICE_UUID_ERROR = "[PPComUser] get device_uuid failed";
    private static final String LOG_UPDATE_DEVICE_INFO_ERROR = "[PPComUser] update device info failed";
    private static final String LOG_CREATE_USER_INFO_ERROR = "[PPComUser] create user info failed";

    public interface OnGetPPComUserEvent {
        void onCompleted(User user);
    }

    private PPMessageSDK messageSDK;
    private PPComSDK sdk;
    private Context context;

    private User user; // cached user

    public PPComUser(PPComSDK sdk) {
        this.sdk = sdk;
        this.context = sdk.getConfiguration().getContext();
        this.messageSDK = sdk.getPPMessageSDK();
    }

    public User getUser() {
        return user;
    }

    public void getUser(OnGetPPComUserEvent event) {
        if (user != null) {
            if (event != null) event.onCompleted(user);
            return;
        }
        createUser(event);
    }

    private void createUser(final OnGetPPComUserEvent event) {
        if (isAnonymousUser()) {
            createAnonymousUser(getAnonymousUserTraceUUID(), event);
        } else if (isEntUser()) {
            createEntUser(event);
        } else if (isEmailUser()) {
            createEmailUser(event);
        } else {
            L.w(LOG_CREATE_USER_INFO_ERROR);
        }
    }

    // 1. create anonymous user
    private void createAnonymousUser(String ppcomTraceUUID, final OnGetPPComUserEvent event) {
        String traceUUID = ppcomTraceUUID;
        String appUUID = sdk.getConfiguration().getAppUUID();

        JSONObject params = new JSONObject();
        try {
            params.put("app_uuid", appUUID);
            params.put("ppcom_trace_uuid", traceUUID);
            params.put("is_app_user", true);
            params.put("is_browser_user", false);
            // params.put("ent_user_data", sdk.getConfiguration().getEntUserData());
            // params.put("ent_user_type", sdk.getConfiguration().getEntUserType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        L.d("CREATE_ANONYMOUS_USER");

        // Create anonymous user
        messageSDK.getAPI().createAnonymousUser(params, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                User user = User.parse(jsonResponse);
                PPComUser.this.user = user; // Cache It

                // We don't care the update result
                updateUserInfo(user);

                updateDevice(user, event);
            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }
        });
    }

    // 1. first get user_uuid by user_email
    // 2. second get user detail info
    private void createEmailUser(final OnGetPPComUserEvent event) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_icon", sdk.getConfiguration().getUserIcon());
            jsonObject.put("user_email", sdk.getConfiguration().getUserEmail());
            jsonObject.put("user_fullname", sdk.getConfiguration().getUserFullName());
            jsonObject.put("ent_user_data", sdk.getConfiguration().getEntUserData());
            jsonObject.put("ent_user_type", sdk.getConfiguration().getEntUserType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        messageSDK.getAPI().getUserUUID(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                String userUUID = null;
                try {
                    userUUID = jsonResponse.getString("user_uuid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getUserDetailInfo(userUUID, event);
            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }
        });
    }

    private void createEntUser(final OnGetPPComUserEvent event) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_icon", sdk.getConfiguration().getUserIcon());
            jsonObject.put("user_email", sdk.getConfiguration().getUserEmail());
            jsonObject.put("user_fullname", sdk.getConfiguration().getUserFullName());
            jsonObject.put("ent_user_uuid", sdk.getConfiguration().getEntUserUUID());
            jsonObject.put("ent_user_data", sdk.getConfiguration().getEntUserData());
            jsonObject.put("ent_user_type", sdk.getConfiguration().getEntUserType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        messageSDK.getAPI().getUserUUID(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                String userUUID = null;
                try {
                    userUUID = jsonResponse.getString("user_uuid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getUserDetailInfo(userUUID, event);
            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }
        });
    }

    private void getUserDetailInfo(String userUUID, final OnGetPPComUserEvent event) {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", userUUID);
            jsonObject.put("type", "DU");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        messageSDK.getAPI().getUserDetailInfo(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {

                try {
                    if (jsonObject.getInt("error_code") == 0) {
                        User user = User.parse(jsonResponse);
                        PPComUser.this.user = user;
                        updateDevice(user, event);
                    } else {
                        if (event != null) event.onCompleted(PPComUser.this.user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(PPComUser.this.user);
            }
        });
    }

    private void updateUserInfo(User user) {
        JSONObject jsonObject = new JSONObject();

        String userUUID = user.getUuid();
        String userName = sdk.getConfiguration().getUserFullName() != null ? sdk.getConfiguration().getUserFullName() : user.getName();
        String userIcon = sdk.getConfiguration().getUserIcon() != null ? sdk.getConfiguration().getUserIcon() : user.getIcon();
        String userEmail = sdk.getConfiguration().getUserEmail();
        String entUserUuid = sdk.getConfiguration().getEntUserUUID();
        String entUserData = sdk.getConfiguration().getEntUserData();
        String entUserType = sdk.getConfiguration().getEntUserType();

        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", userUUID);
            jsonObject.put("user_fullname", userName);
            jsonObject.put("user_icon", userIcon);
            jsonObject.put("user_email", userEmail);
            jsonObject.put("ent_user_uuid", entUserUuid);
            jsonObject.put("ent_user_data", entUserData);
            jsonObject.put("ent_user_type", entUserType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        messageSDK.getAPI().updateUserInfo(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
            }

            @Override
            public void onCancelled() {
                L.w(LOG_UPDATE_USER_INFO_ERROR);
            }

            @Override
            public void onError(int errorCode) {
                L.w(LOG_UPDATE_USER_INFO_ERROR);
            }
        });

    }

    private void updateDevice(final User user, final OnGetPPComUserEvent event) {
        String deviceUUID = Utils.getDeviceUUID(context);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", user.getUuid());
            jsonObject.put("device_ostype", DEVICE_OS_TYPE);
            jsonObject.put("jpush_registration_id", sdk.getConfiguration().getJpushRegistrationId());
            jsonObject.put("ppcom_trace_uuid", getAnonymousUserTraceUUID());
            jsonObject.put("device_id", deviceUUID);
        } catch (JSONException e) {
            L.e(e);
        }

        messageSDK.getAPI().createDevice(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                try {
                    String deviceUUID = jsonResponse.getString("device_uuid");
                    user.setDeviceUUID(deviceUUID);

                    updateDeviceOSType(deviceUUID);
                } catch (JSONException e) {
                    L.e(e);
                }
                if (event != null) {
                    event.onCompleted(user);
                }
            }

            @Override
            public void onCancelled() {
                L.w(LOG_GET_DEVICE_UUID_ERROR);
            }

            @Override
            public void onError(int errorCode) {
                L.w(LOG_GET_DEVICE_UUID_ERROR);
            }
        });
    }

    private void updateDeviceOSType(final String deviceUUID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_uuid", deviceUUID);
            jsonObject.put("device_ostype", DEVICE_OS_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageSDK.getAPI().updateDevice(jsonObject, null);
    }

    private boolean isAnonymousUser() {
        return (sdk.getConfiguration().getUserEmail() == null
                && sdk.getConfiguration().getEntUserUUID() == null);
    }

    private boolean isEntUser() {
        return (sdk.getConfiguration().getEntUserUUID() != null);
    }

    private boolean isEmailUser() {
        return (sdk.getConfiguration().getUserEmail() != null);
    }

    public String getAnonymousUserTraceUUID() {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sp.getString(SHARED_PREF_TRACE_ID_KEY, null) == null) {
            sp.edit().putString(SHARED_PREF_TRACE_ID_KEY, Utils.randomUUID()).commit();
        }
        return sp.getString(SHARED_PREF_TRACE_ID_KEY, null);
    }

    public void updateUserInfo(JSONObject jsonObject) {
        if (sdk.getStartupHelper().getComUser().getUser() == null) {
            throw new PPMessageException(LOG_UPDATE_USER_INFO_ERROR);
        }

        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", sdk.getStartupHelper().getComUser().getUser().getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageSDK.getAPI().updateUserInfo(jsonObject, null);
        return;
    }

    public void updateDeviceJpushRegistrationId() {
        if (sdk.getStartupHelper().getComUser().getUser() == null) {
            throw new PPMessageException(LOG_UPDATE_DEVICE_INFO_ERROR);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_uuid", sdk.getStartupHelper().getComUser().getUser().getDeviceUUID());
            jsonObject.put("jpush_registration_id", sdk.getConfiguration().getJpushRegistrationId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageSDK.getAPI().updateDevice(jsonObject, null);
        return;
    }

}
