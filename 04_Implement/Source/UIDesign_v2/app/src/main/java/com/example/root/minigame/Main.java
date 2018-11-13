package com.example.root.minigame;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.minigame.Activities.StartingMenu;

import static android.view.Window.FEATURE_NO_TITLE;


public class Main extends AppCompatActivity {

    Button btn_ok, btn_return, btn_setting;
    Player thisPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisPlayer = new Player();

        final Dialog dialog_setName = new Dialog(Main.this);
        dialog_setName.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_setName.setContentView(R.layout.dialog_set_nickname);
        dialog_setName.show();

        Button btn_confirm = (Button) dialog_setName.findViewById(R.id.btn_confirm);
        final EditText edt_playerName = (EditText) dialog_setName.findViewById(R.id.edt_playerName);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = edt_playerName.getText().toString();
                if (!playerName.equals(""))
                {
                    thisPlayer.setPlayerName(playerName);
                    dialog_setName.dismiss();
                    Toast.makeText(Main.this, "Đặt tên nhân vật thành công!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Main.this, "Tên không hơp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        btn_ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (thisPlayer.getPlayerName().equals(""))
                {
                    Toast.makeText(Main.this, "Vui lòng đặt tên nhân vật!", Toast.LENGTH_SHORT).show();
                    dialog_setName.show();
                }
                else {
                    Intent intent = new Intent(Main.this, StartingMenu.class);
                    Bundle bun = new Bundle();
                    bun.putString("thisPlayerName", thisPlayer.getPlayerName());
                    intent.putExtra("bundle", bun);
                    startActivity(intent);
                }
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        btn_return.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               finishAndRemoveTask();
            }
        });


        // Set up the user interaction to manually show or hide the system UI.

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    public static class CustomListApdater_Chat {

    }
}

