package com.yunlankeji.yishangou.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.yunlankeji.yishangou.R;

public class LoadingDialog extends AlertDialog {

    private TextView mTextView;
    private ImageView mImageView;
    private ProgressBar mProgressView;
    private boolean isShowText = true;//文字是否可见

    private String mText = "加载中...";

    Context context;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.Loading_Layout);
        this.context = context;
//        mLifecycleOwner = lifecycleOwner;
    }

    public LoadingDialog(@NonNull Context context, String text) {
        super(context, R.style.Loading_Layout);
        this.context = context;
        mText = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        Window window = getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_loading);

            mTextView = window.findViewById(R.id.txt_info);
            mImageView = window.findViewById(R.id.imageView);
            if (!isShowText) {
                mTextView.setVisibility(View.GONE);
            }
            mTextView.setText(mText);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.progressbar);
        anim.start();
        mImageView.startAnimation(anim);
    }

    public void showDialogWithText(String text) {
        if (mTextView != null) {
            mTextView.setText(text);
        }
        if (!isShowing()) {
            show();
        }
    }

    public void showDialog() {
        if (!isShowing()) {
            show();
        }
    }

    public void hideDialog() {
        if (isShowing()) {
            dismiss();
        }
    }

    public void setTextGone() {
        isShowText = false;
    }

//    public void showFinish() {
//        hideDialogDelay();
//        mImageView.setVisibility(View.VISIBLE);
//        mProgressView.setVisibility(View.GONE);
//        mImageView.setBackgroundResource(R.drawable.img_finish);
//        mTextView.setText("完成");
//    }
//
//    public void showFinish(String msg) {
//        hideDialogDelay();
//        mImageView.setVisibility(View.VISIBLE);
//        mProgressView.setVisibility(View.GONE);
//        mImageView.setBackgroundResource(R.drawable.img_finish);
//        mTextView.setText(msg);
//    }
//
//    public void showError(String msg) {
//        hideDialogDelay();
//        mImageView.setVisibility(View.VISIBLE);
//        mProgressView.setVisibility(View.GONE);
//        mImageView.setBackgroundResource(R.drawable.img_error);
//        mTextView.setText(msg);
//    }

    private void hideDialogDelay() {
        /*Observable.just(1)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<Integer>autoDisposable(AndroidLifecycleScopeProvider.from(mLifecycleOwner)))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        dismiss();
                    }
                });*/
    }

}

