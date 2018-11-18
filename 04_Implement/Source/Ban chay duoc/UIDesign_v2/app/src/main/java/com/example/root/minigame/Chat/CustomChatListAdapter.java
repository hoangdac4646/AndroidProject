package com.example.root.minigame.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.minigame.R;

import java.util.List;

public class CustomChatListAdapter extends BaseAdapter {

    private List<ChatMessage> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomChatListAdapter(Context aContext,  List<ChatMessage> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_list__bt__devices, null);
            holder = new ViewHolder();
            holder.avatarView = (ImageView) convertView.findViewById(R.id.iv_roomAvatar);
            holder.playerNameView = (TextView) convertView.findViewById(R.id.txt_hostName);
            holder.messageView = (TextView) convertView.findViewById(R.id.txt_gameName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatMessage cm = this.listData.get(position);
        holder.playerNameView.setText(cm.getPlayer().getPlayerName());
        holder.avatarView.setBackgroundResource(cm.getPlayer().getAvatarID());
        holder.messageView.setText(cm.getMessage());

        return convertView;
    }

    static class ViewHolder {
        ImageView avatarView;
        TextView playerNameView;
        TextView messageView;
    }

}
