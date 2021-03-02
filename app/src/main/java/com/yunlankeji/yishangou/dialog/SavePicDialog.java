package com.yunlankeji.yishangou.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yunlankeji.yishangou.R;

/**
 * Create by Snooker at 2020 2020/6/2 15:58
 * Describe:长按图片，从屏幕底部弹出来
 */
public class SavePicDialog extends PopupWindow implements View.OnClickListener {

    private Context context;

    private TextView mSaveTv, mCancelTv;
    public OnStatusChangedListener onStatusChangedListener;

    public SavePicDialog(final Context context) {
        super(context);
        this.context = context;

        View view = View.inflate(context, R.layout.dialog_save_picture, null);
        mSaveTv = view.findViewById(R.id.m_save_tv);
        mCancelTv = view.findViewById(R.id.m_cancel_tv);

        mSaveTv.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);

        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//		this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.color_transparent));
        //     设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    public interface OnStatusChangedListener {
        void onStatusChanged(View view);
    }

    public void setOnStatusChangedListener(OnStatusChangedListener onStatusChangedListener) {
        this.onStatusChangedListener = onStatusChangedListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.m_save_tv:
                onStatusChangedListener.onStatusChanged(view);
                dismiss();
                break;
            case R.id.m_cancel_tv:
                onStatusChangedListener.onStatusChanged(view);
                dismiss();
                break;
        }

    }
}
