package com.example.fooddeliveryapp.ApplicationUtilities;

import android.content.DialogInterface;
import android.graphics.drawable.Icon;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.annotation.Nullable;

public class DialogsManager {

//    AppCompatActivity context;


    public static void basicAlert(AppCompatActivity context, String purpose, String message, String title, String buttonText, @Nullable DialogInterface.OnClickListener onClick)
    {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, com.google.android.material.R.layout.mtrl_picker_fullscreen);

        if(purpose == "success")
            builder.setIcon(R.drawable.success_icon);
        else if(purpose == "error")
            builder.setIcon(R.drawable.error_icon);


        builder.setPositiveButton(buttonText, onClick );
        builder.setMessage(message);
        builder.setCustomTitle(context.findViewById(com.google.android.material.R.id.title_template));
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.show();
    }

}
