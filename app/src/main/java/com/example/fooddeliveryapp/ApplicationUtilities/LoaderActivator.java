package com.example.fooddeliveryapp.ApplicationUtilities;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class LoaderActivator {
    public AppCompatActivity context;

    RelativeLayout loader;

    Animation anim;
    public LoaderActivator(AppCompatActivity context) {
        this.context = context;
        this.loader = context.findViewById(com.example.fooddeliveryapp.R.id.base_loader);
        reset();
    }

    public void reset()
    {
        loader.setVisibility(View.INVISIBLE);
    }

    public void show()
    {
        anim = AnimationUtils.loadAnimation(context, com.example.fooddeliveryapp.R.anim.fade_in);
        anim.setDuration(300);
        loader.setVisibility(View.VISIBLE);

        loader.startAnimation(anim);
    }

    public void hide()
    {
        anim = AnimationUtils.loadAnimation(context, com.example.fooddeliveryapp.R.anim.fade_out);

        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        loader.startAnimation(anim);

    }
}
