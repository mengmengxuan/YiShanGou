package com.yunlankeji.yishangou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.yunlankeji.yishangou.R;
import com.personal.baseutils.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmDialog extends Dialog {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.msgLayout)
    RelativeLayout msgLayout;
    @BindView(R.id.negative)
    TextView negative;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.positive)
    TextView positive;

    Context context;
    private OnStatusListener onStatusListener;

    private CharSequence titleStr, msgStr, negativeStr, positiveStr;
    int gravity = Gravity.CENTER;
    int positiveButtonBackground, positiveButtonTextColor;
    int negativeButtonBackground, negativeButtonTextColor;
    private boolean bNo, bYes;

    public ConfirmDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        ButterKnife.bind(this);
 /*       getWindow().setLayout(Utils.getScreenW(context) - Utils.dip2px(context, 80),
                ViewGroup.LayoutParams.WRAP_CONTENT);*/

        if (titleStr == null || TextUtils.isEmpty(titleStr.toString())) {
            titleLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) msgLayout.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.topMargin = Utils.dip2px(context, 30);
            params.bottomMargin = Utils.dip2px(context, 30);
            msgLayout.setLayoutParams(params);
        } else {
            mTitle.setText(titleStr);
        }

        if (msgStr == null || TextUtils.isEmpty(msgStr.toString())) {
            msgLayout.setVisibility(View.GONE);
        } else {
            msgLayout.setGravity(gravity);
            message.setText(msgStr);
        }

        if (negativeStr == null || TextUtils.isEmpty(negativeStr.toString())) {
            negative.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            negative.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            negative.setText(negativeStr);
        }

        negative.setBackgroundResource(negativeButtonBackground == 0 ? 0 : negativeButtonBackground);
        negative.setTextColor(ContextCompat.getColor(context, negativeButtonTextColor == 0 ? R.color.red : negativeButtonTextColor));
        negative.setText(negativeStr == null ? "" : negativeStr);
        negative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        positive.setBackgroundResource(positiveButtonBackground == 0 ? 0 : positiveButtonBackground);
        positive.setTextColor(ContextCompat.getColor(context, positiveButtonTextColor == 0 ? R.color.red : positiveButtonTextColor));
        positive.setText(positiveStr == null ? "" : positiveStr);
        positive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onStatusListener != null) {
                    onStatusListener.OnStatus(ConfirmDialog.this);
                }
            }
        });

        if (!bYes) {
            positive.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        if (!bNo) {
            negative.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }

    }

    public ConfirmDialog setCaption(CharSequence str) {
        titleStr = str;
        return this;
    }

    public ConfirmDialog setMessage(CharSequence str) {
        msgStr = str;
        return this;
    }

    public interface OnStatusListener {
        void OnStatus(Dialog dialog);
    }

    public void setOnStatusListener(OnStatusListener onStatusListener) {
        this.onStatusListener = onStatusListener;
    }

    /**
     * 确定按钮
     *
     * @param text
     * @param back_color
     * @param l
     * @return
     */
    public ConfirmDialog setPositiveButton(CharSequence text, int back_color, int text_color, OnStatusListener l) {
        bYes = true;
        positiveStr = text;
        positiveButtonBackground = back_color;
        positiveButtonTextColor = text_color;
        onStatusListener = l;
        return this;
    }

    public ConfirmDialog setNegativeButton(CharSequence text, int back_color, int text_color, OnStatusListener l) {
        bNo = true;
        negativeStr = text;
        negativeButtonBackground = back_color;
        negativeButtonTextColor = text_color;
        onStatusListener = l;
        return this;
    }
}
