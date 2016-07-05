package com.ppmessage.ppcomlib.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.R;
import com.ppmessage.ppcomlib.model.ConversationMemberModel;
import com.ppmessage.ppcomlib.model.ConversationsModel;
import com.ppmessage.ppcomlib.model.PPComMessagesModel;
import com.ppmessage.ppcomlib.ui.adapter.ConversationMembersAdapter;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;

import java.util.List;
import java.util.Iterator;

/**
 * Created by ppmessage on 5/13/16.
 */
public class ConversationMemberActivity extends AppCompatActivity {

    public static final String EXTRA_CONVERSATION_UUID = "com.ppmessage.ppcomlib.ui.ConversationMemberActivity.conversation_uuid";

    private GridView gridView;

    private String conversationUUID;
    private PPComSDK sdk;

    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppcomlib_conversation_member_activity);
        gridView = (GridView) findViewById(R.id.gridView);

        setTitle(R.string.ppcom_sdk_conversation_members_activity_name);

        sdk = PPComSDK.getInstance();
        conversationUUID = (String) getIntent().getStringExtra(EXTRA_CONVERSATION_UUID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (conversationUUID != null) {
            startLoading();

            ConversationMemberModel conversationMemberModel = sdk.getMessageService().getConversationMemberModel();
            conversationMemberModel.getMembers(conversationUUID, new ConversationMemberModel.OnGetConversationMembersEvent() {
                @Override
                public void onCompleted(List<User> userList) {
                    stopLoading();
                    updateGridView(userList);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateGridView(List<User> userList) {

        User comUser = sdk.getStartupHelper().getComUser().getUser();
        for (Iterator<User> iter = userList.listIterator(); iter.hasNext(); ) {
            User u = iter.next();
            if (u.getUuid().equals(comUser.getUuid())) {
                iter.remove();
            }
        }

        final ConversationMembersAdapter conversationMembersAdapter = new ConversationMembersAdapter(sdk, userList);
        conversationMembersAdapter.setListener(new ConversationMembersAdapter.OnConversationMembersItemClickListener() {
            @Override
            public void onClicked(User user, int position, View convertView) {
                if (user.getUuid() == null) return;

                User comUser = sdk.getStartupHelper().getComUser().getUser();
                if (comUser == null) return;

                L.d(comUser.getUuid());
                L.d(user.getUuid());

                if (user.getUuid().equals(comUser.getUuid())) return;

                chattingWithUser(user);
            }
        });
        gridView.setAdapter(conversationMembersAdapter);
    }

    private void chattingWithUser(final User user) {
        startLoading();

        ConversationsModel conversationsModel = sdk.getMessageService().getConversationsModel();
        conversationsModel.asyncGetUserConversation(user.getUuid(), new ConversationsModel.OnGetConversationEvent() {
            @Override
            public void onCompleted(Conversation conversation) {
                stopLoading();

                if (conversation != null) {
                    Intent intent = new Intent(ConversationMemberActivity.this, PPComMessageActivity.class);
                    intent.putExtra(PPComMessageActivity.EXTRA_KEY_CONVERSATION_NAME, conversation.getConversationName());
                    intent.putExtra(PPComMessageActivity.EXTRA_KEY_CONVERSATION_UUID, conversation.getConversationUUID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private void startLoading() {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setTitle(R.string.pp_com_sdk_loading_dialog_content);
        }
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }

    private void stopLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

}
