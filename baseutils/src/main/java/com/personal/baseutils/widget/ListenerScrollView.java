package com.personal.baseutils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by admin on 2016/6/16.
 */
public class ListenerScrollView extends ScrollView {

    ScrollBottomListener listener;
    onScrollChanged changeListener;

    public ListenerScrollView(Context context) {
        super(context);
    }

    public ListenerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnBottomListener(ScrollBottomListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (getScrollY() + getHeight() >= getChildAt(0).getMeasuredHeight()) {
            if (listener != null) {
                listener.onBottom();
            }
        }

        if (changeListener != null) {
            changeListener.onChanged(l, t, oldl, oldt);
        }
    }


    public void setOnChangeListener(onScrollChanged changeListener) {
        this.changeListener = changeListener;
    }

    public interface ScrollBottomListener {
        void onBottom();
    }

    public interface onScrollChanged {
        void onChanged(int l, int t, int oldl, int oldt);
    }


}
