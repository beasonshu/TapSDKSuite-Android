package com.tapsdk.suite;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tapsdk.suite.component.TapComponent;
import com.tapsdk.suite.internal.FloatingRootView;
import com.tapsdk.suite.internal.TouchBarView;
import com.tapsdk.suite.util.UIUtil;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

class FloatingWidget {

    public static final String TAG = "FloatingRootView";

    public final static int ORIENTATION_PORTRAIT = 1;
    public final static int ORIENTATION_LANDSCAPE = 2;

    private int screenOrientation = ORIENTATION_LANDSCAPE;

    private final static int STATE_INITIAL = 0;

    private final static int STATE_COLLAPSE = 1;

    private final static int STATE_EXPAND = 2;

    private int state = STATE_INITIAL;

    private static boolean showEntranceAnimation = true;

    static class Config {
        public Integer[] portraitOvalCenterPosition = new Integer[]{54, 190};
        public Integer[] landscapeOvalCenterPosition = new Integer[]{54, 162};
        public Integer[] ovalAxis = new Integer[]{84, 220};
    }

    private List<TapComponent> tapComponentList;

    private WeakReference<Context> contextWeakReference = null;

    private FrameLayout floatingRootBorderView;

    private FloatingRootView floatingRootView;

    private TouchBarView touchBarView;

    private FloatingWidget() {
    }

    private FloatingWidget(List<TapComponent> tapComponentList) {
        this.tapComponentList = tapComponentList;
    }

    public static class Builder {
        private List<TapComponent> tapComponentList = Collections.emptyList();

        public Builder() {
        }

        public Builder withComponentList(List<TapComponent> tapComponentList) {
            this.tapComponentList = tapComponentList;
            return this;
        }

        public FloatingWidget build() {
            return new FloatingWidget(tapComponentList);
        }
    }

    private void makeRootView(Context context, Point size) {
        Config config = new Config();
        Integer[] screenOvalCenterPositionDp;
        Integer[] screenOvalCenterPosition;
        int startY;

        if (ORIENTATION_PORTRAIT == screenOrientation) {
            screenOvalCenterPosition = config.portraitOvalCenterPosition;
            screenOvalCenterPositionDp = new Integer[]{
                    UIUtil.dp2px(context, config.portraitOvalCenterPosition[0])
                    , UIUtil.dp2px(context, config.portraitOvalCenterPosition[1])
            };
            startY = 63;
        } else {
            screenOvalCenterPosition = config.landscapeOvalCenterPosition;

            int centerY = UIUtil.dp2px(context, 375);
            if (context instanceof Activity) {
                int height = ((Activity) context).getWindow().getDecorView().getHeight();
                int width = ((Activity) context).getWindow().getDecorView().getWidth();
                if (size.x > 0 && size.y > 0) {
                    centerY = Math.min((int)(Math.min(width, height) / 2f - UIUtil.dp2px(context,22)), UIUtil.dp2px(context, 375/2f - 22));
                }
            }

            screenOvalCenterPositionDp = new Integer[]{
                    UIUtil.dp2px(context, config.landscapeOvalCenterPosition[0])
                    , centerY
            };
            startY = 35;
        }

        Integer[] screenOvalAxis = new Integer[]{
                UIUtil.dp2px(context, config.ovalAxis[0])
                , UIUtil.dp2px(context, config.ovalAxis[1])
        };

        floatingRootView = FloatingRootView.createDefault(context, screenOvalCenterPositionDp, screenOvalAxis, screenOvalCenterPosition, startY);
        floatingRootBorderView = new FrameLayout(context);
        floatingRootBorderView.setBackgroundColor(Color.parseColor("#99000000"));
        floatingRootBorderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingRootView.showComponents(false);
            }
        });
    }

    public void attach(final Context context) {
        if (context == null) return;
        contextWeakReference = new WeakReference<>(context);
        this.screenOrientation = context.getResources().getConfiguration().orientation;
        showTouchBar(context, showEntranceAnimation);
        showEntranceAnimation = false;
    }

    private void showTouchBar(final Context context, boolean showEntranceAnimation) {
        if (state == STATE_COLLAPSE) return;
        state = STATE_COLLAPSE;

        touchBarView = TouchBarView.createDefault(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int width;
        if (screenOrientation == ORIENTATION_LANDSCAPE) {
            width = UIUtil.dp2px(context, 126);
        } else {
            width = UIUtil.dp2px(context, 112);
        }

        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            flags |= WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                width
                , WindowManager.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.TYPE_APPLICATION
                , flags
                , PixelFormat.TRANSLUCENT
        );

        if (screenOrientation == ORIENTATION_LANDSCAPE) {
            lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
            lp.verticalMargin = 0.054f;
        } else {
            lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
            lp.verticalMargin = 0.082f;
        }
        wm.addView(touchBarView, lp);
        touchBarView.attach(showEntranceAnimation);

        touchBarView.setComponentsExpandListener(new TouchBarView.ComponentsExpandListener() {
            @Override
            public void onExpand() {
                hideTouchBar(context);
                showComponents(context);
            }
        });
    }

    private void showComponents(final Context context) {
        if (state == STATE_EXPAND) return;
        if (floatingRootBorderView != null && floatingRootBorderView.getParent() != null) return;
        state = STATE_EXPAND;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        makeRootView(context , size);

        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            flags |= WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT
                , WindowManager.LayoutParams.MATCH_PARENT
                , WindowManager.LayoutParams.TYPE_APPLICATION
                , flags
                , PixelFormat.TRANSLUCENT
        );
        wm.addView(floatingRootBorderView, lp);
        FrameLayout.LayoutParams clp;
        if (FloatingWidget.this.screenOrientation == ORIENTATION_PORTRAIT) {
            clp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT
                    , UIUtil.dp2px(context, 430)
            );
            clp.gravity = Gravity.BOTTOM;
            clp.setMargins(UIUtil.dp2px(context, -10),0,0,UIUtil.dp2px(context, 78));
        } else {
            int targetHeight = UIUtil.dp2px(context, 375);
            if (context instanceof Activity) {
                int height = ((Activity) context).getWindow().getDecorView().getHeight();
                int width = ((Activity) context).getWindow().getDecorView().getWidth();
                if (size.x > 0 && size.y > 0) {
                    targetHeight = Math.min(Math.min(width, height), UIUtil.dp2px(context, 375));
                }
            }
            clp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT
                    , targetHeight
            );
            clp.setMargins(UIUtil.dp2px(context, 2), 0 , 0 , 0 );
            clp.gravity = Gravity.CENTER;
        }
        floatingRootBorderView.addView(floatingRootView, clp);
        floatingRootView.attach(tapComponentList, this.screenOrientation);
        floatingRootView.setComponentsCollapseListener(new FloatingRootView.ComponentsCollapseListener() {
            @Override
            public void onCollapse() {
                hideComponents(context);
                showTouchBar(context, false);
            }
        });
    }

    private void hideTouchBar(Context context) {
        // host is empty so ignore detach
        if (context == null || touchBarView == null) return;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        try {
            wm.removeView(touchBarView);
        } catch (IllegalArgumentException e) {
            System.out.println(e.toString());
        }
        touchBarView.onDetach();
    }

    private void hideComponents(Context context) {
        if (context == null || floatingRootView == null) return;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        try {
            wm.removeView(floatingRootBorderView);
        } catch (IllegalArgumentException e) {
            System.out.println(e.toString());
        }
        floatingRootView.onDetach();
    }

    public void detach() {
        Context context = null;
        if (contextWeakReference != null) {
            context = contextWeakReference.get();
        }
        hideComponents(context);
        hideTouchBar(context);
        contextWeakReference.clear();
        touchBarView = null;
        floatingRootView = null;
        floatingRootBorderView = null;
    }
}
