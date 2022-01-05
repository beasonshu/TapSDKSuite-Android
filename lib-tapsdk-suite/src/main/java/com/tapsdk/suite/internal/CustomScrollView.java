package com.tapsdk.suite.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.tapsdk.suite.util.UIUtil;

public class CustomScrollView extends ScrollView {

    private final static int MAX_SCROLL_SPEED = 200;

    private final static int MAX_OUTER_TOUCH_AREA = 15;

    public boolean forbid = false;

    public void setForbid(boolean forbid) {
        this.forbid = forbid;
    }

    private int componentsOuterTouchArea;

    public interface CustomOnScrollChangedListener {
        void onScroll(View view, int originX, int originY, int x, int y);
    }

    private CustomOnScrollChangedListener listener;

    public void setCustomOnScrollChangedListener(CustomOnScrollChangedListener listener) {
        this.listener = listener;
    }

    public CustomScrollView(Context context) {
        super(context);
        init(context);

    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        componentsOuterTouchArea = UIUtil.dp2px(context, MAX_OUTER_TOUCH_AREA);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            listener.onScroll(this, l, t, oldl, oldt);
        }
    }

    @Override
    public void fling(int velocityY) {
        int topVelocityY = (int) ((Math.min(Math.abs(velocityY), MAX_SCROLL_SPEED) ) * Math.signum(velocityY));
        super.fling(topVelocityY);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (forbid) return false;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            ViewGroup root = (ViewGroup) getChildAt(0);
            if (root != null) {
                int[] rootLocation = new int[2];
                root.getLocationOnScreen(rootLocation);
                boolean hit = false;
                for (int i = 0 ; i < root.getChildCount() ; i++) {
                    View child = root.getChildAt(i);
                    int[] childLocation = new int[2];
                    child.getLocationOnScreen(childLocation);
                    int[] relativeLocation = new int[2];
                    relativeLocation[0] = childLocation[0] - rootLocation[0];
                    relativeLocation[1] = childLocation[1] - rootLocation[1];
                    Rect childBounds = new Rect(relativeLocation[0] - componentsOuterTouchArea
                            , relativeLocation[1] - componentsOuterTouchArea
                            , relativeLocation[0] + child.getWidth() + componentsOuterTouchArea
                            , relativeLocation[1] + child.getHeight() + componentsOuterTouchArea
                    );
                    if (ev.getX() >= childBounds.left && ev.getX() <= childBounds.right
                        && ev.getY() >= childBounds.top && ev.getY() <= childBounds.bottom) {
                        hit = true;
                        break;
                    }
                }

                return hit;
            }
        }
        return super.onTouchEvent(ev);
    }
}
