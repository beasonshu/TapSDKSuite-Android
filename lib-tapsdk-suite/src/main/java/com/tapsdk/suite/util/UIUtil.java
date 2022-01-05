package com.tapsdk.suite.util;

import android.content.Context;

public class UIUtil {

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale);
    }

}
