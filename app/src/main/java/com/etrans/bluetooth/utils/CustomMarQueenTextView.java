package com.etrans.bluetooth.utils;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 单元名称:CustomMarqueenTextView.java
 * Created by fuxiaolei on 2016/7/28.
 * 说明:
 * Last Change by fuxiaolei on 2016/7/28.
 */
public class CustomMarQueenTextView extends TextView {

    public CustomMarQueenTextView(Context context) {
        super(context);
        createView();
    }

    public CustomMarQueenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public CustomMarQueenTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createView();
    }

    private void createView() {
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
