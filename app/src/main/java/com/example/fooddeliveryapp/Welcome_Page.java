package com.example.fooddeliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fooddeliveryapp.ApplicationUtilities.DialogsManager;
import com.example.fooddeliveryapp.ApplicationUtilities.LanguageChanger;
import com.example.fooddeliveryapp.ApplicationUtilities.LoaderActivator;
import com.example.fooddeliveryapp.Fragments.SignIn_Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class Welcome_Page extends AppCompatActivity {

    //loader
    LoaderActivator loader;

    //Firebase auth
    FirebaseAuth auth ;

    //storing references
    MaterialAlertDialogBuilder languageSelector= null;

    //alert dialogs inputs and outputs
    String[] supportedLanguages = null;
    String selectedLanguage = "";
    int current_language;

    LanguageChanger languageChanger = null;

    //listener to open the popup
    View.OnClickListener popupActivator = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            supportedLanguages = new String[]{getString(R.string.langEnglish).toString(),getString(R.string.langUrdu).toString()};
            current_language = languageChanger.currentLanguage.equals("en_US") ? 0 : 1;

            new MaterialAlertDialogBuilder(Welcome_Page.this, com.google.android.material.R.layout.select_dialog_singlechoice_material)
                    .setTitle(getString(R.string.selectLanguage))
                    .setSingleChoiceItems(supportedLanguages, current_language, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedLanguage = supportedLanguages[i] == supportedLanguages[current_language] ? "" : supportedLanguages[i];
                        }
                    })
                    .setPositiveButton(R.string.dialogSelect, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!selectedLanguage.isEmpty()) {
                                String completeLanguageCode = selectedLanguage == getString(R.string.langEnglish).toString() ? "en_US" : "ur";
                                languageChanger.languageChanger(completeLanguageCode);
                                languageChanger.restartActivity();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .show();
        }
    };


    //Sign In and Sign Up Buttons Listeners
    View.OnClickListener routeToAccountPages = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent switch_now = new Intent(Welcome_Page.this, Authentication_Page.class);

            switch (view.getId())
            {
                case R.id.routeToSignIn:
                    switch_now.putExtra("intention", "Sign In");
                    break;
                case R.id.routeToGoogleSignIn:
                    switch_now.putExtra("intention", "Google Sign In");
                    break;
                case R.id.routeToSignUp:
                    switch_now.putExtra("intention", "Sign Up");
                    break;
            }
            startActivity(switch_now);
        }
    };

    //Router Class for intent handling


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //pre creation
        auth = FirebaseAuth.getInstance();


//        //already someone logged in

        if( auth.getCurrentUser() != null )
        {
//            auth.getCurrentUser().delete();
            Intent go_to_dashboard  = new Intent(Welcome_Page.this, App_Dashboard.class);
            startActivity(go_to_dashboard);
            finish();
        }

        //throwing the context
        languageChanger = new LanguageChanger(this);

        //post creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        //fetching references

        //setting button listener
        findViewById(R.id.langChange).setOnClickListener(popupActivator);
        findViewById(R.id.routeToSignIn).setOnClickListener(routeToAccountPages);
        findViewById(R.id.routeToSignUp).setOnClickListener(routeToAccountPages);
        findViewById(R.id.routeToGoogleSignIn).setOnClickListener(routeToAccountPages);

    }



}