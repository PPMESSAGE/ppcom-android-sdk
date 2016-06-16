package com.ppmessage.ppcomlib.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKException;
import com.ppmessage.ppcomlib.R;
import com.ppmessage.ppcomlib.model.ConversationsModel;
import com.ppmessage.ppcomlib.services.ConversationWaitingService;
import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.model.UnackedMessagesLoader;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.notification.WSMessageAckNotificationHandler;
import com.ppmessage.sdk.core.ui.ConversationFragment;
import com.ppmessage.sdk.core.ui.adapter.ConversationsAdapter;
import com.ppmessage.sdk.core.utils.Utils;

import java.util.List;

/**
 * Created by ppmessage on 5/13/16.
 */
public class ConversationsActivity extends AppCompatActivity {

    protected ConversationFragment conversationFragment;
    private Dialog loadingDialog;

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;
    private ConversationWaitingService conversationWaitingService;
    private ConversationsModel conversationsModel;
    private UnackedMessagesLoader unackedMessagesLoader;

    private boolean inWaiting;
    private String waitingGroupUUID;

    private INotification.OnNotificationEvent notificationListener;

    private static final String LOG_WAITING = "[ConversationsActivity] waiting conversations ...";
    private static final String LOG_REMOVE_LISTENER = "[ConversationsActivity] remove notification listener";
    private static final String LOG_ADD_LISTENER = "[ConversationsActivity] add notification listener";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sdk = PPComSDK.getInstance();
        messageSDK = sdk.getConfiguration().getMessageSDK();
        conversationWaitingService = new ConversationWaitingService(sdk);
        conversationsModel = sdk.getMessageService().getConversationsModel();
        unackedMessagesLoader = new UnackedMessagesLoader(messageSDK);

        conversationFragment = getConversationFragment();
        setFragment(conversationFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        conversationFragment.setOnItemClickListener(new ConversationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(final View container, final Conversation conversation) {

                if (!conversation.isGroupType()) {
                    startMessageActivity(conversation);
                } else {
                    conversationsModel.asyncGetGroupConversation(conversation.getGroupUUID(), new ConversationsModel.OnGetConversationEvent() {
                        @Override
                        public void onCompleted(Conversation groupConversation) {
                            if (groupConversation == null) {
                                waiting(conversation.getGroupUUID());
                            } else {
                                startMessageActivity(groupConversation);
                            }
                        }
                    });
                }

            }
        });
        innerResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        removeNotificationListener();
    }

    public void startUp() {
        getLoadingDialog().show();

        sdk.getStartupHelper().startUp(new PPComStartupHelper.OnStartupCallback() {
            @Override
            public void onSuccess() {
                L.d("=== startup success ===");
                ConversationsActivity.this.onStartupSuccess();
            }

            @Override
            public void onError(PPComSDKException exception) {
                L.e("=== startup error: %s ===", exception);
                Utils.makeToast(ConversationsActivity.this, R.string.pp_com_sdk_startup_error);
            }
        });
    }

    /**
     * Override this methods, to provided your own implemented ConversationFragment
     *
     * @return
     */
    protected ConversationFragment getConversationFragment() {
        ConversationFragment fragment = new ConversationFragment();
        fragment.setMessageSDK(sdk.getConfiguration().getMessageSDK());
        return fragment;
    }

    /**
     * Set conversation list to update Conversations content
     *
     * @param conversationList
     */
    public void setConversationList(List<Conversation> conversationList) {
        if (conversationFragment != null) {
            conversationFragment.setConversationList(conversationList);
        }
    }

    protected Dialog getLoadingDialog() {
        if (loadingDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.pp_com_sdk_loading_dialog_content);
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancelWaiting(waitingGroupUUID);
                }
            });

            loadingDialog = progressDialog;
        }
        return loadingDialog;
    }

    private void innerResume() {
        notifyDataSetChanged();
        addNotificationListener();
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    private void onStartupSuccess() {
        conversationsModel.asyncGetConversations(new ConversationsModel.OnGetConversationsEvent() {
            @Override
            public void onCompleted(List<Conversation> conversationList) {
                if (conversationList != null) {

                    setConversationList(conversationsModel.sortedConversations());
                    loadUnackedMessages();
                    getLoadingDialog().hide();

                } else {
                    waiting(null);
                }
            }
        });
    }

    private void waiting(String groupUUID) {
        if (groupUUID != null) {
            waitingGroupUUID = groupUUID;
        }
        inWaiting = true;
        L.d(LOG_WAITING);
    }

    private void addNotificationListener() {
        L.d(LOG_ADD_LISTENER);

        INotification notification = sdk.getConfiguration().getMessageSDK().getNotification();
        notificationListener = new INotification.SimpleNotificationEvent() {

            @Override
            public int getInterestedEvent() {
                return INotification.EVENT_CONVERSATION |
                        INotification.EVENT_MESSAGE |
                        INotification.EVENT_MSG_SEND_ERROR;
            }

            @Override
            public void onMessageInfoArrived(PPMessage message) {

                sdk.getMessageService().updateModels(message);
                notifyDataSetChanged();

            }

            @Override
            public void onConversationInfoArrived(Conversation conversation) {
                if (conversation != null) {

                    sdk.getMessageService().getConversationsModel().add(conversation);
                    notifyDataSetChanged();

                    cancelWaiting(waitingGroupUUID);
                }
            }

            @Override
            public void onMessageSendError(WSMessageAckNotificationHandler.MessageSendResult messageSendResult) {
                PPMessage find = sdk.getMessageService().getMessagesModel()
                        .findMessage(messageSendResult.getConversationUUID(),
                                messageSendResult.getMessageUUID());
                if (find != null) {
                    find.setError(true);
                }
            }

        };
        notification.addListener(notificationListener);
    }

    private void removeNotificationListener() {
        L.d(LOG_REMOVE_LISTENER);

        if (notificationListener != null) {
            sdk.getConfiguration().getMessageSDK().getNotification().removeListener(notificationListener);
            notificationListener = null;
        }
    }

    private void notifyDataSetChanged() {
        setConversationList(conversationsModel.sortedConversations());
    }

    private void cancelWaiting(final String groupUUID) {
        getLoadingDialog().hide();

        if (!inWaiting) return;

        inWaiting = false;
        if (conversationWaitingService != null) {
            conversationWaitingService.cancel(groupUUID);
        }
    }

    private void startMessageActivity(Conversation conversation) {
        if (conversation == null || conversation.getConversationUUID() == null) return;

        Intent intent = new Intent(ConversationsActivity.this, PPComMessageActivity.class);
        intent.putExtra(PPComMessageActivity.EXTRA_KEY_CONVERSATION_UUID, conversation.getConversationUUID());
        intent.putExtra(PPComMessageActivity.EXTRA_KEY_CONVERSATION_NAME, conversation.getConversationName());
        startActivity(intent);
    }

    private void loadUnackedMessages() {
        if (unackedMessagesLoader != null) {
            unackedMessagesLoader.loadUnackedMessages();
        }
    }

}
