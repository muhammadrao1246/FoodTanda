package com.example.fooddeliveryapp.ApplicationUtilities;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Debugger {
    Context context;
    int duration;

    public Debugger(Context context, int duration) {
        this.context = context;
        this.duration = duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void show(String message)
    {
        Toast.makeText(context, message, duration).show();
    }
}
