package com.example;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class KeyboardView extends Activity
{
    public static String PACKAGE_NAME;

    SharedPreferences settings;

    SoundManager soundManager;

    private Note lowest_note = new Note((byte)65);
    private Note highest_note = new Note((byte)102);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        soundManager = SoundManager.getInstance();
        soundManager.initSounds(getBaseContext());

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        MakeKeyboard(layout);
        setContentView(layout, rlp);

        //settings.getBoolean("perform_updates", false));
        //settings.getString("updates_interval", "-1"));
        //settings.getString("welcome_message", "NULL"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    private View.OnClickListener keyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //showToastMessage(String.format("You clicked %s", v.getId()));
            SoundManager.playSound(v.getId());
        }
    };

    private void showToastMessage(String msg) {

        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void MakeKeyboard(ViewGroup parent)
    {
        int keyWidth = 80;
        int whiteKeyHeight = 160;

        // start drawing from white note
        if (lowest_note.getId() > 0 && Note.isBlackKey(highest_note.getId()))
        {
            lowest_note = new Note((byte)(lowest_note.getId() - 1));
        }
        // end drawing with white note
        if (highest_note.getId() < (byte)127 && Note.isBlackKey(highest_note.getId()))
        {
            highest_note = new Note((byte)(highest_note.getId() + 1));
        }

        // white keys must be added first
        double whiteKeyOffset = 0;
        for (byte i = lowest_note.getId(); i <= highest_note.getId(); i++) {

            boolean isBlackKey = Note.isBlackKey(i);

            if( !isBlackKey ) {
                Button btn = new Button(this);
                btn.setId(i);
                btn.setBackgroundResource( isBlackKey ? R.drawable.black_key: R.drawable.white_key );
                btn.setHeight(isBlackKey ? whiteKeyHeight / 2 : whiteKeyHeight);
                btn.setWidth(keyWidth);
                RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                btnParams.leftMargin = (int)(keyWidth * whiteKeyOffset - (isBlackKey ? keyWidth / 2 : 0));
                btn.setLayoutParams(btnParams);
                btn.setOnClickListener(keyListener);
                parent.addView(btn);
                // load sound
                int id = this.getResources().getIdentifier("_" + Integer.toString(i), "raw", PACKAGE_NAME);
                soundManager.addSound(i, id);


                whiteKeyOffset++;
            }
        }
        // because of android z-order limitation do second pass for black keys.
        whiteKeyOffset = 0;
        for (byte i = lowest_note.getId(); i <= highest_note.getId(); i++) {
            boolean isBlackKey = Note.isBlackKey(i);
            if( isBlackKey ) {
                Button btn = new Button(this);
                btn.setId(i);
                btn.setBackgroundResource( isBlackKey ? R.drawable.black_key: R.drawable.white_key );
                btn.setHeight(isBlackKey ? whiteKeyHeight / 2 : whiteKeyHeight);
                btn.setWidth(keyWidth);
                RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                btnParams.leftMargin = (int)(keyWidth * whiteKeyOffset - (isBlackKey ? keyWidth / 2 : 0));
                btn.setLayoutParams(btnParams);
                btn.setOnClickListener(keyListener);
                parent.addView(btn);
                // load sound
                int id = this.getResources().getIdentifier("_" + Integer.toString(i), "raw", PACKAGE_NAME);
                soundManager.addSound(i, id);
            }
            else {
                whiteKeyOffset++;
            }
        }
    }
}

