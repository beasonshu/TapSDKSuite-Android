package com.tapsdk.suite;

import android.annotation.SuppressLint;
import android.app.Activity;
import com.tapsdk.suite.component.TapComponent;
import com.tapsdk.suite.util.ActivityUtil;
import com.tapsdk.tapsdk_suite.BuildConfig;
import java.util.List;

public class TapSDKSuite {

    private static List<TapComponent> tapComponentList;

    private static volatile boolean showed = false;

    @SuppressLint("StaticFieldLeak")
    private static FloatingWidget widget = null;

    private static final Object lock = new Object();

    /**
     * need up
     * @param tapComponents
     */
    public static void configComponents(List<TapComponent> tapComponents) {
        synchronized (lock) {
            tapComponentList = tapComponents;
        }
    }

    public static void enable(Activity activity) {
        synchronized (lock) {
            if (showed || widget != null) return;
            if (!checkParamsValid()) return;
            if (ActivityUtil.isActivityNotAlive(activity)) return;
            showed = true;
            widget = new FloatingWidget.Builder()
                    .withComponentList(tapComponentList)
                    .build();
            widget.attach(activity);
        }
    }

    public static void disable() {
        synchronized (lock) {
            if (widget != null) widget.detach();
            widget = null;
            showed = false;
        }
    }

    private static boolean checkParamsValid() {
        if (tapComponentList == null || tapComponentList.size() == 0) {
            if (BuildConfig.DEBUG) {
                throw new IllegalArgumentException("componentList can't be empty");
            }
            return false;
        }
        return true;
    }

    public static boolean isShowing() {
        return showed;
    }
}
