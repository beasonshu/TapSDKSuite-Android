package com.tapsdk.suite.util;

import android.content.Context;

public class DrawableUtil {
    public static int getDrawable(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
