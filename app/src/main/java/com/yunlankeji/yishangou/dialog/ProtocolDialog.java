package com.yunlankeji.yishangou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.Util;
import com.zzhoujay.richtext.RichText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class ProtocolDialog extends Dialog {

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
    @BindView(R.id.m_protocol_cb)
    CheckBox mProtocolCb;

    Context context;
    private OnStatusListener onPositiveStatusListener;
    private OnStatusListener onNegativeStatusListener;

    private CharSequence titleStr, msgStr, negativeStr, positiveStr;
    int gravity = Gravity.CENTER;
    int background, textColor;
    private boolean bNo, bYes;

    public ProtocolDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    public String getEditText() {
        String str = message.getText().toString().trim();
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_protocol);
        ButterKnife.bind(this);
 /*       getWindow().setLayout(Utils.getScreenW(context) - Utils.dip2px(context, 80),
                ViewGroup.LayoutParams.WRAP_CONTENT);*/

        if (titleStr == null || TextUtils.isEmpty(titleStr.toString())) {
            titleLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) msgLayout.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.topMargin = Util.dpToPx(30, context);
            params.bottomMargin = Util.dpToPx(30, context);
            msgLayout.setLayoutParams(params);
        } else {
            mTitle.setText(titleStr);
        }

        if (msgStr == null || TextUtils.isEmpty(msgStr.toString())) {
            msgLayout.setVisibility(View.VISIBLE);
        } else {
            msgLayout.setGravity(gravity);

            if (msgStr.toString().contains("<p")) {
                RichText.fromHtml(msgStr.toString()).into(message);
            } else {
                message.setText(msgStr);
            }
        }

        if (negativeStr == null || TextUtils.isEmpty(negativeStr.toString())) {
            negative.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            negative.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            negative.setText(negativeStr);
        }

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNegativeStatusListener != null) {
                    onNegativeStatusListener.OnStatus(ProtocolDialog.this);
                }
                dismiss();
            }
        });

        positive.setTextColor(ContextCompat.getColor(context, textColor == 0 ? R.color.white : textColor));
        positive.setText(positiveStr == null ? "" : positiveStr);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPositiveStatusListener != null) {
                    onPositiveStatusListener.OnStatus(ProtocolDialog.this);
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

    public ProtocolDialog setCaption(CharSequence str) {
        titleStr = str;
        return this;
    }

    public ProtocolDialog setMessage(CharSequence str) {
        msgStr = str;
        return this;
    }

    public interface OnStatusListener {
        void OnStatus(Dialog dialog);
    }

   /* public void setOnStatusListener(OnStatusListener onStatusListener) {
        this.onStatusListener = onStatusListener;
    }*/

    /**
     * 确定按钮
     *
     * @param text
     * @param l
     * @return
     */
    public ProtocolDialog setPositiveButton(CharSequence text, OnStatusListener l) {
        bYes = true;
        positiveStr = text;
        onPositiveStatusListener = l;
        return this;
    }

    public ProtocolDialog setNegativeButton(CharSequence text, OnStatusListener l) {
        bNo = true;
        negativeStr = text;
        onNegativeStatusListener = l;
        return this;
    }

    public Boolean getCheck() {
        return mProtocolCb.isChecked();
    }
}
