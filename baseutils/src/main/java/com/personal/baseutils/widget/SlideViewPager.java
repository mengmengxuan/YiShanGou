package com.personal.baseutils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import com.personal.baseutils.utils.Utils;

/**
 * Time: 2019/1/29 0029
 * Author:LiYong
 * Description:
 */
public class SlideViewPager extends ViewPager {

    Context context;
    int index = 0;

    public void setIndex(int index) {
        this.index = index;
    }

    public SlideViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public SlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getY() < Utils.dip2px(context, 120) && index == 0) {
            return false; // 拦截事件，禁止滑动
        }else {
            return super.onInterceptTouchEvent(ev);
        }

    }

}
