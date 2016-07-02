package com.ppmessage.sdk.core.query;

import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ppmessage on 6/23/16.
 */
public class MemoryQuery {

    private Map<String, Conversation> conversationMap;
    private Map<String, User> userMap;

    public MemoryQuery() {
        this.conversationMap = new LinkedHashMap<>();
        this.userMap = new LinkedHashMap<>();
    }

    public Conversation queryConversation(String conversationUUID) {
        if (conversationUUID == null) return null;
        return conversationMap.get(conversationUUID);
    }

    public User queryUser(String userUUID) {
        if (userUUID == null) return null;
        return userMap.get(userUUID);
    }

    public void cacheConversation(Conversation conversation) {
        if (conversation == null || conversation.getConversationUUID() == null) return;
        if (!conversationMap.containsKey(conversation.getConversationUUID())) {
            conversationMap.put(conversation.getConversationUUID(), conversation);
        }
    }

    public void cacheUser(User user) {
        if (user == null || user.getUuid() == null) return;
        if (!userMap.containsKey(user.getUuid())) {
            userMap.put(user.getUuid(), user);
        }
    }

}
