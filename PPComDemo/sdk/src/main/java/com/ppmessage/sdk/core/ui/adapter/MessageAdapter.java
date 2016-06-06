package com.ppmessage.sdk.core.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageFileMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageImageMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageTxtMediaItem;
import com.ppmessage.sdk.core.utils.IImageLoader;
import com.ppmessage.sdk.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ppmessage on 5/12/16.
 */
public class MessageAdapter extends BaseAdapter {

    private static final double MAX_FILE_WIDTH_RATIO = 0.7;
    private static final double MAX_TEXT_BUBBLE_RATIO = 0.7;
    private static final double MAX_IMAGE_WIDTH_RATIO = 0.9;
    private static final double MAX_IMAGE_HEIGHT_RATIO = 0.3;

    private static final int DEFAULT_AVATAR_WIDTH = 48;
    private static final int DEFAULT_AVATAR_HEIGHT = 48;

    public enum ViewType {
        TEXT_LEFT, TEXT_RIGHT, IMAGE_LEFT, IMAGE_RIGHT, FILE_LEFT, FILE_RIGHT,
    }

    private PPMessageSDK sdk;
    private Context context;
    private Activity activity;
    private IImageLoader imageLoader;

    private List<PPMessage> mChatMessages;
    private LayoutInflater mInflater;
    private boolean showOutgoingUserAvatar;

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    public MessageAdapter(PPMessageSDK sdk, Activity activity, List<PPMessage> messages) {
        this(sdk, activity, messages, true);
    }

    public MessageAdapter(PPMessageSDK sdk, Activity activity, List<PPMessage> messages, boolean showOutgoingUserAvatar) {
        this.sdk = sdk;
        this.activity = activity;
        this.context = sdk.getContext();
        this.imageLoader = sdk.getImageLoader();
        this.mChatMessages = messages;
        this.showOutgoingUserAvatar = showOutgoingUserAvatar;

        SCREEN_WIDTH = Utils.getDisplayPoint(this.context).x;
        SCREEN_HEIGHT = Utils.getDisplayPoint(this.context).y;
    }

    @Override
    public int getCount() {
        return mChatMessages != null ? mChatMessages.size() : 0;
    }

    @Override
    public PPMessage getItem(int position) {
        return mChatMessages != null ? mChatMessages.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return getMessageViewType(getItem(position));
    }

    @Override
    public int getViewTypeCount() {
        return ViewType.values().length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        PPMessage message = getItem(position);

        if (mInflater == null) {
            mInflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        View v = null;
        if (type == ViewType.TEXT_LEFT.ordinal()) {
            v = getLeftTextMessageView(convertView, parent, message);
        } else if (type == ViewType.TEXT_RIGHT.ordinal()) {
            v = getRightTextMessageView(convertView, parent, message);
        } else if (type == ViewType.IMAGE_LEFT.ordinal()) {
            v = getLeftImageMessageView(convertView, parent, message);
        } else if (type == ViewType.IMAGE_RIGHT.ordinal()) {
            v = getRightImageMessageView(convertView, parent, message);
        } else if (type == ViewType.FILE_LEFT.ordinal()) {
            v = getLeftFileMessageView(convertView, parent, message);
        } else if (type == ViewType.FILE_RIGHT.ordinal()) {
            v = getRightFileMessageView(convertView, parent, message);
        }

        if (v != null) {
            v.setClickable(false);
            v.setOnClickListener(null);
        }

        return v;
    }

    /**
     * Get messages
     *
     * @return
     */
    public List<PPMessage> getMessages() {
        return mChatMessages;
    }

    public void setMessages(List<PPMessage> mChatMessages) {
        this.mChatMessages = mChatMessages;
        this.notifyDataSetChanged();
    }

    /**
     * Add message to tail
     *
     */
    public boolean addMessage(PPMessage message) {
        if (mChatMessages == null) {
            mChatMessages = new ArrayList<>();
        }
        if (mChatMessages != null) {
            boolean append = mChatMessages.add(message);
            notifyDataSetChanged();
            return append;
        }
        return false;
    }

    private View getRightFileMessageView(View convertView, ViewGroup parent,
                                         PPMessage message) {
        ViewHolderRightFileMessage holder = null;
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.pp_chat_item_file_by_user,
                    parent, false);
            holder = new ViewHolderRightFileMessage();
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_user_avatar);
            holder.fileNameTv = (TextView) v.findViewById(R.id.pp_chat_item_file_by_user_file_name);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_file_by_user_message_extra);
            holder.container = (ViewGroup) v.findViewById(R.id.pp_chat_item_file_by_user_container);
            v.setTag(holder);
        } else {
            holder = (ViewHolderRightFileMessage) v.getTag();
        }

        PPMessageFileMediaItem fileMediaItem = (PPMessageFileMediaItem) message.getMediaItem();
        showOutgoingUserAvatar(holder.avatar, message.getFromUser());
        holder.fileNameTv.setText(fileMediaItem.getName());
        setMessageItemExtraInfo(holder.timestampTv, message);
        holder.fileNameTv.setMaxWidth(getMaxFileBubbleWidth());

        return v;
    }

    private View getLeftFileMessageView(View convertView, ViewGroup parent,
                                        PPMessage message) {
        ViewHolderLeftFileMessage holder = null;
        View v = convertView;
        if (v == null) {
            holder = new ViewHolderLeftFileMessage();

            v = mInflater.inflate(
                    R.layout.pp_chat_item_file_by_admin, parent, false);
            holder.fileNameTv = (TextView) v.findViewById(R.id.pp_chat_item_file_by_admin_file_name);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_file_by_admin_message_extra);
            holder.container = (ViewGroup) v.findViewById(R.id.pp_chat_item_file_by_admin_container);
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_file_by_admin_user_avatar);

            v.setTag(holder);
        } else {
            holder = (ViewHolderLeftFileMessage) v.getTag();
        }

        PPMessageFileMediaItem fileMediaItem = (PPMessageFileMediaItem) message.getMediaItem();

        holder.fileNameTv.setText(fileMediaItem.getName());
        setMessageItemExtraInfo(holder.timestampTv, message);
        loadAvatar(v, message, holder.avatar);
        holder.fileNameTv.setMaxWidth(getMaxFileBubbleWidth());
        v.setClickable(false);

        return v;
    }

    private View getRightImageMessageView(View convertView, ViewGroup parent,
                                          PPMessage message) {
        ViewHolderRightImageMessage holder = null;
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(
                    R.layout.pp_chat_item_image_by_user, parent, false);
            holder = new ViewHolderRightImageMessage();
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_user_avatar);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_image_by_user_message_extra);
            holder.imgBody = (ImageView) v.findViewById(R.id.pp_chat_item_image_by_user_message_body);
            v.setTag(holder);
        } else {
            holder = (ViewHolderRightImageMessage) v.getTag();
        }

        setMessageItemExtraInfo(holder.timestampTv, message);

        PPMessageImageMediaItem imageMediaItem = (PPMessageImageMediaItem) message.getMediaItem();
        calcAndSetImageViewFinalTargetSize(holder.imgBody, imageMediaItem.getOrigWidth(), imageMediaItem.getOrigHeight());
        showOutgoingUserAvatar(holder.avatar, message.getFromUser());

        sdk.getImageLoader().loadImage(imageMediaItem.getOrigUrl(),
                imageMediaItem.getOrigWidth(),
                imageMediaItem.getOrigHeight(),
                new ColorDrawable(Color.GRAY),
                holder.imgBody);

        return v;
    }

    private View getLeftImageMessageView(View convertView, ViewGroup parent,
                                         PPMessage message) {
        ViewHolderLeftImageMessage holder = null;
        View v = convertView;
        if (v == null) {
            holder = new ViewHolderLeftImageMessage();
            v = mInflater.inflate(
                    R.layout.pp_chat_item_image_by_admin, parent, false);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_image_by_admin_message_extra);
            holder.imgBody = (ImageView) v.findViewById(R.id.pp_chat_item_image_by_admin_message_body);
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_image_by_admin_user_avatar);
            v.setTag(holder);
        } else {
            holder = (ViewHolderLeftImageMessage) v.getTag();
        }

        PPMessageImageMediaItem imageMediaItem = (PPMessageImageMediaItem) message.getMediaItem();
        setMessageItemExtraInfo(holder.timestampTv, message);
        loadAvatar(v, message, holder.avatar);
        calcAndSetImageViewFinalTargetSize(holder.imgBody, imageMediaItem.getOrigWidth(), imageMediaItem.getOrigHeight());

        sdk.getImageLoader().loadImage(
                imageMediaItem.getOrigUrl(),
                imageMediaItem.getOrigWidth(),
                imageMediaItem.getOrigHeight(),
                new ColorDrawable(Color.GRAY),
                holder.imgBody);

        return v;
    }

    private View getRightTextMessageView(View convertView, ViewGroup parent,
                                         PPMessage message) {
        ViewHolderRightTextMessage holder = null;
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.pp_chat_item_text_by_user,
                    parent, false);
            holder = new ViewHolderRightTextMessage();
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_user_avatar);
            holder.bodyTv = (TextView) v.findViewById(R.id.pp_chat_item_text_by_user_message_body);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_text_by_user_message_extra);
            v.setTag(holder);
        } else {
            holder = (ViewHolderRightTextMessage) v.getTag();
        }

        setMessageItemExtraInfo(holder.timestampTv, message);
        showOutgoingUserAvatar(holder.avatar, message.getFromUser());
        bindTextViewLongClickListener(holder.bodyTv);
        holder.bodyTv.setMaxWidth(getMaxTextBubbleWidth());
        loadText(message, holder.bodyTv);

        return v;
    }

    private View getLeftTextMessageView(View convertView, ViewGroup parent,
                                        PPMessage message) {
        ViewHolderLeftTextMessage holder = null;
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.pp_chat_item_text_by_admin,
                    parent, false);
            holder = new ViewHolderLeftTextMessage();
            holder.bodyTv = (TextView) v.findViewById(R.id.pp_chat_item_text_by_admin_message_body);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_text_by_admin_message_extra);
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_text_by_admin_user_avatar);

            v.setTag(holder);
        } else {
            holder = (ViewHolderLeftTextMessage) v.getTag();
        }

        setMessageItemExtraInfo(holder.timestampTv, message);
        bindTextViewLongClickListener(holder.bodyTv);
        loadAvatar(v, message, holder.avatar);
        holder.bodyTv.setMaxWidth(getMaxTextBubbleWidth());
        loadText(message, holder.bodyTv);

        return v;
    }

    private void loadAvatar(View convertView, PPMessage message, ImageView avatar) {
        User fromUser = message.getFromUser();
        if (fromUser != null && fromUser.getIcon() != null) {
            imageLoader.loadImage(fromUser.getIcon(),
                    DEFAULT_AVATAR_WIDTH,
                    DEFAULT_AVATAR_HEIGHT,
                    R.drawable.pp_icon_avatar,
                    avatar);
        }
    }

    private void loadText(PPMessage message, TextView bodyTv) {
        String messageText = null;
        if (message.getMessageSubType().equals(PPMessage.TYPE_TEXT)) {
            messageText = message.getMessageBody();
        } else {
            PPMessageTxtMediaItem txtMediaItem = (PPMessageTxtMediaItem) message.getMediaItem();
            if (txtMediaItem != null) {
                if (txtMediaItem.getTextContent() != null) {
                    messageText = txtMediaItem.getTextContent();
                } else {
                    messageText = message.getMessageBody();
                }
            }
        }

        bodyTv.setText(messageText);
    }

    private void setMessageItemExtraInfo(TextView extraTv, PPMessage message) {
        if (message.isError()) {
            extraTv.setText(context.getText(R.string.pp_message_error));
            extraTv.setTextColor(context.getResources().getColor(R.color.pp_chat_item_extra_error));
        } else {
            extraTv.setText(Utils.formatTimestamp(message.getTimestamp()));
            extraTv.setTextColor(context.getResources().getColor(R.color.pp_chat_item_extra));
        }
    }

    private void showOutgoingUserAvatar(ImageView userAvatar, User fromUser) {
        userAvatar.setVisibility(showOutgoingUserAvatar ? View.VISIBLE : View.GONE);
        if (showOutgoingUserAvatar) {
            if (fromUser != null && fromUser.getIcon() != null) {
                imageLoader.loadImage(fromUser.getIcon(),
                        DEFAULT_AVATAR_WIDTH,
                        DEFAULT_AVATAR_HEIGHT,
                        R.drawable.pp_icon_avatar,
                        userAvatar);
            }
        }
    }

    // === Long Click Listener ===

    private void bindTextViewLongClickListener(final TextView textView) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.copyToClipboard(activity, textView.getText().toString());
                Utils.makeToast(activity, R.string.pp_copy_message_success_hint);
                return true;
            }
        });
    }

    // === Provide width info about message bubbles ===

    protected int getMaxFileBubbleWidth() {
        return (int) (SCREEN_WIDTH * MAX_FILE_WIDTH_RATIO);
    }

    protected int getMaxTextBubbleWidth() {
        return (int) (SCREEN_WIDTH * MAX_TEXT_BUBBLE_RATIO);
    }

    protected int getMaxImageBubbleWidth() {
        return (int) (SCREEN_WIDTH * MAX_IMAGE_WIDTH_RATIO);
    }

    protected int getMaxImageBubbleHeight() {
        return (int) (SCREEN_HEIGHT * MAX_IMAGE_HEIGHT_RATIO);
    }

    private void calcAndSetImageViewFinalTargetSize(ImageView targetImageView, int originImageWidth, int originImageHeight) {
        double maxBubbleWidth = getMaxImageBubbleWidth();
        double maxBubbleHeight = getMaxImageBubbleHeight();

        int finalWidth = 0;
        int finalHeight = 0;

        if (originImageWidth <= maxBubbleWidth && originImageHeight <= maxBubbleHeight) {
            finalWidth = originImageWidth;
            finalHeight = originImageHeight;
        } else {
            double ratio = Math.min(maxBubbleWidth / originImageWidth, maxBubbleHeight / originImageHeight);
            finalWidth = (int) (originImageWidth * ratio);
            finalHeight = (int) (originImageHeight * ratio);
        }

        setImageSize(targetImageView, finalWidth, finalHeight);
    }

    private void setImageSize(ImageView imageView, int width, int height) {
        imageView.getLayoutParams().width = width;
        imageView.getLayoutParams().height = height;
    }

    /**
     * Get Message view Type by message
     *
     * @param message
     * @return
     */
    public static int getMessageViewType(PPMessage message) {
        int direction = message.getDirection();
        String messageSubType = message.getMessageSubType();
        ViewType viewType = ViewType.TEXT_LEFT;

        if (direction == PPMessage.DIRECTION_INCOMING) {

            if (messageSubType.equals(PPMessage.TYPE_TEXT) || messageSubType.equals(PPMessage.TYPE_TXT)) {
                viewType = ViewType.TEXT_LEFT;
            } else if (messageSubType.equals(PPMessage.TYPE_IMAGE)) {
                viewType = ViewType.IMAGE_LEFT;
            } else if (messageSubType.equals(PPMessage.TYPE_FILE)) {
                viewType = ViewType.FILE_LEFT;
            }

        } else {

            if (messageSubType.equals(PPMessage.TYPE_TEXT) || messageSubType.equals(PPMessage.TYPE_TXT)) {
                viewType = ViewType.TEXT_RIGHT;
            } else if (messageSubType.equals(PPMessage.TYPE_IMAGE)) {
                viewType = ViewType.IMAGE_RIGHT;
            } else if (messageSubType.equals(PPMessage.TYPE_FILE)) {
                viewType = ViewType.FILE_RIGHT;
            }

        }

        return viewType.ordinal();
    }

    // === ViewHolder Class ===

    /**
     * Left Text Message ViewHolder
     */
    class ViewHolderLeftTextMessage {
        ImageView avatar;
        TextView bodyTv;
        TextView timestampTv;
    }

    /**
     * Right Text Message ViewHolder
     */
    class ViewHolderRightTextMessage {
        ImageView avatar;
        TextView bodyTv;
        TextView timestampTv;
    }

    /**
     * Left Image Message ViewHolder
     */
    class ViewHolderLeftImageMessage {
        ImageView imgBody;
        TextView timestampTv;
        ImageView avatar;
    }

    /**
     * Right Image Message ViewHolder
     */
    class ViewHolderRightImageMessage {
        ImageView avatar;
        ImageView imgBody;
        TextView timestampTv;
    }

    /**
     * Left File Message ViewHolder
     */
    class ViewHolderLeftFileMessage {
        ImageView avatar;
        TextView fileNameTv;
        TextView timestampTv;
        ViewGroup container;
    }

    /**
     * Right File Message ViewHolder
     */
    class ViewHolderRightFileMessage {
        ImageView avatar;
        TextView fileNameTv;
        TextView timestampTv;
        ViewGroup container;
    }

}
