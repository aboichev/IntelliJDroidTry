package com.example;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;

public class KeyboardView extends Activity
{
    private Note lowest_note = new Note((byte)65);
    private Note highest_note = new Note((byte)102);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        MakeKeyboard(layout);
        setContentView(layout, rlp);
    }

    private void showToastMessage(String msg) {

        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private View.OnClickListener keyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showToastMessage(String.format("You clicked %s", v.getId()));
            // Use a new tread as this can take a while
            final Thread thread = new Thread(new Runnable() {
                public void run() {
                    genTone();
                    handler.post(new Runnable() {

                        public void run() {
                            playSound();
                        }
                    });
                }
            });
            thread.start();
        }
    };

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
            }
            else {
                whiteKeyOffset++;
            }
        }
    }
    private final int duration = 3; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 440; // hz

    private final byte generatedSnd[] = new byte[2 * numSamples];

    Handler handler = new Handler();

    void genTone(){

        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){

        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }

}

