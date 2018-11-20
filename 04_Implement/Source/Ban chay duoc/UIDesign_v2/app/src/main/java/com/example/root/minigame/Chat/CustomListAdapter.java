package com.example.root.minigame.Chat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.example.root.minigame.Classes.Room;
import com.example.root.minigame.R;

public class CustomListAdapter extends BaseAdapter {

    private List<Room> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<Room> listData) {
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
            holder.hostNameView = (TextView) convertView.findViewById(R.id.txt_hostName);
            holder.gameNameView = (TextView) convertView.findViewById(R.id.txt_gameName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Room room = this.listData.get(position);
        holder.hostNameView.setText("Host: "+ room.getHostName());
        holder.gameNameView.setText("Game: " + room.getGameName());
        holder.avatarView.setBackgroundResource(room.getAvatarID());

        return convertView;
    }

    static class ViewHolder {
        ImageView avatarView;
        TextView hostNameView;
        TextView gameNameView;
    }

}