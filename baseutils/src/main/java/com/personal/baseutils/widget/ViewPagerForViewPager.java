package com.personal.baseutils.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.personal.baseutils.utils.Utils;

/**
 * @author chenzheng
 * @ClassName: ViewPagerForViewPager
 * @Description: 自定义ViewPager来实现被嵌套在ViewPager中
 * @date 2015-4-11 下午4:10:59
 */
public class ViewPagerForViewPager extends ViewPager {

    private int current;
    private int height = 0;
    Context context;

    public ViewPagerForViewPager(Context context) {
        super(context);
    }

    public ViewPagerForViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    PointF downPoint = new PointF();
    OnSingleTouchListener onSingleTouchListener;

    float olderX = 0, olderY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent evt) {

        if (isPagingEnabled) {
            switch (evt.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 记录按下时候的坐标
                    downPoint.x = evt.getX();
                    downPoint.y = evt.getY();
                    if (this.getChildCount() > 1) { // 有内容，多于1个时
                        // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (this.getChildCount() > 1) { // 有内容，多于1个时
                        // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (olderX == 0 && olderY == 0) {
                        olderX = evt.getX();
                        olderY = evt.getY();
                    } else {
                        final float deltaX = Math.abs(evt.getX() - olderX);
                        final float deltaY = Math.abs(evt.getY() - olderY);
                        // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                        // 左右滑动
                        if (deltaX > deltaY) {  //  不拦截事件，由子View进行处理
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        // 上下滑动
                        if (deltaX < deltaY) { //  拦截事件，由父View进行处理
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    // 在up时判断是否按下和松手的坐标为一个点
                    olderX = 0;
                    olderY = 0;
                    if (PointF.length(evt.getX() - downPoint.x, evt.getY()
                            - downPoint.y) < (float) 5.0) {
                        onSingleTouch(this);
                        return true;
                    }
                    break;
            }
            return super.onTouchEvent(evt);
        } else {
            return this.isPagingEnabled && super.onTouchEvent(evt);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //   /***  取所有view最大高度作为ViewPager的高度 ***/
//        int height = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            if (h > height)
//                height = h;
//        }
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("#####+++#####","onMeasure==");
        //   /***  取所有view最大高度作为ViewPager的高度 ***/
        if (getChildCount() > current) {
            View child = getChildAt(current);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            height = h;
            if(height == 0||height < 1760){
                height = 1760* Utils.getScreenH(context)/1920;
            }
         //  Math.max(height,1600);
   //         Log.e("##########","getScreenW=="+Utils.getScreenW(context)+"getScreenH=="+Utils.getScreenH(context)+"height333=="+ height);
        }
        //getScreenW==1080getScreenH==1920height333==1600
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void resetHeight(int current) {
        this.current = current;
        if (getChildCount() > current) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
               Log.e("#####+++#####","current=="+current+";height111=="+ layoutParams.height);
            } else {
                layoutParams.height = height;
               Log.e("#####+++#####","current=="+current+";height222=="+ layoutParams.height);
            }
            if(layoutParams.height == 0){
                height = 1760* Utils.getScreenH(context)/1920;
            }
         //   requestLayout();
          setLayoutParams(layoutParams);
        }
    }


    private boolean isPagingEnabled = true;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    public void onSingleTouch(View v) {
        if (onSingleTouchListener != null) {
            onSingleTouchListener.onSingleTouch(v);
        }
    }

    public interface OnSingleTouchListener {
        public void onSingleTouch(View v);
    }

    public void setOnSingleTouchListener(
            OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }
}