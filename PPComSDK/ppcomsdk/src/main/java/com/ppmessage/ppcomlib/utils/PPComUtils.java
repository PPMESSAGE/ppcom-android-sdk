package com.ppmessage.ppcomlib.utils;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.ppmessage.ppcomlib.PPComSDK;

/**
 * Created by ppmessage on 7/29/16.
 */
public final class PPComUtils {

    private PPComUtils() {
    }

    public static void setActivityActionBarStyle(AppCompatActivity activity) {
        if (PPComSDK.getInstance() == null || PPComSDK.getInstance().getConfiguration() == null) return;

        setActivityActionBarStyle(activity,
                PPComSDK.getInstance().getConfiguration().getActionbarBackgroundColor(),
                PPComSDK.getInstance().getConfiguration().getActionbarTitleColor());
    }

    public static void setActivityActionBarStyle(AppCompatActivity activity, @ColorInt int actionBarBackgroundColor, @ColorInt int actionBarTitleColor) {
        if (activity == null || activity.getSupportActionBar() == null) return;

        final ActionBar actionBar = activity.getSupportActionBar();
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(actionBarTitleColor), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarBackgroundColor));
    }

}
