package com.ppmessage.ppcomdemo;

import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageAudioMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageFileMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageImageMediaItem;
import com.ppmessage.sdk.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ppmessage on 5/12/16.
 */
public class TestData {

    public static final String TEST_APP_UUID = "c56adc0a-1b54-11e6-bc9f-acbc327f19e9";

    public static final String TEST_PPCOM_USER_UUID = "a30ebb35-111d-11e6-80fb-acbc327f19e9";
    public static final String TEST_PPCOM_USER_DEVICE_UUID = "a33ec145-111d-11e6-afe3-acbc327f19e9";
    public static final String TEST_PPCOM_CONVERSATION_UUID = "a5922e19-111d-11e6-a6ba-acbc327f19e9";
    public static final String TEST_PPCOM_TRACE_UUID = "ace3577f-9e0f-41a3-f9bb-1d8be0de01c9";

    public static final String TEST_PPKEFU_USER_UUID = "b5fd6b63-06b6-11e6-a042-acbc327f19e9"; // Guijin Ding

    public static List<PPMessage> getTestMessageList() {
        List<PPMessage> messageList = new ArrayList<>();

        messageList.add(makeTextMessage("ABC", PPMessage.DIRECTION_INCOMING, true));
        messageList.add(makeTextMessage("DEF", PPMessage.DIRECTION_OUTGOING));
        messageList.add(makeImageMessage("http://images.wookmark.com/111098_nature-animals-wildlife-dogs-1920x1080-wallpaper_www.wall321.com_43.jpg", 634, 356, PPMessage.DIRECTION_INCOMING));
        messageList.add(makeImageMessage("http://lemanoosh.com/wp-content/uploads/d6374625065727.5634d1ec41b4c-500x888.jpg", 500, 888, PPMessage.DIRECTION_OUTGOING));
        messageList.add(makeFileMessage("AA.txt", 123, PPMessage.DIRECTION_INCOMING));
        messageList.add(makeFileMessage("BB.zip", 456, PPMessage.DIRECTION_OUTGOING));
        messageList.add(makeTextMessage("ABC", PPMessage.DIRECTION_INCOMING));
        messageList.add(makeTextMessage("DEF", PPMessage.DIRECTION_OUTGOING, true));
        messageList.add(makeTextMessage("" +
                "THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT " +
                "THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT " +
                "THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT " +
                "THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT " +
                "THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT " +
                "THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT " +
                "THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT ",
                PPMessage.DIRECTION_INCOMING));
        messageList.add(makeAudioMessage(0, PPMessage.DIRECTION_INCOMING));
        messageList.add(makeAudioMessage(5, PPMessage.DIRECTION_INCOMING));
        messageList.add(makeAudioMessage(20, PPMessage.DIRECTION_INCOMING));
        messageList.add(makeAudioMessage(60, PPMessage.DIRECTION_INCOMING));
        messageList.add(makeAudioMessage(100, PPMessage.DIRECTION_INCOMING));
        messageList.add(makeAudioMessage(0, PPMessage.DIRECTION_OUTGOING));
        messageList.add(makeAudioMessage(5, PPMessage.DIRECTION_OUTGOING));
        messageList.add(makeAudioMessage(20, PPMessage.DIRECTION_OUTGOING));
        messageList.add(makeAudioMessage(60, PPMessage.DIRECTION_OUTGOING));
        messageList.add(makeAudioMessage(100, PPMessage.DIRECTION_OUTGOING));

        return messageList;
    }

    public static List<Conversation> getTestConversationList() {
        List<Conversation> conversationList = new ArrayList<>();

        conversationList.add(makeConversation("http://images3.wookmark.com/576902_wookmark.png", "Conversation A", "Conversation Summary A"));
        conversationList.add(makeConversation("http://images2.wookmark.com/577612_wookmark.jpg", "Conversation B", "Conversation Summary B"));
        conversationList.add(makeConversation("http://images3.wookmark.com/577613_wookmark.jpg", "Conversation C", "Conversation Summary C"));
        conversationList.add(makeConversation("http://images1.wookmark.com/577614_wookmark.jpg", "Conversation D", "Conversation Summary D"));
        conversationList.add(makeConversation("http://images3.wookmark.com/577556_image_86034.633598_.jpg", "Conversation E", "Conversation Summary E"));

        return conversationList;
    }

    private static PPMessage makeTextMessage(String text, int direction) {
        return makeTextMessage(text, direction, false);
    }

    private static PPMessage makeTextMessage(String text, int direction, boolean error) {
        PPMessage message = new PPMessage.Builder()
                .setMessageBody(text)
                .setConversation(makeConversation())
                .setFromUser(makeIncomingUser())
                .build();
        message.setDirection(direction);
        message.setError(error);
        return message;
    }

    private static PPMessage makeImageMessage(String imageUri, int width, int height, int direction) {
        PPMessageImageMediaItem imageMediaItem = new PPMessageImageMediaItem();
        imageMediaItem.setOrigUrl(imageUri);
        imageMediaItem.setThumUrl(imageUri);
        imageMediaItem.setOrigWidth(width);
        imageMediaItem.setOrigHeight(height);
        imageMediaItem.setThumWidth(width);
        imageMediaItem.setThumHeight(height);

        PPMessage message = new PPMessage.Builder()
                .setConversation(makeConversation())
                .setFromUser(makeIncomingUser())
                .setMediaItem(imageMediaItem)
                .build();
        message.setDirection(direction);
        return message;
    }

    private static PPMessage makeFileMessage(String fileName, int fileSize, int direction) {
        PPMessageFileMediaItem fileMediaItem = new PPMessageFileMediaItem();
        fileMediaItem.setName(fileName);
        fileMediaItem.setSize(fileSize);

        PPMessage message = new PPMessage.Builder()
                .setConversation(makeConversation())
                .setFromUser(makeIncomingUser())
                .setMediaItem(fileMediaItem)
                .build();
        message.setDirection(direction);
        return message;
    }

    private static PPMessage makeAudioMessage(int duration, int direction) {
        PPMessageAudioMediaItem audioMediaItem = new PPMessageAudioMediaItem();
        audioMediaItem.setDuration(duration);

        PPMessage message = new PPMessage.Builder()
                .setConversation(makeConversation())
                .setFromUser(makeIncomingUser())
                .setMediaItem(audioMediaItem)
                .build();

        message.setDirection(direction);
        return message;
    }

    public static Conversation makeConversation() {
        Conversation conversation = new Conversation();
        conversation.setConversationUUID(TEST_PPCOM_CONVERSATION_UUID);
        return conversation;
    }

    public static Conversation makeConversation(String icon, String name, String summary) {
        Conversation conversation = new Conversation();
        conversation.setConversationUUID(Utils.randomUUID());
        conversation.setConversationSummary(summary);
        conversation.setConversationIcon(icon);
        conversation.setConversationName(name);
        conversation.setUpdateTimestamp(System.currentTimeMillis());
        return conversation;
    }

    public static User makeIncomingUser() {
        User incomingUser = new User();
        incomingUser.setUuid(TEST_PPKEFU_USER_UUID);
        incomingUser.setName("Guijin Ding");
        incomingUser.setIcon("https://www.tm-town.com/assets/default_female600x600-3702af30bd630e7b0fa62af75cd2e67c.png");
        return incomingUser;
    }

    public static User makeOutgoingUser() {
        User outgoingUser = new User();
        outgoingUser.setUuid(TEST_PPCOM_USER_UUID);
        outgoingUser.setName("Tom");
        outgoingUser.setIcon("http://f9.topitme.com/9/68/6f/11875246993c56f689l.jpg");
        return outgoingUser;
    }

}
