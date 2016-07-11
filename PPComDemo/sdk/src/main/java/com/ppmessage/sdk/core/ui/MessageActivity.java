package com.ppmessage.sdk.core.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnHttpRequestCompleted;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageImageMediaItem;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.ui.adapter.MessageAdapter;
import com.ppmessage.sdk.core.ui.view.MessageListView;
import com.ppmessage.sdk.core.utils.Uploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ppmessage on 5/11/16.
 */
public class MessageActivity extends AppCompatActivity {

    private static final String FILE_NOT_EXIST_LOG = "[Send] file == not exist";
    private static final String TEXT_EMPTY_LOG = "[Send] text == nil";
    private static final String SDK_EMPTY_LOG = "[Send] SDK == nil";
    private static final String CONVERSATION_EMTPY_LOG = "[Send] conversation == nil";
    private static final String FROMUSER_EMPTY_LOG = "[Send] FromUser == nil";

    protected MessageListView messageListView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected MessageAdapter messageAdapter;
    protected TextView sendButton;
    protected EditText inputEt;

    private PPMessageSDK sdk;

    private Conversation conversation;

    protected ImageView leftIconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_chat);

        messageListView = (MessageListView) findViewById(R.id.pp_chat_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pp_chat_swipe_refresh_layout);
        sendButton = (TextView) findViewById(R.id.pp_chat_tools_send_btn);
        inputEt = (EditText) findViewById(R.id.pp_chat_tools_input_et);
        leftIconView= (ImageView)findViewById(R.id.pp_chat_tools_left_icon);

        sendButton.setEnabled(false);
        swipeRefreshLayout.setEnabled(false);

        // Avoid keyboard auto popup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initEvent();

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

    protected void onTextMessageSendFinish(PPMessage message) {
        inputEt.setText("");
    }

    protected void onSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout) {

    }

    // === MessageActivity Business Logic ===

    private void initEvent() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PPMessage message = sendText(inputEt.getText().toString());
                onTextMessageSendFinish(message);
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
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MessageActivity.this.onSwipeRefresh(swipeRefreshLayout);
            }
        });

        leftIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    protected PPMessage sendText(String text) {
        PPMessage message = null;
        if (checkInfoBeforeSendMessage(text)) {
            message = new PPMessage.Builder()
                    .setFromUser(sdk.getNotification().getConfig().getActiveUser())
                    .setConversation(conversation)
                    .setMessageBody(text)
                    .build();
            if (!sdk.getNotification().canSendMessage()) {
                message.setError(true);
            } else {
                sdk.getNotification().sendMessage(message);
            }
        }
        return message;
    }

    protected PPMessage sendImageFile(File imageFile) {
        if (checkInfoBeforeSendFile(imageFile)) {
            new Uploader().uploadFile(imageFile,
                    sdk.getNotification().getConfig().getActiveUser().getUuid(),
                    new Uploader.OnUploadingListener() {
                        @Override
                        public void onError(Exception e) {

                        }

                        @Override
                        public void onComplected(JSONObject response) {
                            try {
                                String fuuid = (String)response.get("fuuid");
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("fid", fuuid);
                                jsonObject.put("mime","image/jpg");
                                PPMessage message = new PPMessage.Builder()
                                    .setFromUser(sdk.getNotification().getConfig().getActiveUser())
                                    .setConversation(conversation)
                                    .setMessageBody(jsonObject.toString())
                                    .build();
                                message.setMessageSubType("IMAGE");
                                if (!sdk.getNotification().canSendMessage()) {
                                    message.setError(true);
                                } else {
                                    sdk.getNotification().sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        return null;
    }

    private boolean checkInfoBeforeSendMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            L.w(TEXT_EMPTY_LOG);
            return false;
        }
        return checkConnectionInfoBeforeSend();
    }

    private boolean checkInfoBeforeSendFile(File sendFile) {
        if (!sendFile.exists()) {
            L.w(FILE_NOT_EXIST_LOG);
            return false;
        }
        return checkConnectionInfoBeforeSend();
    }

    private boolean checkConnectionInfoBeforeSend() {
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

    public void selectImage() {

        CharSequence[] items = {"相册", "相机"};
        new AlertDialog.Builder(this)
                .setTitle("选择图片来源")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0 ){
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "选择图片"), 0);
                        }else{
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        }
                    }
                })
                .create().show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        //选择完或者拍完照后会在这里处理，然后我们继续使用setResult返回Intent以便可以传递数据和调用
        if (data.getExtras() != null)
            getIntent().putExtras(data.getExtras());
        if (data.getData()!= null)
            getIntent().setData(data.getData());
        handleResult(1, getIntent());
    }

    protected void handleResult(int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                if (data != null) {
                    //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
                    Uri mImageCaptureUri = data.getData();
                    Bitmap image = null;
                    //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
                    if (mImageCaptureUri != null) {
                        try {
                            //这个方法是根据Uri获取Bitmap图片的静态方法
                            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                            image = extras.getParcelable("data");
                        }
                    }

                    if (image != null) {
                        FileOutputStream out = null;
                        File cacheDir = getExternalCacheDir();
                        if (cacheDir== null) {
                            cacheDir =getCacheDir();
                        }
                        String filePath = cacheDir.getAbsolutePath()+"/"+System.currentTimeMillis();
                        try {
                            out = new FileOutputStream(filePath);
                            image.compress(Bitmap.CompressFormat.JPEG, 60, out); // bmp is your Bitmap instance
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        File saveImageFile = new File(filePath);
                        if (saveImageFile.exists()) {
                            sendImageFile(saveImageFile);
                        }
                    }
                }
                break;
            default:
                break;

        }
    }
}
