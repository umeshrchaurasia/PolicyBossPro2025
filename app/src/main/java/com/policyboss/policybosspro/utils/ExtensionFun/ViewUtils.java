package com.policyboss.policybosspro.utils.ExtensionFun;// Create a new file named ViewUtils.java
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public final class ViewUtils {
    private ViewUtils() {}

    public static void applyBottomSystemBarPadding(final View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    navBarInsets.bottom
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }
}

