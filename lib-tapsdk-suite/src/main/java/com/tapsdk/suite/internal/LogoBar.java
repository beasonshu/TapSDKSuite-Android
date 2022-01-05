package com.tapsdk.suite.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tapsdk.suite.util.ImageUtil;
import com.tapsdk.suite.util.UIUtil;
import com.tapsdk.tapsdk_suite.BuildConfig;
import com.tapsdk.tapsdk_suite.R;

@SuppressLint("AppCompatCustomView")
public class LogoBar {

    public final static String TAG = "LogoBar";

    public interface OnLogoClickListener {
        void onClick();
    }

    private OnLogoClickListener onLogoClickListener;

    public void setOnLogoClickListener(OnLogoClickListener listener) {
        onLogoClickListener = listener;
    }

    static class BgBar extends ImageView {
        private Paint paint;
        private Bitmap logoBitmap;
        private Rect innerArcRect;
        private Rect logoSrcRect;
        private Rect logoDestRect;

        private float totalAngle;
        private int numOfComponents;

        public BgBar(Context context) {
            super(context);
            init(context);
        }

        public BgBar(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public void setParams(float totalAngle, int numOfComponents) {
            this.totalAngle = totalAngle;
            this.numOfComponents = numOfComponents;
        }

        private void init(Context context) {
            this.paint = new Paint();

            // need optimize
            paint.setColor(Color.WHITE);
            paint.setAlpha(63);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(UIUtil.dp2px(context, 1.5f));

            this.logoBitmap = ImageUtil.createFromResourceId(context, R.drawable.tapsdk_suite_ic_taptap);
            this.logoSrcRect = new Rect(0, 0, logoBitmap.getWidth(), logoBitmap.getHeight());
            this.logoDestRect = new Rect(UIUtil.dp2px(context, 17), UIUtil.dp2px(context, 33)
                    , UIUtil.dp2px(context, 63), UIUtil.dp2px(context, 47));
            this.innerArcRect = new Rect(UIUtil.dp2px(context, 1.5f), UIUtil.dp2px(context, 1.5f), UIUtil.dp2px(context, 78.5f), UIUtil.dp2px(context, 78.5f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(logoBitmap, logoSrcRect, logoDestRect, null);
            if (numOfComponents > 5) {
                canvas.drawArc(innerArcRect.left, innerArcRect.top, innerArcRect.right, innerArcRect.bottom
                        , -(totalAngle / 2), totalAngle, false, paint);
            }
        }
    }

    static class ScrollProgressBar extends ImageView {
        private Paint paint;
        private Rect innerArcRect;
        private float totalAngle;
        private float segmentAngle;
        private int numOfComponents;

        public void setParams(float totalAngle, int numOfComponents) {
            this.totalAngle = totalAngle;
            this.numOfComponents = numOfComponents;
            double ratio = (5d / numOfComponents);
            segmentAngle = (float) (totalAngle * ratio);
        }

        public ScrollProgressBar(Context context) {
            super(context);
            init(context);
        }

        public ScrollProgressBar(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        private void init(Context context) {
            this.paint = new Paint();
            // need optimize
            paint.setColor(Color.WHITE);
            paint.setAlpha(255);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(UIUtil.dp2px(context, 3f));
            this.innerArcRect = new Rect(UIUtil.dp2px(context, 1.5f), UIUtil.dp2px(context, 1.5f), UIUtil.dp2px(context, 78.5f), UIUtil.dp2px(context, 78.5f));
        }

        public float getAngleDiff() {
            return totalAngle - segmentAngle;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (numOfComponents > 5) {
                canvas.drawArc(innerArcRect.left, innerArcRect.top, innerArcRect.right, innerArcRect.bottom, -(totalAngle / 2), segmentAngle, false, paint);
            }
        }
    }

    protected Rect bounds;
    private BgBar bgBar;
    private ScrollProgressBar scrollProgressBar;

    public float getProgressBarAngle(int length) {
        return (float) (((float) length / 3.14) * ((float) 180 / 40));
    }

    public LogoBar(Context context, ViewGroup parent, FrameLayout.LayoutParams lp, int numOfComponents) {
        this.bounds = new Rect(lp.leftMargin, lp.topMargin, lp.leftMargin + UIUtil.dp2px(context, 80), lp.topMargin + UIUtil.dp2px(context, 80));
        if (parent == null) {
            if (BuildConfig.DEBUG) {
                throw new IllegalArgumentException(TAG + " parent is null");
            }
            return;
        }
        bgBar = new BgBar(context);

        float totalAngle = getProgressBarAngle(36);
        bgBar.setParams(totalAngle, numOfComponents);
        FrameLayout.LayoutParams targetLp = new FrameLayout.LayoutParams(getWidth(), getHeight());
        targetLp.setMargins(bounds.left, 0, bounds.right, 0);
        targetLp.gravity = lp.gravity;
        parent.addView(bgBar, targetLp);

        scrollProgressBar = new ScrollProgressBar(context);
        scrollProgressBar.setParams(totalAngle, numOfComponents);
        parent.addView(scrollProgressBar, targetLp);

        bgBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onLogoClickListener != null) onLogoClickListener.onClick();
            }
        });

        scrollProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onLogoClickListener != null) onLogoClickListener.onClick();
            }
        });
    }

    public void refreshProgress(int dy, int totalMove) {
        if (scrollProgressBar == null) return;
        float changeAngle = scrollProgressBar.getAngleDiff() / totalMove * dy;
        float newAngle = scrollProgressBar.getRotation() + changeAngle < 0 ? 0 : scrollProgressBar.getRotation() + changeAngle;
        scrollProgressBar.setRotation(newAngle);
    }

    public void playAnimation(Animation animation) {
        bgBar.startAnimation(animation);
        scrollProgressBar.startAnimation(animation);
    }

    public int getWidth() {
        return bounds.right - bounds.left;
    }

    public int getHeight() {
        return bounds.bottom - bounds.top;
    }

    public void setVisibility(int visible) {
        bgBar.setVisibility(visible);
        scrollProgressBar.setVisibility(visible);
    }
}
