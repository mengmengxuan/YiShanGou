package com.personal.baseutils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class ChildListView extends ListView {

    public ChildListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ChildListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ChildListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


    /*********************** 不要拦截父控件ListView的下拉刷新事件 start *************************/
    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    */

    /**
     * 为了让ChildListView的adapter中的控件可以触发点击事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }


    /**
     * 为了让外层的AutoListView可以下拉刷新
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
    /*********************** 不要拦截父控件ListView的下拉刷新事件  end *************************/

}
