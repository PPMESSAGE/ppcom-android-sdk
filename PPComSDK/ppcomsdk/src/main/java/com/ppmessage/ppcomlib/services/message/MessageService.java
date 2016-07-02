package com.ppmessage.ppcomlib.services.message;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.model.ConversationMemberModel;
import com.ppmessage.ppcomlib.model.ConversationsModel;
import com.ppmessage.ppcomlib.model.PPComMessagesModel;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.message.PPMessage;

/**
 * Created by ppmessage on 5/17/16.
 */
public class MessageService implements IMessageService {

    private PPComSDK sdk;

    private ConversationsModel conversationsModel;
    private PPComMessagesModel messagesModel;
    private ConversationMemberModel conversationMemberModel;

    public MessageService(PPComSDK sdk) {
        this.sdk = sdk;

        this.conversationsModel = new ConversationsModel(sdk);
        this.messagesModel = new PPComMessagesModel(sdk);
        this.conversationMemberModel = new ConversationMemberModel(sdk);
    }

    @Override
    public ConversationsModel getConversationsModel() {
        return this.conversationsModel;
    }

    @Override
    public PPComMessagesModel getMessagesModel() {
        return this.messagesModel;
    }

    @Override
    public ConversationMemberModel getConversationMemberModel() {
        return this.conversationMemberModel;
    }

    @Override
    public void updateModels(PPMessage message) {
        messagesModel.add(message);
        updateConversationModel(message);
    }

    private void updateConversationModel(PPMessage message) {
        Conversation conversation = conversationsModel.get(message.getConversation().getConversationUUID());
        if (conversation == null) {
            conversationsModel.add(message.getConversation());
        } else {
            conversation.setUpdateTimestamp(message.getTimestamp());
            conversation.setConversationSummary(PPMessage.summary(sdk.getConfiguration().getContext(), message));
        }
    }

}
