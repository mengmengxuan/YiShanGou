package com.yunlankeji.yishangou;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.dialog.LoadingDialog;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.hwangjr.rxbus.RxBus;
import com.personal.baseutils.utils.SPUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create by Snooker on 2020/7/17
 * Describe:Activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static long lastClickTime;
    private Unbinder mUnbinder;
    protected LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(setLayout());
        mUnbinder = ButterKnife.bind(this);
        RxBus.get().register(this);
        mLoadingDialog = new LoadingDialog(this);

        //全透明状态栏
        ConstantUtil.setStatusBarFullTransparent(this);
        //设置状态栏文字颜色及图标为深色
        getWindow().getDecorView().setSystemUiVisibility(View
                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        initData();
    }

    public abstract int setLayout();

    protected abstract void initView();

    @Override
    protected void onResume() {
        super.onResume();
        Data userinfo = (Data) SPUtils.get(this, "userInfo", null);
        if (userinfo != null) {
            Global.agreeStatus = userinfo.agreeStatus;
            Global.balanceAccount = userinfo.balanceAccount;
            Global.createDt = userinfo.createDt;
            Global.id = userinfo.id;
            Global.inviteCode = userinfo.inviteCode;
            Global.isMerchant = userinfo.isMerchant;
            Global.isRider = userinfo.isRider;
            Global.logo = userinfo.logo;
            Global.memberCode = userinfo.memberCode;
            Global.memberName = userinfo.memberName;
            Global.parentOneCode = userinfo.parentOneCode;
            Global.parentSecondCode = userinfo.parentSecondCode;
            Global.phone = userinfo.phone;
            Global.pwd = userinfo.pwd;
            Global.totalAccount = userinfo.totalAccount;
            Global.merchantCode = userinfo.merchantCode;
            Global.merchantName = userinfo.merchantName;
            Global.riderCode = userinfo.riderCode;
            Global.agreeStatus = userinfo.agreeStatus;
        }
    }

    public void initData() {

    }

    //解决两次点击太快，跳转两次界面的问题
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 300;
    }

    public void showLoading() {
        mLoadingDialog.showDialog();
    }

    public void hideLoading() {
        mLoadingDialog.hideDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        RxBus.get().unregister(this);
    }

    public void doActivity(Context context, Class tClass) {
        Intent intent = new Intent(context, tClass);
        startActivity(intent);
    }

    public void doActivity(Context context, Class<?> c, String content1, String tag1) {
        Intent intent = new Intent(context, c);
        intent.putExtra(tag1, content1);
        startActivity(intent);
    }

    public void doActivity(Context context, Class<?> c, String content1, String tag1, String content2, String tag2) {
        Intent intent = new Intent(context, c);
        intent.putExtra(tag1, content1);
        intent.putExtra(tag2, content2);
        startActivity(intent);
    }
}
