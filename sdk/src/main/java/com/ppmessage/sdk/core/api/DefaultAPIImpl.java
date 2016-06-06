package com.ppmessage.sdk.core.api;

import com.ppmessage.sdk.core.PPMessageSDK;

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
        super.post("/PP_GET_USER_UUID", requestParam, completedCallback);
    }

    @Override
    public void ackMessage(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/ACK_MESSAGE", requestParam, completedCallback);
    }

    @Override
    public void getMessageHistory(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_HISTORY_MESSAGE", requestParam, completedCallback);
    }

    @Override
    public void createDevice(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_CREATE_DEVICE", requestParam, completedCallback);
    }

    @Override
    public void updateDevice(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_UPDATE_DEVICE", requestParam, completedCallback);
    }

    @Override
    public void createAnonymousUser(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_CREATE_ANONYMOUS", requestParam, completedCallback);
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
    public void getAppOrgGroupList(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_APP_ORG_GROUP_LIST", requestParam, completedCallback);
    }

    @Override
    public void getWaitingQueueLength(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_AMD_QUEUE_LENGTH", requestParam, completedCallback);
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
    public void loadMessageHistorys(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_PAGE_HISTORY_MESSAGE", requestParam, completedCallback);
    }

    @Override
    public void cancelWaitingCreateConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_CANCEL_WAITING_CREATE_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void createPPComConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PPCOM_CREATE_CONVERSATION", requestParam, completedCallback);
    }

    @Override
    public void getConversationUserList(JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        super.post("/PP_GET_CONVERSATION_USER_LIST", requestParam, completedCallback);
    }

}
