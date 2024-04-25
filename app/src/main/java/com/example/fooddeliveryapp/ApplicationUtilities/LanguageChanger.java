package com.example.fooddeliveryapp.ApplicationUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageChanger {
    public AppCompatActivity context = null;
    public SharedPreferences preferences = null;

    public String currentLanguage = null;

    public LanguageChanger(AppCompatActivity context)
    {
        this.context = context;
        langWatcher(); //it will automattically load the saved language preference for app
    }

    public void langWatcher()
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        //fetching the language string
        currentLanguage = preferences.getString("language","");

        if(!currentLanguage.isEmpty()) //if the language variable is stored in preferences
        {
            languageChanger(currentLanguage);
        }

        System.out.println(currentLanguage);
    }

    public void languageChanger(String languageCompleteCode)
    {
        Locale locale = null;
        String code = "";
        String country = "";

        //Separating region, code and country from complete language code
        if( languageCompleteCode.contains("_") ) //means the country code is attached
        {
            country = languageCompleteCode.substring(languageCompleteCode.indexOf("_") + 1, languageCompleteCode.length());
            code = languageCompleteCode.substring(0, languageCompleteCode.indexOf("_"));
        }
        else
        {
            code  = languageCompleteCode;
        }

        //means
        if( country.length() > 0 )
            locale = new Locale(code, country);
        else
            locale = new Locale(code);

        //fetching app resources
        Resources resources = context.getResources();
        //fetching configurations setting loaded
        Configuration configuration = resources.getConfiguration();

        //adding new configuration locale values to existing configuration referenced variable "configuration"
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale); //urdu supports RTL right-to-left and english vice-versa

        //updating current resources configuration
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());



        //Using SharedPreferences to save the current configuration for future app sessions or openings in default app configs
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        //grabbing the editor
        SharedPreferences.Editor editor = preferences.edit();
        //edited the config string
        editor.putString("language", languageCompleteCode);

        //applying the changes or saving them
        editor.apply();


    }

    public void restartActivity()
    {
        //Now finally restart the activity to reflect the changes in whole app
        context.finish(); //finishing the current activity

        context.startActivity(context.getIntent()); //starting the current activity again
    }
}
