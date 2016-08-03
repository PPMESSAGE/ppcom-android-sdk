package com.ppmessage.sdk.core.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.ppmessage.sdk.core.bean.message.PPMessageAudioMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageFileMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageImageMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageTxtMediaItem;
import com.ppmessage.sdk.core.ui.EaseShowBigImageActivity;
import com.ppmessage.sdk.core.ui.MessageActivity;
import com.ppmessage.sdk.core.utils.IImageLoader;
import com.ppmessage.sdk.core.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ppmessage on 5/12/16.
 */
public class MessageAdapter extends BaseAdapter {

    public interface OnConvertViewClickEvent {
        void onClick(View convertView, int position);
    }

    private static final String LOG_MEDIAPLAYER_PREPARE_ERROR = "[MessageAdapter] mediaplayer try to play: %s, meet error: %s";
    private static final String LOG_MEDIAPLAYER_MEET_ERROR = "[MessageAdapter] mediaplayer meet error: [%d: %d]";
    private static final String LOG_MEDIAPLAYER_COMPLETED = "[MessageAdapter] mediaplayer play completed";
    private static final String LOG_MEDIAPLAYER_BUFFER_UPDATED = "[MessageAdapter] mediaplayer buffer updated:%d";
    private static final String LOG_MEDIAPLAYER_START_PLAY_URI = "[MessageAdapter] mediaplayer start play:%s";
    private static final String LOG_SHOW_USERAVATAR_ERROR = "[MessageAdapter] show user avatar error, fromuser:%s, fromuser_icon:%s";

    private static final double MAX_FILE_WIDTH_RATIO = 0.6;
    private static final double MAX_TEXT_BUBBLE_RATIO = 0.6;
    private static final double MAX_IMAGE_WIDTH_RATIO = 0.8;
    private static final double MAX_IMAGE_HEIGHT_RATIO = 0.3;
    private static final double MAX_AUDIO_WIDTH_RATIO = 0.5;
    private static final double MIN_AUDIO_WIDTH = 96;

    private static final int DEFAULT_AVATAR_WIDTH = 72;
    private static final int DEFAULT_AVATAR_HEIGHT = 72;

    private static final int DEFAULT_THUMB_WIDTH_IN_SERVER = 120;
    private static final int DEFAULT_THUMB_HEIGHT_IN_SERVER = 160;
    private static final int DEFAULT_THUMB_TO_DISPLAY_ZOOMIN_SAMPLE_SIZE = 3;
    private static final int DEFAULT_DISPLAY_WIDTH = DEFAULT_THUMB_WIDTH_IN_SERVER * DEFAULT_THUMB_TO_DISPLAY_ZOOMIN_SAMPLE_SIZE;
    private static final int DEFAULT_DISPLAY_HEIGHT = DEFAULT_THUMB_HEIGHT_IN_SERVER * DEFAULT_THUMB_TO_DISPLAY_ZOOMIN_SAMPLE_SIZE;

    // 60.0 seconds. we consider 60.0 seconds voice has the same width with the 100.0 seconds voice
    private static final int MAX_AUDIO_VIEW_TIME = 60;

    public enum ViewType {
        TEXT_LEFT, TEXT_RIGHT, IMAGE_LEFT, IMAGE_RIGHT,
        FILE_LEFT, FILE_RIGHT, AUDIO_LEFT, AUDIO_RIGHT
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

    private WeakReference<ImageView> lastClickedAudioImageViewRef;
    private int lastClickedAudioMessageDirection;

    private MediaPlayer mediaPlayer;

    private OnConvertViewClickEvent convertViewClickEvent;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        } else if (type == ViewType.AUDIO_LEFT.ordinal()) {
            v = getLeftAudioMessageView(convertView, parent, message);
        } else if (type == ViewType.AUDIO_RIGHT.ordinal()) {
            v = getRightAudioMessageView(convertView, parent, message);
        }

        if (v != null) {
            final View messageView = v;
            messageView.setClickable(true);
            messageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (convertViewClickEvent != null) {
                        convertViewClickEvent.onClick(messageView, position);
                    }
                }
            });
        }

        return v;
    }

    public void setConvertViewClickEvent(OnConvertViewClickEvent convertViewClickEvent) {
        this.convertViewClickEvent = convertViewClickEvent;
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
        setMessages(mChatMessages, true);
    }

    public void setMessages(List<PPMessage> mChatMessages, boolean autoNotify) {
        this.mChatMessages = mChatMessages;
        if (autoNotify) {
            this.notifyDataSetChanged();
        }
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

    // ======================
    // File Message
    // ======================

    private View getRightFileMessageView(View convertView, ViewGroup parent,
                                         PPMessage message) {
        ViewHolderRightFileMessage holder = null;
        View v = convertView;
        if (v == null || v.getTag() == null || v.getTag().getClass() != ViewHolderRightFileMessage.class) {
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
        bindFileViewClickListener(holder.container);

        return v;
    }

    private View getLeftFileMessageView(View convertView, ViewGroup parent,
                                        PPMessage message) {
        ViewHolderLeftFileMessage holder = null;
        View v = convertView;
        if (v == null || v.getTag() == null || v.getTag().getClass() != ViewHolderLeftFileMessage.class) {
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
        if (fileMediaItem != null) {
            holder.fileNameTv.setText(fileMediaItem.getName());
        }

        setMessageItemExtraInfo(holder.timestampTv, message);
        loadAvatar(v, message, holder.avatar);
        holder.fileNameTv.setMaxWidth(getMaxFileBubbleWidth());
        v.setClickable(false);
        bindFileViewClickListener(holder.container);

        return v;
    }

    // ======================
    // Image Message
    // ======================

    private View getRightImageMessageView(View convertView, ViewGroup parent,
                                          PPMessage message) {
        ViewHolderRightImageMessage holder = null;
        View v = convertView;
        if (v == null || v.getTag() == null || v.getTag().getClass() != ViewHolderRightImageMessage.class) {
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
        showOutgoingUserAvatar(holder.avatar, message.getFromUser());
        loadImage(imageMediaItem, holder.imgBody);
        bindImageViewClickListener(message, holder.imgBody);

        return v;
    }

    private View getLeftImageMessageView(View convertView, ViewGroup parent,
                                         PPMessage message) {
        ViewHolderLeftImageMessage holder = null;
        View v = convertView;
        if (v == null || v.getTag() == null || v.getTag().getClass() != ViewHolderLeftImageMessage.class) {
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
        loadImage(imageMediaItem, holder.imgBody);
        bindImageViewClickListener(message, holder.imgBody);

        return v;
    }

    // ======================
    // Text Message
    // ======================

    private View getRightTextMessageView(View convertView, ViewGroup parent,
                                         PPMessage message) {
        ViewHolderRightTextMessage holder = null;
        View v = convertView;
        if (v == null || v.getTag() == null || v.getTag().getClass() != ViewHolderRightTextMessage.class) {
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
        if (v == null || v.getTag() == null || convertView.getTag().getClass() != ViewHolderLeftTextMessage.class) {
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

    // ======================
    // Audio Message
    // ======================

    private View getLeftAudioMessageView(View convertView, ViewGroup parent,
                                         final PPMessage message) {
        ViewHolderLeftAudioMesasge holder = null;
        View v = convertView;
        if (v == null || v.getTag() == null || convertView.getTag().getClass() != ViewHolderLeftAudioMesasge.class) {
            v = mInflater.inflate(R.layout.pp_chat_item_audio_by_admin, parent, false);
            holder = new ViewHolderLeftAudioMesasge();
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_audio_by_admin_user_avatar);
            holder.durationTv = (TextView) v.findViewById(R.id.pp_chat_item_audio_by_admin_duration);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_audio_by_admin_message_extra);
            holder.audioImage = (ImageView) v.findViewById(R.id.pp_chat_item_audio_by_admin_audio_image);
            holder.audioImageContainer = (ViewGroup) v.findViewById(R.id.pp_chat_item_audio_by_admin_container);
        } else {
            holder = (ViewHolderLeftAudioMesasge) v.getTag();
        }

        PPMessageAudioMediaItem audioMediaItem = (PPMessageAudioMediaItem) message.getMediaItem();

        setMessageItemExtraInfo(holder.timestampTv, message);
        loadAvatar(v, message, holder.avatar);
        calcAndSetAudioViewFinalTargetWidth(holder.audioImageContainer, audioMediaItem.getDuration());

        if (audioMediaItem != null) {
            holder.durationTv.setText(String.format(Locale.getDefault(), "%.1f\"", audioMediaItem.getDuration()));
        }

        final ImageView audioImage = holder.audioImage;
        holder.audioImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAudioMessageClicked(message, audioImage);
            }
        });

        return v;
    }

    private View getRightAudioMessageView(View convertView, ViewGroup parent,
                                          final PPMessage message) {
        ViewHolderRightAudioMessage holder = null;
        View v = convertView;
        if (v == null || v.getTag() == null || convertView.getTag().getClass() != ViewHolderRightAudioMessage.class) {
            v = mInflater.inflate(R.layout.pp_chat_item_audio_by_user, parent, false);
            holder = new ViewHolderRightAudioMessage();
            holder.avatar = (ImageView) v.findViewById(R.id.pp_chat_item_user_avatar);
            holder.durationTv = (TextView) v.findViewById(R.id.pp_chat_item_audio_by_user_duration);
            holder.timestampTv = (TextView) v.findViewById(R.id.pp_chat_item_audio_by_user_message_extra);
            holder.audioImage = (ImageView) v.findViewById(R.id.pp_chat_item_audio_by_user_audio_image);
            holder.audioImageContainer = (ViewGroup) v.findViewById(R.id.pp_chat_item_audio_by_user_container);
        } else {
            holder = (ViewHolderRightAudioMessage) v.getTag();
        }

        PPMessageAudioMediaItem audioMediaItem = (PPMessageAudioMediaItem) message.getMediaItem();

        setMessageItemExtraInfo(holder.timestampTv, message);
        showOutgoingUserAvatar(holder.avatar, message.getFromUser());
        calcAndSetAudioViewFinalTargetWidth(holder.audioImageContainer, audioMediaItem.getDuration());

        if (audioMediaItem != null) {
            holder.durationTv.setText(String.format(Locale.getDefault(), "%.1f\"", audioMediaItem.getDuration()));
        }

        final ImageView audioImage = holder.audioImage;
        holder.audioImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAudioMessageClicked(message, audioImage);
            }
        });

        return v;
    }

    // =====================
    // Helper
    // =====================

    /**
     * Used for left message avatar view
     *
     * @param convertView
     * @param message
     * @param avatar
     */
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
            } else {
                L.w(LOG_SHOW_USERAVATAR_ERROR, fromUser, fromUser != null ? fromUser.getIcon() : "null");
            }
        }
    }

    private void loadImage(PPMessageImageMediaItem imageMediaItem, ImageView imageView) {
        if (imageMediaItem != null) {

            int reqWidth = 0;
            int reqHeight = 0;
            String imageUri = null;
            boolean useThumb = false;

            // Local path exist
            if (imageMediaItem.getLocalPathUrl() != null && new File(Uri.parse(imageMediaItem.getLocalPathUrl()).getPath()).exists()) {
                imageUri = imageMediaItem.getLocalPathUrl();
            }

            // Orig Bitmap exist in disk
            if (imageUri == null) {
                File imageFile = sdk.getImageLoader().imageFile(imageMediaItem.getOrigUrl());
                if (imageMediaItem.getOrigUrl() != null &&
                        imageFile != null &&
                        imageFile.exists()) {
                    imageUri = imageMediaItem.getOrigUrl();
                }
            }

            // We prefer thumb image first
            if (imageUri == null) {

                // Thumb image
                if (imageMediaItem.getThumUrl() != null) {
                    useThumb = true;
                    imageUri = imageMediaItem.getThumUrl();
                } else if (imageMediaItem.getOrigUrl() != null) {
                    imageUri = imageMediaItem.getOrigUrl();
                }

            }

            if (useThumb) {
                reqWidth = imageMediaItem.getThumWidth();
                reqHeight = imageMediaItem.getThumHeight();

                // thumb image is too small
                if (imageMediaItem.getOrigUrl() != null) {
                    if (imageMediaItem.getOrigWidth() > DEFAULT_THUMB_WIDTH_IN_SERVER &&
                            imageMediaItem.getOrigHeight() > DEFAULT_THUMB_HEIGHT_IN_SERVER) {
                        reqWidth *= DEFAULT_THUMB_TO_DISPLAY_ZOOMIN_SAMPLE_SIZE;
                        reqHeight *= DEFAULT_THUMB_TO_DISPLAY_ZOOMIN_SAMPLE_SIZE;
                    }
                }
            } else {
                // Avoid poentially & frequently happend OOM problem
                // Avoid poentially & frequently happend OOM problem
                int inSampleSize = Utils.calculateInSampleSize(
                        DEFAULT_DISPLAY_WIDTH,
                        DEFAULT_DISPLAY_HEIGHT,
                        imageMediaItem.getOrigWidth(),
                        imageMediaItem.getOrigWidth());

                reqWidth = (int) ((float) imageMediaItem.getOrigWidth() / inSampleSize);
                reqHeight = (int) ((float) imageMediaItem.getOrigHeight() / inSampleSize);
            }

            // Make sure reqWidth and reqHeight > 0
            if (reqWidth <= 0 || reqHeight <= 0) {
                reqWidth = DEFAULT_DISPLAY_WIDTH;
                reqHeight = DEFAULT_DISPLAY_HEIGHT;
            }

            if (imageUri != null) {
                calcAndSetImageViewFinalTargetSize(imageView, reqWidth, reqHeight);
                sdk.getImageLoader().loadImage(
                        imageUri,
                        reqWidth,
                        reqHeight,
                        new ColorDrawable(Color.GRAY),
                        imageView);
            }

        }
    }

    // === Long Click, Click Listener ===

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

    private void bindImageViewClickListener(final PPMessage message, final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message != null) {
                    final PPMessageImageMediaItem imageMediaItem = (PPMessageImageMediaItem) message.getMediaItem();
                    if (imageMediaItem != null) {
                        String imageUrl = imageMediaItem.getLocalPathUrl();
                        if (imageUrl == null || !new File(Uri.parse(imageUrl).getPath()).exists() ) {
                            imageUrl = imageMediaItem.getOrigUrl();
                        }

                        Intent intent = new Intent(activity, EaseShowBigImageActivity.class);
                        intent.putExtra(EaseShowBigImageActivity.EXTRA_IMAGE_URI_KEY, imageUrl);
                        intent.putExtra(EaseShowBigImageActivity.EXTRA_IMAGE_WIDTH_KEY, imageMediaItem.getOrigWidth());
                        intent.putExtra(EaseShowBigImageActivity.EXTRA_IMAGE_HEIGHT_KEY, imageMediaItem.getOrigHeight());
                        activity.startActivityForResult(intent, MessageActivity.REQUEST_SHOW_BIG_IMAGE_RESULT);
                    }

                }
            }
        });
    }

    private void bindFileViewClickListener(final ViewGroup fileContainer) {
        fileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void onAudioMessageClicked(final PPMessage message, final ImageView audioImageView) {
        ImageView lastClickedAudioImageView = null;
        if (lastClickedAudioImageViewRef != null && lastClickedAudioImageViewRef.get() != null) {
            lastClickedAudioImageView = lastClickedAudioImageViewRef.get();
        }

        if (lastClickedAudioImageView != null) {

            Drawable audioBackground = lastClickedAudioImageView.getBackground();
            AnimationDrawable currenPlayingAnimationDrawable = null;
            if (audioBackground != null && audioBackground instanceof AnimationDrawable) {
                currenPlayingAnimationDrawable = (AnimationDrawable) audioBackground;
            }

            if (lastClickedAudioImageView == audioImageView &&
                    isPlaying()) {
                if (currenPlayingAnimationDrawable != null) {
                    stopPlay();
                    stopAudioAnimationDrawableAndSetStaticAudioImage(currenPlayingAnimationDrawable, lastClickedAudioImageView, message);
                }
                return;
            } else {
                if (currenPlayingAnimationDrawable != null) {
                    stopPlay();
                    stopAudioAnimationDrawableAndSetStaticAudioImage(currenPlayingAnimationDrawable, lastClickedAudioImageView, message);
                }
            }
        }

        startPlay(message);
        startAudioAnimationDrawable(audioImageView, message);
    }

    private void stopAudioAnimationDrawableAndSetStaticAudioImage(AnimationDrawable drawable, final ImageView audioImage, final PPMessage message) {
        stopAudioAnimationDrawableAndSetStaticAudioImage(drawable, audioImage, message.getDirection());
    }

    private void stopAudioAnimationDrawableAndSetStaticAudioImage(AnimationDrawable drawable, final ImageView audioImage, final int messageDirection) {
        drawable.stop();
        audioImage.setBackgroundDrawable(null);

        switch (messageDirection) {
            case PPMessage.DIRECTION_INCOMING:
                audioImage.setImageResource(R.drawable.pp_receiver_voice_node_playing);
                break;

            case PPMessage.DIRECTION_OUTGOING:
                audioImage.setImageResource(R.drawable.pp_sender_voice_node_playing);
                break;
        }
    }

    private void startAudioAnimationDrawable(ImageView audioImageView, PPMessage message) {
        switch (message.getDirection()) {
            case PPMessage.DIRECTION_INCOMING:
                audioImageView.setBackgroundResource(R.drawable.pp_chat_item_audio_by_admin_anim_bg);
                break;

            case PPMessage.DIRECTION_OUTGOING:
                audioImageView.setBackgroundResource(R.drawable.pp_chat_item_audio_by_user_anim_bg);
                break;
        }

        audioImageView.setImageDrawable(null);
        AnimationDrawable animationDrawable = (AnimationDrawable) audioImageView.getBackground();
        animationDrawable.start();

        this.lastClickedAudioImageViewRef = new WeakReference<ImageView>(audioImageView);
        this.lastClickedAudioMessageDirection = message.getDirection();
    }

    private void startPlay(PPMessage message) {
        Uri audioUri = parseAudioUri(message);
        startPlay(audioUri);
    }

    private void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void stopPlayQuietly() {
        stopPlay();
        if (lastClickedAudioImageViewRef != null && lastClickedAudioImageViewRef.get() != null) {
            ImageView animatingImageView = lastClickedAudioImageViewRef.get();
            if (animatingImageView.getBackground() instanceof AnimationDrawable) {
                stopAudioAnimationDrawableAndSetStaticAudioImage(
                        (AnimationDrawable) animatingImageView.getBackground(),
                        animatingImageView,
                        lastClickedAudioMessageDirection);
            }
        }
    }

    private Uri parseAudioUri(PPMessage message) {
        Uri audioUri = null;
        PPMessageAudioMediaItem audioMediaItem = (PPMessageAudioMediaItem) message.getMediaItem();
        if (audioMediaItem.getfLocalPath() != null) {
            Uri candidateUri = Uri.parse(audioMediaItem.getfLocalPath());
            // Make sure local file exists
            if (candidateUri.getPath() != null) {
                if (new File(candidateUri.getPath()).exists()) {
                    audioUri = candidateUri;
                }
            }
        }
        if (audioUri == null) {
            if (audioMediaItem.getFurl() != null) {
                audioUri = Uri.parse(audioMediaItem.getFurl());
            }
        }
        return audioUri;
    }

    private void startPlay(Uri audioUri) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    L.w(LOG_MEDIAPLAYER_MEET_ERROR, i, i1);
                    Utils.makeToast(activity, R.string.pp_mediaplayer_play_error);
                    return false; // is error handled
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    L.d(LOG_MEDIAPLAYER_COMPLETED);
                    stopPlayQuietly();
                }
            });
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    L.d(LOG_MEDIAPLAYER_BUFFER_UPDATED, i);
                }
            });

            try {
                mediaPlayer.setDataSource(activity, audioUri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                L.e(e);
                L.w(LOG_MEDIAPLAYER_PREPARE_ERROR, audioUri, e);
                Utils.makeToast(activity, R.string.pp_mediaplayer_play_error);
                return;
            }
        }

        L.d(LOG_MEDIAPLAYER_START_PLAY_URI, audioUri);
        mediaPlayer.start();
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

    protected int getMaxAudioBubbleWidth() {
        return (int) (SCREEN_WIDTH * MAX_AUDIO_WIDTH_RATIO);
    }

    protected int getMinAudioBubbleWidth() {
        return (int) MIN_AUDIO_WIDTH;
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

    private void calcAndSetAudioViewFinalTargetWidth(ViewGroup voiceImageParentView, float duration) {
        float fixDuration = Math.min(MAX_AUDIO_VIEW_TIME, Math.max(duration, 0));
        int baseWidth = getMinAudioBubbleWidth();
        int maxWidth = getMaxAudioBubbleWidth();
        int targetWidth = (int) (baseWidth + (maxWidth - baseWidth) * fixDuration / MAX_AUDIO_VIEW_TIME);
        voiceImageParentView.getLayoutParams().width = targetWidth;
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
            } else if (messageSubType.equals(PPMessage.TYPE_AUDIO)) {
                viewType = ViewType.AUDIO_LEFT;
            }

        } else {

            if (messageSubType.equals(PPMessage.TYPE_TEXT) || messageSubType.equals(PPMessage.TYPE_TXT)) {
                viewType = ViewType.TEXT_RIGHT;
            } else if (messageSubType.equals(PPMessage.TYPE_IMAGE)) {
                viewType = ViewType.IMAGE_RIGHT;
            } else if (messageSubType.equals(PPMessage.TYPE_FILE)) {
                viewType = ViewType.FILE_RIGHT;
            } else if (messageSubType.equals(PPMessage.TYPE_AUDIO)) {
                viewType = ViewType.AUDIO_RIGHT;
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

    /**
     * Left Audio Message ViewHolder
     */
    class ViewHolderLeftAudioMesasge {
        ImageView avatar;
        ImageView audioImage;
        ViewGroup audioImageContainer;
        TextView durationTv;
        TextView timestampTv;
    }

    /**
     * Right Audio Message ViewHolder
     */
    class ViewHolderRightAudioMessage {
        ImageView avatar;
        ImageView audioImage;
        ViewGroup audioImageContainer;
        TextView durationTv;
        TextView timestampTv;
    }

}
