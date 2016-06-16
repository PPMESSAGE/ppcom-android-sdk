package com.ppmessage.ppcomlib.services.message;

import com.ppmessage.ppcomlib.model.ConversationMemberModel;
import com.ppmessage.ppcomlib.model.ConversationsModel;
import com.ppmessage.ppcomlib.model.PPComMessagesModel;
import com.ppmessage.sdk.core.bean.message.PPMessage;

/**
 * Created by ppmessage on 5/17/16.
 */
public interface IMessageService {

    /**
     * Get conversations model
     *
     * @return
     */
    ConversationsModel getConversationsModel();

    /**
     * Get messages model
     *
     * @return
     */
    PPComMessagesModel getMessagesModel();

    /**
     * Get conversation members model
     *
     * @return
     */
    ConversationMemberModel getConversationMemberModel();

    /**
     *
     * Update model layers by message, message may be new generated, or have something just has updated.
     *
     * You should call this method to notify models to update the content.
     *
     * @param message
     */
    void updateModels(PPMessage message);

}
