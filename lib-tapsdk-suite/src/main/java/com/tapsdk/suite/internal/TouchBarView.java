package com.tapsdk.suite.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tapsdk.suite.util.UIUtil;
import com.tapsdk.tapsdk_suite.R;

public class TouchBarView extends FrameLayout {

    Animation rightInAnimation;

    private boolean ready = false;

    public TouchBarView(Context context) {
        super(context);
    }

    public TouchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static TouchBarView createDefault(Context context) {
        return (TouchBarView) LayoutInflater.from(context).inflate(R.layout.tapsdk_suite_view_floating_touch_bar, null, false);
    }

    public interface ComponentsExpandListener {
        void onExpand();
    }

    private ComponentsExpandListener componentsExpandListener;

    public void setComponentsExpandListener(ComponentsExpandListener listener) {
        this.componentsExpandListener = listener;
    }

    public void applyAnimation() {
        if (rightInAnimation == null) rightInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.tapsdk_suite_anim_right_in);
        final ImageView touchImageView = findViewById(R.id.touchImageView);
        final ImageView senseToolImageView = findViewById(R.id.senseToolImageView);
        senseToolImageView.startAnimation(rightInAnimation);
        rightInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                touchImageView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                touchImageView.setVisibility(View.VISIBLE);
                senseToolImageView.setVisibility(View.GONE);
                ready = true;
                registerListener();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void attach(boolean showEnterAnimation) {
        FrameLayout touchBar = findViewById(R.id.touchFrameLayout);
        if (showEnterAnimation) {
            applyAnimation();
        } else {
            ready = true;
            touchBar.findViewById(R.id.touchImageView).setVisibility(View.VISIBLE);
            touchBar.findViewById(R.id.senseToolImageView).setVisibility(View.GONE);
        }
        registerListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void registerListener() {
        FrameLayout touchBar = findViewById(R.id.touchFrameLayout);
        touchBar.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!ready) return false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (hitSlide(event.getX())) {
                        if (componentsExpandListener != null) componentsExpandListener.onExpand();
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
    }

    private boolean hitSlide(float x) {
        return x >= 0 && x <= UIUtil.dp2px(getContext(), 30);
    }

    public void onDetach() {

    }
}
