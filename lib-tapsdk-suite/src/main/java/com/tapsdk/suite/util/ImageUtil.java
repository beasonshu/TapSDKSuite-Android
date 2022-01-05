package com.tapsdk.suite.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtil {

    public static Bitmap createFromResourceId(Context context, Integer resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }
}
