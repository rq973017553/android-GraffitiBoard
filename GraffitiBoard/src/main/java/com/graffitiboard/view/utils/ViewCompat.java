package com.graffitiboard.view.utils;

import android.os.Build;
import android.view.View;

public class ViewCompat {
    /**
     * Returns true if {@code view} has been through at least one layout since it
     * was last attached to or detached from a window.
     *
     * See http://developer.android.com/reference/android/support/v4/view/ViewCompat.html#isLaidOut%28android.view.View%29
     *
     * @param view the view
     * @return true if this view has been through at least one layout since it was last attached to or detached from a window.
     */
    public static boolean isLaidOut(View view) {
        // Future (API19+)...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return view.isLaidOut();
        }
        // Legacy...
        return view.getWidth() > 0 && view.getHeight() > 0;
    }
}
