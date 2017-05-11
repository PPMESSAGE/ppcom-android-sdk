package com.ppmessage.sdk.core.api;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.L;

import org.json.JSONObject;


/**
 * Created by ppmessage on 5/5/16.
 */
public class DefaultAPIImpl extends BaseAPIRequest implements IAPI {

    public DefaultAPIImpl(PPMessageSDK sdk) {
        super(sdk);
    }

    @Override
    public void getUserUUID(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPCOM_GET_USER_UUID", requestParam, completedCallback);
    }

    @Override
    public void ackMessage(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/ACK_MESSAGE", requestParam, completedCallback);
    }


    @Override
    public void createPPComDevice(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPCOM_CREATE_DEVICE", requestParam, completedCallback);
    }

    @Override
    public void updateDevice(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_UPDATE_DEVICE", requestParam, completedCallback);
    }

    @Override
    public void createAnonymousUser(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPCOM_CREATE_ANONYMOUS", requestParam, completedCallback);
    }

    @Override
    public void getUnackedMessages(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/GET_UNACKED_MESSAGES", requestParam, completedCallback);
    }

    @Override
    public void getPPComDefaultConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPCOM_GET_DEFAULT_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void createPPComDefaultConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPCOM_CREATE_DEFAULT_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void getAppOrgGroupList(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_APP_ORG_GROUP_LIST", requestParam, completedCallback);
    }


    @Override
    public void getAppInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_APP_INFO", requestParam, completedCallback);
    }

    @Override
    public void sendMessage(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_SEND_MESSAGE", requestParam, completedCallback);
    }

    @Override
    public void getConversationInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_CONVERSATION_INFO", requestParam, completedCallback);
    }

    @Override
    public void getUserDetailInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_USER_DETAIL", requestParam, completedCallback);
    }

    @Override
    public void updateUserInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_UPDATE_USER", requestParam, completedCallback);
    }

    @Override
    public void getConversationList(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_USER_CONVERSATION_LIST", requestParam, completedCallback);
    }

    @Override
    public void getHistoryMessage(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_PAGE_HISTORY_MESSAGE", requestParam, completedCallback);
    }


    @Override
    public void createPPComConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPCOM_CREATE_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void getConversationUserList(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_CONVERSATION_USER_LIST", requestParam, completedCallback);
    }

    @Override
    public void login(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPKEFU_LOGIN", requestParam, completedCallback);
    }

    @Override
    public void logout(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPKEFU_LOGOUT", requestParam, completedCallback);
    }

    @Override
    public void getServiceUserList(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPKEFU_GET_APP_SERVICE_USER_LIST", requestParam, completedCallback);
    }

    @Override
    public void pageConversationList(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_PAGE_USER_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void createConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_CREATE_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void closeConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_CLOSE_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void pageUnackedMessages(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_PAGE_UNACKED_MESSAGE", requestParam, completedCallback);
    }

    @Override
    public void emailValid(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_IS_EMAIL_VALID", requestParam, completedCallback);
    }


    @Override
    public void deviceValid(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_VALIDATE_ONLINE_DEVICE", requestParam, completedCallback);
    }


}
