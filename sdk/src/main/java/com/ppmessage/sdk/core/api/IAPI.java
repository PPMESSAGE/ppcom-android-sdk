package com.ppmessage.sdk.core.api;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/5/16.
 */
public interface IAPI {

    /**
     * Get user UUID by user_email
     *
     * @param requestParam
     * @param completedCallback
     */
    void getUserUUID(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Ack message when receive a new message
     *
     * @param requestParam
     * @param completedCallback
     */
    void ackMessage(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get Message Historys
     *
     * @param requestParam
     * @param completedCallback
     */
    void getMessageHistory(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Create a device
     *
     * @param requestParam
     * @param completedCallback
     */
    void createDevice(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Update device
     *
     * @param requestParam
     * @param completedCallback
     */
    void updateDevice(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Create an anonymous user
     *
     * @param requestParam
     * @param completedCallback
     */
    void createAnonymousUser(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get all unacked messages
     *
     * @param requestParam
     * @param completedCallback
     */
    void getUnackedMessages(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get PPCom Default Conversation
     *
     * @param requestParam
     * @param completedCallback
     */
    void getPPComDefaultConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get App org groups
     *
     * @param requestParam
     * @param completedCallback
     */
    void getAppOrgGroupList(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get AMD Waiting Queue Length
     *
     * @param requestParam
     * @param completedCallback
     */
    void getWaitingQueueLength(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get App Info
     *
     * @param requestParam
     * @param completedCallback
     */
    void getAppInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Send message by api
     *
     * @param requestParam
     * @param completedCallback
     */
    void sendMessage(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get conversation info
     *
     * @param requestParam
     * @param completedCallback
     */
    void getConversationInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get user detail info
     *
     * @param requestParam
     * @param completedCallback
     */
    void getUserDetailInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Update user info
     *
     * @param requestParam
     * @param completedCallback
     */
    void updateUserInfo(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get conversation list
     *
     * @param requestParam
     * @param completedCallback
     */
    void getConversationList(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Load message historys
     *
     * @param requestParam
     * @param completedCallback
     */
    void loadMessageHistorys(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Cancel waiting create conversation
     *
     * @param requestParam
     * @param completedCallback
     */
    void cancelWaitingCreateConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Create PPCom Conversation
     *
     * @param requestParam
     * @param completedCallback
     */
    void createPPComConversation(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

    /**
     * Get conversation user list
     *
     * @param requestParam
     * @param completedCallback
     */
    void getConversationUserList(JSONObject requestParam, OnAPIRequestCompleted completedCallback);

}
