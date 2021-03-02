package com.yunlankeji.yishangou;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.utils.SPUtils;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.dialog.LoadingDialog;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.hwangjr.rxbus.RxBus;
import com.yunlankeji.yishangou.utils.LogUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create by Snooker on 2020/8/20
 * Describe:
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    public View mRootView;

    private Unbinder unbinder;
    protected LoadingDialog mLoadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayoutId(), container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        RxBus.get().register(this);
        mLoadingDialog = new LoadingDialog(getActivity());

        initView();
        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        Data userinfo = (Data) SPUtils.get(getActivity(), "userInfo", null);
        if (userinfo != null) {

            LogUtil.d(TAG, "" + JSON.toJSONString(userinfo));
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

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * view与数据绑定
     */
    protected void initView() {
    }

    /**
     * Sets layout id.
     *
     * @return the layout id
     */
    protected abstract int setLayoutId();

    public void showLoading() {
        mLoadingDialog.showDialog();
    }

    public void hideLoading() {
        mLoadingDialog.hideDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        RxBus.get().unregister(this);
    }

    public void doActivity(Class tClass) {
        Intent intent = new Intent(getContext(), tClass);
        startActivity(intent);
    }

    public void doActivity(Class<?> c, String content1, String tag1) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra(tag1, content1);
        startActivity(intent);
    }

    public void doActivity(Class<?> c, String content1, String tag1, String content2, String tag2) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra(tag1, content1);
        intent.putExtra(tag2, content2);
        startActivity(intent);
    }

}
