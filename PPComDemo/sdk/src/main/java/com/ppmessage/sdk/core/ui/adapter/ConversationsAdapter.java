package com.ppmessage.sdk.core.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.ui.view.CircularImageView;
import com.ppmessage.sdk.core.utils.IImageLoader;
import com.ppmessage.sdk.core.utils.Utils;

import java.util.List;

/**
 * Created by ppmessage on 5/13/16.
 */
public class ConversationsAdapter extends BaseAdapter {

    private static final String ITEM_CLICK_LOG = "[ConversationsAdapter] item clicked: %s";

    private static final int DEFAULT_WIDTH = 48;
    private static final int DEFAULT_HEIGHT = 48;

    public interface OnItemClickListener {
        void onItemClicked(View container, Conversation conversation);
    }

    private List<Conversation> conversationList;
    private PPMessageSDK sdk;
    private LayoutInflater mInflater;
    private IImageLoader imageLoader;

    private OnItemClickListener itemClickListener;

    public ConversationsAdapter(PPMessageSDK sdk, List<Conversation> conversationList) {
        this.sdk = sdk;
        this.imageLoader = sdk.getImageLoader();
        this.conversationList = conversationList;
    }

    @Override
    public int getCount() {
        return conversationList != null ? conversationList.size() : 0;
    }

    @Override
    public Conversation getItem(int position) {
        return conversationList != null ? conversationList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PPConversationsItemViewHolder holder = null;

        if (mInflater == null) {
            mInflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.pp_conversations_content_item, parent, false);
            holder = new PPConversationsItemViewHolder();
            holder.messageSummaryTv = (TextView) convertView.findViewById(R.id.pp_conversations_content_item_message_summary);
            holder.messageTimestampTv = (TextView) convertView.findViewById(R.id.pp_conversations_content_item_message_timestamp);
            holder.userNameTv = (TextView) convertView.findViewById(R.id.pp_conversations_content_item_user_name);
            holder.userAvatarImg = (CircularImageView) convertView.findViewById(R.id.pp_conversations_content_item_user_avatar);
            holder.itemContainer = (ViewGroup) convertView.findViewById(R.id.pp_conversations_item_container);
            convertView.setTag(holder);
        } else {
            holder = (PPConversationsItemViewHolder) convertView.getTag();
        }

        Conversation item = getItem(position);
        if (item != null) {
            holder.messageSummaryTv.setText(item.getConversationSummary());
            holder.messageTimestampTv.setText(Utils.formatTimestamp(item.getUpdateTimestamp()));
            holder.userNameTv.setText(item.getConversationName());
            imageLoader.loadImage(item.getConversationIcon(),
                    DEFAULT_WIDTH,
                    DEFAULT_HEIGHT,
                    R.drawable.pp_icon_avatar,
                    holder.userAvatarImg);
            setItemOnClickListener(convertView, item);
        }

        return convertView;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
        notifyDataSetChanged();
    }

    public void setOnItemClickedListener(ConversationsAdapter.OnItemClickListener itemClickedListener) {
        this.itemClickListener = itemClickedListener;
    }

    private void setItemOnClickListener(final View container,
                                        final Conversation item) {
        if (container != null) {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        L.d(ITEM_CLICK_LOG, item);
                        itemClickListener.onItemClicked(container, item);
                    }
                }
            });
        }
    }

    /**
     * ViewHolder
     * @author zhaokun
     *
     */
    class PPConversationsItemViewHolder {
        ViewGroup itemContainer;
        CircularImageView userAvatarImg;
        TextView userNameTv;
        TextView messageSummaryTv;
        TextView messageTimestampTv;
    }

}
