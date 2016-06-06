package com.ppmessage.ppcomlib.model;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.model.conversations.AppOrgGroupList;
import com.ppmessage.ppcomlib.model.conversations.ConversationAgency;
import com.ppmessage.ppcomlib.model.conversations.DefaultConversation;
import com.ppmessage.ppcomlib.model.conversations.NormalConversation;
import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Example:
 *
 * <pre>
 *     PPComSDK sdk = PPComSDK.getInstance();
 *     ConversationsModel conversations = new PPComSDK(sdk);
 *
 *     1. - Async get conversations
 *     conversations.asyncGetConversations(new ConversationsModel.OnGetConversationsEvent() {
 *         @Override
 *         void onCompleted(List<Conversation> conversationList) {
 *             // consider conversationList == null was a signal to tell you should waiting now
 *         }
 *     });
 *
 *     2. - Sync get conversation
 *     String conversationUUID = "QUERY_UUID";
 *     Conversation conversation = conversations.get(conversationUUID);
 *
 *     3. - Add conversation
 *     Conversation newConversation = new Conversation();
 *     conversations.add(newConersation);
 *
 * </pre>
 *
 * Created by ppmessage on 5/16/16.
 */
public class ConversationsModel {

    private static final String LOG_ADD_CONVERSATION = "[ConversationsModel] add conversation: %s";

    public interface OnGetConversationsEvent {
        void onCompleted(List<Conversation> conversationList);
    }

    public interface OnGetConversationEvent {
        void onCompleted(Conversation conversation);
    }

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;
    private PPComStartupHelper startupHelper;
    private ConversationAgency conversationAgency;

    private List<Conversation> conversationList;

    private boolean hasGetConversations;

    public ConversationsModel(PPComSDK sdk) {
        this.sdk = sdk;
        this.messageSDK = sdk.getConfiguration().getMessageSDK();
        this.startupHelper = sdk.getStartupHelper();
        this.conversationAgency = new ConversationAgency(sdk);
        conversationList = new ArrayList<>();
    }

    public Conversation get(String conversationUUID) {
        Conversation find = null;
        for (Conversation c : conversationList) {
            if (c.getConversationUUID() != null &&
                    c.getConversationUUID().equals(conversationUUID)) {
                find = c;
            }
        }
        return find;
    }

    public void add(List<Conversation> conversations) {
        if (conversations != null) {
            for (Conversation c : conversations) {
                add(c);
            }
        }
    }

    public void add(Conversation conversation) {
        if (conversation != null) {

            if (conversation.isGroupType()) {
                addGroup(conversation);
                return;
            }

            Conversation find = get(conversation.getConversationUUID());
            if (find != null) {
                conversationList.remove(find);
            }
            L.d(LOG_ADD_CONVERSATION, conversation.getConversationUUID());
            conversationList.add(conversation);
        }
    }

    public void asyncGetConversations(OnGetConversationsEvent event) {
        if (hasGetConversations && conversationList != null && !conversationList.isEmpty()) {
            if (event != null) {
                event.onCompleted(sortedConversations());
            }
            return;
        }

        getConversationsFromServer(event);
    }

    public List<Conversation> sortedConversations() {
        Collections.sort(conversationList);
        return conversationList;
    }

    /**
     * FIND CONVERSATION
     *
     * @param groupUUID
     * @return
     */
    public Conversation findByGroupUUID(String groupUUID) {
        Conversation find = null;
        for (int i = 0; i < conversationList.size(); i++) {
            Conversation temp = conversationList.get(i);
            if (!temp.isGroupType() &&
                    temp.getGroupUUID() != null &&
                    temp.getGroupUUID().equals(groupUUID)) {
                find = temp;
                break;
            }
        }
        return find;
    }

    public void asyncGetGroupConversation(String groupUUID, final OnGetConversationEvent event) {
        // Check local cache
        Conversation find = findByGroupUUID(groupUUID);
        if (find != null) {
            if (event != null) {
                event.onCompleted(find);
            }
            return;
        }

        conversationAgency.createGroupConversation(groupUUID, new ConversationAgency.OnCreateConversationEvent() {
            @Override
            public void onCompleted(Conversation conversation) {
                if (conversation != null) {
                    add(conversation);
                }
                if (event != null) {
                    event.onCompleted(conversation);
                }
            }
        });

    }

    public void asyncGetUserConversation(String userUUID, final OnGetConversationEvent event) {
        Conversation find = findByAssignedUUID(userUUID);
        if (find != null) {
            if (event != null) {
                event.onCompleted(find);
            }
            return;
        }

        conversationAgency.createUserConversation(userUUID, new ConversationAgency.OnCreateConversationEvent() {
            @Override
            public void onCompleted(Conversation conversation) {
                if (conversation != null) {
                    add(conversation);
                }
                if (event != null) {
                    event.onCompleted(conversation);
                }
            }
        });
    }

    // ===============================================
    //
    // PRIVATE API
    // ==========================================
    // |    1. get ppcom default conversation   |
    // |    2. get app org group list           |
    // |    3. get normal conversation list     |
    // ==========================================
    //

    /**
     * Get conversations from server
     *
     * @param event
     */
    private void getConversationsFromServer(OnGetConversationsEvent event) {
        getPPComDefaultConversation(event);
    }

    /**
     * Get ppcom default conversation from server
     *
     * @param event
     */
    private void getPPComDefaultConversation(final OnGetConversationsEvent event) {
        DefaultConversation defaultConversation = new DefaultConversation(sdk);
        defaultConversation.get(new DefaultConversation.OnGetDefaultConversationEvent() {
            @Override
            public void onCompleted(Conversation conversation) {
                if (event != null) {
                    if (conversation == null) {
                        event.onCompleted(null);
                    } else {
                        onGetDefaultConversation(conversation, event);
                    }
                }
            }
        });
    }

    private void onGetDefaultConversation(Conversation defaultConversation, OnGetConversationsEvent event) {
        add(defaultConversation);
        asyncGetAppOrgGroupList(event);
    }

    private void asyncGetAppOrgGroupList(final OnGetConversationsEvent event) {
        AppOrgGroupList appOrgGroupList = new AppOrgGroupList(sdk);
        appOrgGroupList.get(new AppOrgGroupList.OnGetAppOrgGroupListEvent() {
            @Override
            public void onCompleted(List<Conversation> groupConversations) {
                add(groupConversations);
                onGetAppOrgGroupList(event);
            }
        });
    }

    private void onGetAppOrgGroupList(final OnGetConversationsEvent event) {
        NormalConversation normalConversation = new NormalConversation(sdk);
        normalConversation.get(new NormalConversation.OnGetNormalConversationEvent() {
            @Override
            public void onCompleted(List<Conversation> conversationList) {
                add(conversationList);
                hasGetConversations = true;

                if (event != null) event.onCompleted(sortedConversations());
            }
        });
    }

    /**
     * FIND GROUP
     *
     * @param groupUUID
     * @return
     */
    private Conversation findGroup(final String groupUUID) {
        List<Conversation> conversationList = this.conversationList;
        Conversation group = null;
        for (int i = 0; i < conversationList.size(); i++) {
            Conversation conversation = conversationList.get(i);
            if (conversation.isGroupType() &&
                    conversation.getGroupUUID() != null &&
                    conversation.getGroupUUID().equals(groupUUID)) {
                group = conversation;
                break;
            }
        }
        return group;
    }

    private void addGroup(final Conversation conversation) {
        Conversation find = findGroup(conversation.getGroupUUID());
        if (find != null) {
            this.conversationList.remove(find);
        }
        this.conversationList.add(conversation);
    }

    private Conversation findByAssignedUUID(String userUUID) {
        List<Conversation> conversationList = this.conversationList;
        Conversation find = null;
        for (int i = 0; i < conversationList.size(); i++) {
            Conversation conversation = conversationList.get(i);
            if (!conversation.isGroupType() &&
                    conversation.getAssignedUUID() != null &&
                    conversation.getAssignedUUID().equals(userUUID)) {
                find = conversation;
                break;
            }
        }
        return find;
    }

}
