package com.ppmessage.ppcomlib.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.R;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.User;

import java.util.List;

/**
 * Created by ppmessage on 5/18/16.
 */
public class ConversationMembersAdapter extends BaseAdapter {

    private static final int DEFAULT_WIDTH = 64;
    private static final int DEFAULT_HEIGHT = 64;

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;
    private Context context;
    private List<User> userList;
    private LayoutInflater inflater;
    private OnConversationMembersItemClickListener listener;

    public interface OnConversationMembersItemClickListener {
        void onClicked(User user, int position, View convertView);
    }

    public ConversationMembersAdapter(PPComSDK sdk, List<User> userList) {
        this.sdk = sdk;
        this.messageSDK = sdk.getConfiguration().getMessageSDK();
        this.context = sdk.getConfiguration().getContext();
        this.inflater = LayoutInflater.from(context);
        this.userList = userList;
    }

    public void setListener(OnConversationMembersItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return this.userList != null ? this.userList.size() : 0;
    }

    @Override
    public User getItem(int position) {
        return this.userList != null ? this.userList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ppcomlib_conversation_member_item, parent, false);

            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.group_member_avatar);
            holder.name = (TextView) convertView.findViewById(R.id.group_member_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User user = getItem(position);

        holder.name.setText(user.getName());
        messageSDK.getImageLoader().loadImage(user.getIcon(),
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                holder.avatar);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClicked(user, position, v);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView avatar;
        TextView name;
    }

}
