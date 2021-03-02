package com.yunlankeji.yishangou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yunlankeji.yishangou.R;

public class GeneralDialog extends Dialog {

    public GeneralDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private GeneralDialog dialog;

        private String titleText;
        private String contentText;
        private String positiveBtnText;
        private String negativeBtnText;

        OnButtonClickListener positiveBtnOnClickListener;
        OnButtonClickListener negativeBtnOnClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setPositiveButton(String text, OnButtonClickListener listener) {
            positiveBtnText = text;
            positiveBtnOnClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text, OnButtonClickListener listener) {
            negativeBtnText = text;
            negativeBtnOnClickListener = listener;
            return this;
        }

        public GeneralDialog build() {
            dialog = new GeneralDialog(context, R.style.CustomDialog);
            dialog.setContentView(R.layout.layout_general_dialog);
            TextView tvTitle = dialog.findViewById(R.id.tv_title);
            TextView tvContent = dialog.findViewById(R.id.tv_content);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            TextView tvConfirm = dialog.findViewById(R.id.tv_confirm);

            tvTitle.setText(titleText);
            tvContent.setText(contentText);
            tvCancel.setText(negativeBtnText);
            tvConfirm.setText(positiveBtnText);

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    negativeBtnOnClickListener.onClick(dialog);
                }
            });
            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positiveBtnOnClickListener.onClick(dialog);
                }
            });

            return dialog;
        }
    }

    public interface OnButtonClickListener {
        void onClick(Dialog dialog);
    }
}
