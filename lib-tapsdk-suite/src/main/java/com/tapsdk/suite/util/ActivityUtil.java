package com.tapsdk.suite.util;

import android.app.Activity;

public class ActivityUtil {
    public static boolean isActivityNotAlive(Activity activity) {
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }

    public static boolean isActivityAlive(Activity activity) {
        return !isActivityNotAlive(activity);
    }
}
