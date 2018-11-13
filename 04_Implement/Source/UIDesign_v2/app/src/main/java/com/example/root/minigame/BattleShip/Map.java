package com.example.root.minigame.BattleShip;//package com.example.root.minigame;
//
//import android.content.Context;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.Toast;
//
//public class Map {
//    static private int MAX_ROW = 10;
//    static private int MAX_COL = 10;
//
//    public Map(TableLayout map , Context context){
//        for(int i = 0; i < MAX_ROW    ; i++){
//            TableRow row = new TableRow(map.getContext());
//            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//            for(int j = 0 ; j < MAX_COL ; j ++){
//
//                final Button mbutton = new Button( map.getContext());
//                mbutton.setLayoutParams(new TableRow.LayoutParams(70, 70));
//                mbutton.setId(i * MAX_ROW + j);
//                row.addView(mbutton);
//                mbutton.setBackground(null);
//                mbutton.setOnClickListener(new View.OnClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//                    @Override
//                    public void onClick(View v) {//sự kiện này nè
//                        Index_Click = mbutton.getId();
//                        if(Check_Position_ischecked(Index_Click)){
//                            AddShip(Index_Click);
//                            ischecked.add(mbutton.getId());
//                        }
//                    }
//                });
//                mbutton.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        //Toast.makeText(BattleShipPreActivity.this, "Ne", Toast.LENGTH_SHORT).show();
//                        RemoveShip(mbutton.getId());
//                        return true;
//                    }
//                });
//            }
//            map.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            //ko được nảy t thử rồi, do phải find viwe id đã mới set vào cái table bên activity kia
//        }
//    }
//
//    boolean  Check_Position_ischecked(int temp){
//        for(int i = 0; i < ischecked.size(); i++){
//            if(temp == ischecked.get(i)){
//                return false;
//            }
//
//        }
//        return true;
//    }
//}
