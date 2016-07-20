package com.ppmessage.sdk.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageAudioMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageImageMediaItem;
import com.ppmessage.sdk.core.ui.adapter.MessageAdapter;
import com.ppmessage.sdk.core.ui.view.MessageListView;
import com.ppmessage.sdk.core.utils.AudioRecordMicPhoneStatusUtil;
import com.ppmessage.sdk.core.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ppmessage on 5/11/16.
 */
public class MessageActivity extends AppCompatActivity {

    /** Image capture **/
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    /** Pick image from gallery **/
    public static final int REQUEST_PICK_IMAGES_FROM_GALLERY = 2;

    private static final String TEXT_EMPTY_LOG = "[Send] text == nil";
    private static final String SDK_EMPTY_LOG = "[Send] SDK == nil";
    private static final String CONVERSATION_EMTPY_LOG = "[Send] conversation == nil";
    private static final String FROMUSER_EMPTY_LOG = "[Send] FromUser == nil";
    private static final String CLICK_EVENT_WARNING = "[MessageActivity] Click event, skip send recording, time diff:%d";
    private static final String CANCEL_RECORDING_CANCEL_SENDING = "[MessageActivity] cancel recording, cancel sending audio";
    private static final String EXTERNAL_STORAGE_NOT_OK = "[MessageActivity] external storage cannot writeable, skip record";
    private static final String PHOTOPATH_EMPTY = "[MessageActivity] photo path == null";
    private static final String PHOTOPATH_FILE_NOT_EXIST = "[MessageActivity] photo not exist in path:%s.";
    private static final String PICK_IMAGE_FROM_GALLERY_DATA_EMPTY = "[MessageActivity] pick image from gallery, data == null";
    private static final String PICK_IMAGE_FROM_GALLERY_PATH = "[MessageActivity] pick image from gallery, data path: %s";

    protected MessageListView messageListView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected MessageAdapter messageAdapter;
    protected TextView sendButton;
    protected EditText inputEt;
    protected ViewGroup inputEtContainer;
    protected TextView holdToTalkButton;
    protected ImageView keyboardButton;
    protected ImageView voiceButton;
    protected ImageView moreButton;

    protected ViewStub recordingViewStub;
    protected View recordingView;
    protected ViewGroup recordingImageViewContainer;
    protected ImageView recordingCancelImageView;
    protected TextView recordingStateTv;
    protected ImageView recordingStateImageView;

    private PPMessageSDK sdk;

    private Conversation conversation;

    private float actionDownY;
    private long actionDownTimestamp;
    private static final long RECORDING_MIN_TIME_MS = 300; //300ms
    private static final float RECORDING_CANCEL_MIN_DISTANCE = 300;

    private static final String AUDIO_RECORDING_FOLDER_NAME = "audio-cache";
    private static final String AUDIO_MIME = "audio/amr";
    private MediaRecorder mediaRecorder;
    private String recordingAudioFilePath;
    private long audioRecordStartTimestamp;
    private AudioRecordMicPhoneStatusUtil micPhoneStatusUtil;

    private InputMethodManager imm;
    private String textInput;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_chat);

        messageListView = (MessageListView) findViewById(R.id.pp_chat_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pp_chat_swipe_refresh_layout);
        sendButton = (TextView) findViewById(R.id.pp_chat_tools_send_btn);
        inputEt = (EditText) findViewById(R.id.pp_chat_tools_input_et);
        inputEtContainer = (ViewGroup) findViewById(R.id.pp_chat_tools_input_et_container);
        holdToTalkButton = (TextView) findViewById(R.id.pp_chat_tools_hold_voice_btn);
        voiceButton = (ImageView) findViewById(R.id.pp_chat_tools_voice_btn);
        keyboardButton = (ImageView) findViewById(R.id.pp_chat_tools_keyboard_btn);
        moreButton = (ImageView) findViewById(R.id.pp_chat_tools_more_btn);

        recordingViewStub = (ViewStub) findViewById(R.id.pp_recording_view_import);

        sendButton.setVisibility(View.GONE);
        moreButton.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
        swipeRefreshLayout.setEnabled(false);

        holdToTalkButton.setVisibility(View.GONE);
        keyboardButton.setVisibility(View.GONE);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Avoid keyboard auto popup
        hideKeyboard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
        safeStopPlayAudio();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) { // Take Photo
                if (imagePath == null) {
                    L.w(PHOTOPATH_EMPTY);
                    return;
                }
                if (!new File(imagePath).exists()) {
                    L.w(PHOTOPATH_FILE_NOT_EXIST, imagePath);
                    return;
                }
                notifyGalleryAddPhoto(imagePath);
                sendImageMessage(imagePath);
                imagePath = null;
            } else if (requestCode == REQUEST_PICK_IMAGES_FROM_GALLERY) { // Pick From Gallery
                if (data == null) {
                    L.w(PICK_IMAGE_FROM_GALLERY_DATA_EMPTY);
                    return;
                }

                Uri imageUri = data.getData();
                L.d(PICK_IMAGE_FROM_GALLERY_PATH, imageUri);
                sendImageMessage(imageUri);
            }
        }
    }

    public void setAdapter(MessageAdapter adapter) {
        if (this.messageAdapter != adapter) {
            this.messageAdapter = adapter;
            this.messageListView.setAdapter(this.messageAdapter);
            this.messageAdapter.notifyDataSetChanged();
        }
    }

    public void setMessageSDK(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    protected void onSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout) {
    }

    // === MessageActivity Business Logic ===

    private void initEvent() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText(inputEt.getText().toString());
                inputEt.setText("");
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreButtonClicked(view);
            }
        });

        inputEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageListView.scrollToBottom();
            }
        });

        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setEnabled(
                        conversation != null &&
                                sdk != null &&
                                s.toString().length() > 0
                );
                moreButton.setVisibility(sendButton.isEnabled() ? View.GONE : View.VISIBLE);
                sendButton.setVisibility(sendButton.isEnabled() ? View.VISIBLE : View.GONE);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MessageActivity.this.onSwipeRefresh(swipeRefreshLayout);
            }
        });

        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardButton.setVisibility(View.GONE);
                voiceButton.setVisibility(View.VISIBLE);
                holdToTalkButton.setVisibility(View.GONE);
                inputEtContainer.setVisibility(View.VISIBLE);

                inputEt.requestFocus();
                imm.showSoftInput(inputEt, 0);
                // Instead directly use the method: inputEt.setText(textInput), the following way can
                // place the cursor at the end of current focused EditText
                inputEt.setText("");
                inputEt.append(textInput);
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardButton.setVisibility(View.VISIBLE);
                voiceButton.setVisibility(View.GONE);
                holdToTalkButton.setVisibility(View.VISIBLE);
                inputEtContainer.setVisibility(View.GONE);

                imm.hideSoftInputFromWindow(inputEt.getWindowToken(), 0);
                textInput = inputEt.getText().toString();
            }
        });

        holdToTalkButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        onActionTouchDown(motionEvent);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        onActionTouchMove(motionEvent);
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        onActionTouchUp(motionEvent);
                        return true;
                }
                return false;
            }
        });
    }

    // ========================
    // Send message
    // ========================

    private PPMessage sendText(String text) {
        PPMessage message = null;
        if (checkInfoBeforeSendMessage(text)) {
            message = buildMessage(text);
            sendMessage(message);
        }
        return message;
    }

    private PPMessage sendAudio(String audioFilePath, float durationInMS, String mime) {
        PPMessage message = null;
        if (checkCommonInfoBeforeSendMessage()) {
            message = buildMessage(audioFilePath, durationInMS, mime);
            sendMessage(message);
        }
        return message;
    }

    private PPMessage sendImageMessage(String imagePath) {
        PPMessage message = null;
        if (checkCommonInfoBeforeSendMessage()) {
            message = buildImageMessage(imagePath);
            sendMessage(message);
        }
        return message;
    }

    private PPMessage sendImageMessage(Uri imagePathUri) {
        PPMessage message = null;
        if (checkCommonInfoBeforeSendMessage()) {
            message = buildImageMessage(imagePathUri);
            sendMessage(message);
        }
        return message;
    }

    private void sendMessage(PPMessage message) {
        if (!sdk.getNotification().canSendMessage()) {
            message.setError(true);
        } else {
            sdk.getNotification().sendMessage(message);
        }
        onMessageSendFinish(message);
    }

    private PPMessage buildMessage(String text) {
        return new PPMessage.Builder()
                .setFromUser(sdk.getNotification().getConfig().getActiveUser())
                .setConversation(conversation)
                .setMessageBody(text)
                .build();
    }

    private PPMessage buildMessage(String audioFilePath, float duration, String mime) {
        PPMessageAudioMediaItem audioMediaPart = new PPMessageAudioMediaItem();
        audioMediaPart.setMime(mime);
        audioMediaPart.setDuration(duration);
        audioMediaPart.setfLocalPath(audioFilePath);

        return new PPMessage.Builder()
                .setFromUser(sdk.getNotification().getConfig().getActiveUser())
                .setConversation(conversation)
                .setMediaItem(audioMediaPart)
                .build();
    }

    private PPMessage buildImageMessage(String imagePath) {
        return buildImageMessage(Uri.fromFile(new File(imagePath)));
    }

    private PPMessage buildImageMessage(Uri imageUri) {
        PPMessageImageMediaItem imageMediaItem = new PPMessageImageMediaItem();
        imageMediaItem.setOrigUrl(imageUri.toString());
        calcAndSetImageWidthAndHeight(imageMediaItem, imageUri);

        return new PPMessage.Builder()
                .setFromUser(sdk.getNotification().getConfig().getActiveUser())
                .setConversation(conversation)
                .setMediaItem(imageMediaItem)
                .build();
    }

    private boolean checkInfoBeforeSendMessage(String text) {
        if (!checkCommonInfoBeforeSendMessage()) {
            return false;
        }
        if (TextUtils.isEmpty(text)) {
            L.w(TEXT_EMPTY_LOG);
            return false;
        }
        return true;
    }

    private boolean checkCommonInfoBeforeSendMessage() {
        if (sdk == null) {
            L.w(SDK_EMPTY_LOG);
            return false;
        }
        if (conversation == null) {
            L.w(CONVERSATION_EMTPY_LOG);
            return false;
        }
        if (sdk.getNotification().getConfig().getActiveUser() == null) {
            L.w(FROMUSER_EMPTY_LOG);
            return false;
        }
        return true;
    }

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected void onMessageSendFinish(PPMessage message) {

    }

    // ==========================
    // Recording Touch Event
    // ==========================

    private void onActionTouchDown(MotionEvent motionEvent) {

        if (recordingView == null) {
            recordingView = recordingViewStub.inflate();

            recordingImageViewContainer = (ViewGroup) recordingView.findViewById(R.id.pp_recording_imageview_container);
            recordingStateTv = (TextView) recordingView.findViewById(R.id.pp_recording_view_state_description);
            recordingCancelImageView = (ImageView) recordingView.findViewById(R.id.pp_recording_view_cancel_imageview);
            recordingStateImageView = (ImageView) recordingView.findViewById(R.id.pp_recording_state_imageview);
        }

        if (recordingView != null) {
            recordingView.setVisibility(View.VISIBLE);
        }

        if (recordingStateTv != null) {
            recordingStateTv.setBackgroundResource(0);
            recordingStateTv.setText(R.string.pp_chat_tools_hold_voice_slide_up_to_cancel);
        }

        if (recordingImageViewContainer != null) {
            recordingImageViewContainer.setVisibility(View.VISIBLE);
        }

        if (recordingCancelImageView != null) {
            recordingCancelImageView.setVisibility(View.GONE);
        }

        if (holdToTalkButton != null) {
            holdToTalkButton.setPressed(true);
            holdToTalkButton.setText(R.string.pp_chat_tools_hold_voice_release_to_send);
        }

        actionDownTimestamp = System.currentTimeMillis();
        actionDownY = motionEvent.getY();

        startRecording();
    }

    private void onActionTouchMove(MotionEvent motionEvent) {
        boolean cancelImageVisible = (actionDownY - motionEvent.getY()) >= RECORDING_CANCEL_MIN_DISTANCE;
        if (recordingCancelImageView != null) {
            recordingCancelImageView.setVisibility(cancelImageVisible ? View.VISIBLE : View.GONE);
        }
        if (recordingImageViewContainer != null) {
            recordingImageViewContainer.setVisibility(cancelImageVisible ? View.GONE : View.VISIBLE);
        }
        if (recordingStateTv != null) {
            recordingStateTv.setText(cancelImageVisible ?
                    R.string.pp_chat_tools_hold_voice_release_to_cancel :
                    R.string.pp_chat_tools_hold_voice_slide_up_to_cancel);
            recordingStateTv.setBackgroundResource(cancelImageVisible ?
                    R.drawable.pp_chat_recording_view_state_bg :
                    0);
        }
        if (holdToTalkButton != null) {
            holdToTalkButton.setText(cancelImageVisible ?
                    R.string.pp_chat_tools_hold_voice_release_to_cancel :
                    R.string.pp_chat_tools_hold_voice_release_to_send);
        }
    }

    private void onActionTouchUp(MotionEvent motionEvent) {
        if (recordingView != null) {
            recordingView.setVisibility(View.GONE);
        }

        if (holdToTalkButton != null) {
            holdToTalkButton.setPressed(false);
            holdToTalkButton.setText(R.string.pp_chat_tools_hold_voice_hold_to_talk);
        }

        long timeDiff = System.currentTimeMillis() - actionDownTimestamp;
        if (timeDiff < RECORDING_MIN_TIME_MS) {
            L.w(CLICK_EVENT_WARNING, timeDiff);
            cancelRecording();
            return;
        }

        if (recordingCancelImageView.getVisibility() == View.VISIBLE) {
            L.d(CANCEL_RECORDING_CANCEL_SENDING);
            cancelRecording();
            return;
        }

        stopRecording();
        trySendAudio();
    }

    private void startRecording() {
        if (!Utils.isExternalStorageWritable()) {
            L.w(EXTERNAL_STORAGE_NOT_OK);
            Toast.makeText(MessageActivity.this, R.string.pp_external_storage_not_avaliable, Toast.LENGTH_SHORT).show();
            return;
        }

        // Before start recording, make sure we have stop the audio mediaPlayer first.
        safeStopPlayAudio();

        File audioCacheDir = new File(getCacheDir(), AUDIO_RECORDING_FOLDER_NAME);
        audioCacheDir.mkdirs();
        File audioFile = new File(audioCacheDir, generateAudioFileName());
        this.recordingAudioFilePath = audioFile.getPath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(recordingAudioFilePath);
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mediaRecorder, int i, int i1) {
                L.d("[MediaRecorder] error %d: %d", i, i1);
                stopRecording();
                Toast.makeText(MessageActivity.this, R.string.pp_recording_audio_error, Toast.LENGTH_SHORT).show();
            }
        });
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                L.d("[MediaRecorder] info %d: %d", i, i1);
            }
        });

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            audioRecordStartTimestamp = System.currentTimeMillis();
        } catch (IOException e) {
            L.e(e);
            Toast.makeText(MessageActivity.this, R.string.pp_recording_audio_error, Toast.LENGTH_SHORT).show();
            return;
        }

        micPhoneStatusUtil = new AudioRecordMicPhoneStatusUtil(mediaRecorder);
        micPhoneStatusUtil.setMicPhoneVolumeChangedEvent(new AudioRecordMicPhoneStatusUtil.OnMicPhoneVolumeChangedEvent() {
            @Override
            public void onVolumeChanged(double currentDb, double ratio) {
                // We have 8 pngs
                if (recordingStateImageView != null) {
                    int stateImageResId = R.drawable.pp_recording_signal_001;
                    if (ratio < 0.125) {
                        stateImageResId = R.drawable.pp_recording_signal_001;
                    } else if (ratio < 0.250) {
                        stateImageResId = R.drawable.pp_recording_signal_002;
                    } else if (ratio < 0.375) {
                        stateImageResId = R.drawable.pp_recording_signal_003;
                    } else if (ratio < 0.500) {
                        stateImageResId = R.drawable.pp_recording_signal_004;
                    } else if (ratio < 0.625) {
                        stateImageResId = R.drawable.pp_recording_signal_005;
                    } else if (ratio < 0.750) {
                        stateImageResId = R.drawable.pp_recording_signal_006;
                    } else if (ratio < 0.875) {
                        stateImageResId = R.drawable.pp_recording_signal_007;
                    } else {
                        stateImageResId = R.drawable.pp_recording_signal_008;
                    }
                    recordingStateImageView.setImageResource(stateImageResId);
                }
            }
        });
        micPhoneStatusUtil.start();
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (micPhoneStatusUtil != null) {
            micPhoneStatusUtil.stop();
            micPhoneStatusUtil = null;
        }
    }

    private void cancelRecording() {
        stopRecording();
        if (recordingAudioFilePath != null) {
            File audio = new File(recordingAudioFilePath);
            audio.deleteOnExit();
            recordingAudioFilePath = null;
        }
    }

    private String generateAudioFileName() {
        return "audio-" + System.currentTimeMillis() + ".amr";
    }

    private void trySendAudio() {
        if (recordingAudioFilePath != null) {
            File audio = new File(recordingAudioFilePath);
            if (audio.exists() && audio.length() > 0) {
                long durationInMS = System.currentTimeMillis() - audioRecordStartTimestamp;
                sendAudio(recordingAudioFilePath, (float) (((double) durationInMS) / 1000), AUDIO_MIME);
            }
            recordingAudioFilePath = null;
        }
    }

    private void safeStopPlayAudio() {
        if (messageListView != null && messageListView.getAdapter() != null) {
            MessageAdapter messageAdapter = (MessageAdapter) messageListView.getAdapter();
            if (messageAdapter != null) {
                messageAdapter.stopPlayQuietly();
            }
        }
    }

    // =========================================================
    // More Button ==[current]==> Image Button
    // =========================================================
    private void onMoreButtonClicked(final View view) {
        showPhotoPickerDialog();
    }

    private void showPhotoPickerDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.pp_chat_photo_picker_items,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        takePicture();
                                        break;

                                    case 1:
                                        pickImage();
                                        break;
                                }
                            }
                        }).create().show();
    }

    /**
     * Take a Photo with the Camera App
     */
    private void takePicture() {
        if (!checkSendImageMessageRequirements()) {
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File imageFile = new File(Utils.getPublicImageFolder(), buildImageFileName());
            imagePath = imageFile.getAbsolutePath();
            // Continue only if the File was successfully created
            if (imagePath != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Pick image from gallery
     */
    private void pickImage() {
        if (!checkSendImageMessageRequirements()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGES_FROM_GALLERY);
    }

    private String buildImageFileName() {
        return "image-" + System.currentTimeMillis() + ".jpg";
    }

    private void notifyGalleryAddPhoto(String photoPath) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void calcAndSetImageWidthAndHeight(PPMessageImageMediaItem imageMediaItem, Uri uri) {
        BitmapFactory.Options options = createBitmapOptions();
        BitmapFactory.decodeFile(uri.getPath(), options);
        imageMediaItem.setOrigWidth(options.outWidth);
        imageMediaItem.setOrigHeight(options.outHeight);
        options.inJustDecodeBounds = false;
    }

    private static BitmapFactory.Options createBitmapOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        return options;
    }

    private boolean checkSendImageMessageRequirements() {
        if (!Utils.isExternalStorageWritable()) {
            Utils.makeToast(this, R.string.pp_external_storage_not_avaliable);
            return false;
        }
        return true;
    }

}
