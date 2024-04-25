package com.example.fooddeliveryapp.ApplicationUtilities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.fooddeliveryapp.R;


public class ImageProgressIndicator extends AppCompatImageView {

    private ObjectAnimator rotationAnimator;

    public ImageProgressIndicator(Context context) {
        super(context);
        init();
    }

    public ImageProgressIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Set the company image as the initial image drawable
        setImageResource(R.drawable.app_icon);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Start the rotation animation when the view is attached to the window
        startRotationAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Stop the rotation animation when the view is detached from the window
        stopRotationAnimation();
    }

    private void startRotationAnimation() {
        if (rotationAnimator == null) {
            rotationAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
            rotationAnimator.setInterpolator(new LinearInterpolator());
            rotationAnimator.setDuration(2000); // Adjust the duration as needed
            rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        }
        rotationAnimator.start();
    }

    private void stopRotationAnimation() {
        if (rotationAnimator != null && rotationAnimator.isRunning()) {
            rotationAnimator.cancel();
        }
    }
}
