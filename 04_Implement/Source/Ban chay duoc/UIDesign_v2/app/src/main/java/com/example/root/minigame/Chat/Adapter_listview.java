package com.example.root.minigame.Chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.root.minigame.R;

import java.util.List;

public class Adapter_listview extends ArrayAdapter<SendMessage> {
    Context context;
    int resource;
     List<SendMessage> objects;
    public Adapter_listview(Context context, int resource, @NonNull List<SendMessage> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.resource =resource;
    }

    public class ViewHolder {
        private TextView ThietBi,Noidung;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = new ViewHolder();
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_listview,parent,false);
            holder.ThietBi = view.findViewById(R.id.tv_deviceName);
            holder.Noidung  = view.findViewById(R.id.tv_content);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.ThietBi.setText(objects.get(position).getdeviceName());
        holder.Noidung.setText(objects.get(position).getmessage());
        if( !holder.ThietBi.getText().toString().equals("Me")){
            holder.ThietBi.setBackgroundResource(R.drawable.drawable_name2);
        }else{
            holder.ThietBi.setBackgroundResource(R.drawable.drawable_name);
        }
        return view;
    }
}
