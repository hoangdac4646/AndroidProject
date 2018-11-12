package com.example.root.minigame;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.example.root.minigame.Room;

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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_list__bt__devices, null);
            holder = new ViewHolder();
            holder.IDView = (TextView) convertView.findViewById(R.id.txt_RoomId);
            holder.hostNameView = (TextView) convertView.findViewById(R.id.txt_HostName);
            holder.gameNameView = (TextView) convertView.findViewById(R.id.txt_GameName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Room room = this.listData.get(position);
        holder.hostNameView.setText(room.getHostName());
        holder.gameNameView.setText(room.getGameName());
        holder.IDView.setText(Integer.toString(room.getRoomID()));

        return convertView;
    }

    // Tìm ID của Image ứng với tên của ảnh (Trong thư mục mipmap).
    /*public int getMipmapResIdByName(String resName)  {
        String pkgName = context.getPackageName();

        // Trả về 0 nếu không tìm thấy.
        int resID = context.getResources().getIdentifier(resName , "mipmap", pkgName);
        Log.i("CustomListView", "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }*/

    static class ViewHolder {
        TextView IDView;
        TextView hostNameView;
        TextView gameNameView;
    }

}