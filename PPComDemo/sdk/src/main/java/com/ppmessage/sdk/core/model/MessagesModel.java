package com.ppmessage.sdk.core.model;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Example:
 *
 * <pre>
 *     - 1. Async load historys
 * </pre>
 *
 * Created by ppmessage on 5/17/16.
 */
public class MessagesModel {

    private static final String LOG_MESSAGE_EXIT = "[MessagesModel] message: %s exist";

    private PPMessageSDK messageSDK;

    /**
     * Map messages to conversationUUID
     *
     * <pre>
     *     - conversation-A-uuid: [message-1, message-2, message-3, message-4, ..., message-n, ...]
     *     - conversation-B-uuid: [message-1, message-2, message-3, message-4, ..., message-n, ...]
     *     - conversation-C-uuid: [message-1, message-2, message-3, message-4, ..., message-n, ...]
     * </pre>
     *
     */
    private Map<String, List<PPMessage>> messagesToConversationUUIDMap = new HashMap<>();

    /**
     * [message-uuid-1, message-uuid-2, ..., message-uuid-n, ...]
     */
    private Set<String> messageUUIDSet = new HashSet<>();

    private MessageHistoryLoader historyLoader;

    public MessagesModel(PPMessageSDK sdk) {
        this.messageSDK = sdk;

        MessageHistorysModel historyModel = new MessageHistorysModel(sdk);
        this.historyLoader = new MessageHistoryLoader(historyModel);
    }

    /**
     * Append messages to tail
     *
     * @param message
     */
    public boolean add(PPMessage message) {
        ensureConversationUUIDKeyExist(message);
        List<PPMessage> messageList = getMessageList(message.getConversation().getConversationUUID());
        return appendMessageToList(messageList, message, messageList.size());
    }

    /**
     * Add all messages at the head, messages MUST have the same conversation_uuid
     *
     * @param messageList
     * @return
     */
    public List<PPMessage> addAllAtHead(List<PPMessage> messageList) {
        return addAll(0, messageList);
    }

    /**
     * Add all messages, messages MUST have the same conversation_uuid
     *
     * @param location
     * @param messageList
     */
    public List<PPMessage> addAll(int location, List<PPMessage> messageList) {

        if (messageList == null || messageList.isEmpty()) return messageList;

        List<PPMessage> deleted = new ArrayList<>();
        for (PPMessage m:messageList) {
            if (exist(m)) deleted.add(m);
        }
        if (!deleted.isEmpty()) {
            messageList.removeAll(deleted);
        }

        if (!messageList.isEmpty()) {
            ensureConversationUUIDKeyExist(messageList.get(0));
            List<PPMessage> existedMessages = getMessageList(messageList.get(0).getConversation().getConversationUUID());
            for (PPMessage m : messageList) {
                messageUUIDSet.add(m.getMessageID());
            }
            existedMessages.addAll(location, messageList);
        }

        return messageList;
    }

    /**
     * Load history
     *
     * @return
     */
    public MessageHistoryLoader getHistoryLoader() {
        return historyLoader;
    }

    /**
     * Get messages by conversationUUID
     *
     * @param conversationUUID
     * @return
     */
    public List<PPMessage> getMessageList(String conversationUUID) {
        ensureConversationUUIDKeyExist(conversationUUID);
        return messagesToConversationUUIDMap.get(conversationUUID);
    }

    /**
     * Find message
     *
     * @param conversationUUID
     * @param messageUUID
     * @return
     */
    public PPMessage findMessage(String conversationUUID, String messageUUID) {
        if (conversationUUID == null || messageUUID == null) return null;
        if (!exist(messageUUID)) return null;

        ensureConversationUUIDKeyExist(conversationUUID);
        List<PPMessage> messageList = getMessageList(conversationUUID);

        PPMessage find = null;
        for (int i = messageList.size() - 1; i >= 0; i--) {
            PPMessage message = messageList.get(i);
            if (message.getMessageID().equals(messageUUID)) {
                find = message;
                break;
            }
        }
        return find;

    }

    /**
     * Append message to list, if message is exit, it will be ignored.
     *
     * @param messageList
     * @param message
     */
    private boolean appendMessageToList(List<PPMessage> messageList, PPMessage message, int location) {
        if (exist(message)) return false;

        messageUUIDSet.add(message.getMessageID());
        messageList.add(location, message);

        return true;
    }

    /**
     * Make sure conversation_uuid key exist in map
     * @param message
     */
    private void ensureConversationUUIDKeyExist(PPMessage message) {
        String conversationUUID = message.getConversation().getConversationUUID();
        ensureConversationUUIDKeyExist(conversationUUID);
    }

    private void ensureConversationUUIDKeyExist(String conversationUUID) {
        if (!messagesToConversationUUIDMap.containsKey(conversationUUID)) {
            messagesToConversationUUIDMap.put(conversationUUID, new ArrayList<PPMessage>());
        }
    }

    private boolean exist(String messageUUID) {
        if (messageUUIDSet.contains(messageUUID)) {
            L.w(LOG_MESSAGE_EXIT, messageUUID);
            return true;
        }
        return false;
    }

    /**
     * Is message exist
     *
     * @param message
     * @return
     */
    private boolean exist(PPMessage message) {
        return exist(message.getMessageID());
    }

}
