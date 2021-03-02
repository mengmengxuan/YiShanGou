package com.yunlankeji.yishangou.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.personal.baseutils.widget.wheelview.OnWheelChangedListener;
import com.personal.baseutils.widget.wheelview.OnWheelScrollListener;
import com.personal.baseutils.widget.wheelview.WheelView;
import com.personal.baseutils.widget.wheelview.adapter.AbstractWheelTextAdapter1;
import com.yunlankeji.yishangou.R;

import java.util.ArrayList;

public class ChoiceDialog extends PopupWindow implements View.OnClickListener {

    Context context;
    private WheelView firstWv;
    private TextView mSureBtn, mCancelBtn, mTitleTv;
    String type;

    private int maxTextSize = 24;
    private int minTextSize = 14;

    private CalendarTextAdapter startAdapter;

    private ArrayList<String> items = new ArrayList<>();
    private OnChooseListener onChooseListener;
    private String title;
    private int selectPosition;

    public void setOnChooseListener(OnChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
    }

    public void setTitle(String title) {
        this.title = title;
        mTitleTv.setText(title);
    }

    public ChoiceDialog(final Context context) {
        super(context);
        this.context = context;
        View view = View.inflate(context, R.layout.dialog_wheel_chioce, null);
        firstWv = view.findViewById(R.id.first_wv);

        mSureBtn = view.findViewById(R.id.m_sure_tv);
        mCancelBtn = view.findViewById(R.id.m_cancel_tv);
        mTitleTv = view.findViewById(R.id.m_title_tv);

        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//		this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        mSureBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    public void initData(ArrayList<String> items) {
        if (items != null && items.size() > 0) {
            type = items.get(0);
            startAdapter = new CalendarTextAdapter(context, items, 0, maxTextSize, minTextSize);
        }
        firstWv.setVisibleItems(5);
        firstWv.setViewAdapter(startAdapter);
        firstWv.setCurrentItem(0);

        firstWv.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) startAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, startAdapter);
                type = currentText.substring(0, currentText.length()).toString();
                selectPosition = wheel.getCurrentItem();
            }
        });

        firstWv.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) startAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, startAdapter);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == mSureBtn) {
            onChooseListener.onClick(type, selectPosition);
        } else {
            dismiss();
        }
        dismiss();
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }

    public interface OnChooseListener {
        public void onClick(String type, int position);
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter1 {

        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }

    }

}
