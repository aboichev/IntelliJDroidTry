package com.example;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class MyActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard);
    }
}