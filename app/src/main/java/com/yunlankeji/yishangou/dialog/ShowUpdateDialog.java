package com.yunlankeji.yishangou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yunlankeji.yishangou.R;
import com.personal.baseutils.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Time: 2019/5/13 0013
 * Author:LiYong
 * Description:
 */
public class ShowUpdateDialog extends Dialog {

    @BindView(R.id.m_desc_tv)
    TextView mDescTv;
    @BindView(R.id.m_cancle_iv)
    ImageView mCancleIv;
    @BindView(R.id.m_tip_tv)
    TextView mTipTv;
    @BindView(R.id.m_upgrade_tv)
    TextView mUpgradeTv;

    Context context;

    String desc;
    boolean isForceUpdate = false;
    OnChooseUpdateListenner onChooseUpdateListenner;

    public interface OnChooseUpdateListenner {
        public void chooseUpdate();
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOnChooseUpdateListener(OnChooseUpdateListenner onChooseUpdateListenner) {
        this.onChooseUpdateListenner = onChooseUpdateListenner;
    }

    public void setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public ShowUpdateDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_update);
        getWindow().setLayout(Utils.getScreenW(context) - Utils.dip2px(context, 80),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);
        if (isForceUpdate) {
            mCancleIv.setVisibility(View.GONE);
        } else {
            mCancleIv.setVisibility(View.VISIBLE);
        }
        mDescTv.setText(desc);

        Log.e("########", "desc===" + desc);
    }

    @OnClick({R.id.m_cancle_iv, R.id.m_upgrade_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_cancle_iv:
                dismiss();
                break;
            case R.id.m_upgrade_tv:
                onChooseUpdateListenner.chooseUpdate();
                dismiss();
                break;
        }
    }

}
