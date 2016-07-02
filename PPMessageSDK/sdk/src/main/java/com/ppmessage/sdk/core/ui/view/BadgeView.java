package com.ppmessage.sdk.core.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.ppmessage.sdk.R;

/**
 * Created by ppmessage on 6/8/16.
 */
public class BadgeView extends TextView {

    private int number;

    public BadgeView(Context context) {
        super(context);
        init();
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.pp_circle_badge_background);
        setTextColor(getResources().getColor(android.R.color.white));
        setGravity(Gravity.CENTER);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        setText(Math.min(this.number, 99) + "");
    }
}
