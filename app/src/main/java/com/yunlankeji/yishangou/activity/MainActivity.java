package com.yunlankeji.yishangou.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.utils.SPUtils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.fragment.HomeFragment;
import com.yunlankeji.yishangou.fragment.MineFragment;
import com.yunlankeji.yishangou.fragment.OrderFragment;
import com.yunlankeji.yishangou.fragment.RunErrandsFragment;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.service.LocationService;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.UpdateAppManager;
import com.yunlankeji.yishangou.utils.ZLBusAction;
import com.yunlankeji.yishangou.view.NoSlideViewPager;
import com.hwangjr.rxbus.RxBus;
import com.personal.baseutils.utils.PermissionUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    // 1 将需要申请的多个权限放在数组中
    private static final String[] requestPermissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,};

    ArrayList<Fragment> mFragments = new ArrayList<>();
    PagerAdapter pagerAdapter;
    @BindView(R.id.viewPager)
    NoSlideViewPager viewPager;

    @BindView(R.id.m_home_iv)
    AppCompatImageView mHomeIv;
    @BindView(R.id.m_order_iv)
    AppCompatImageView mOrderIv;
    @BindView(R.id.m_run_errands_iv)
    AppCompatImageView mRunErrandsIv;
    @BindView(R.id.m_mine_iv)
    AppCompatImageView mMineIv;

    @BindView(R.id.m_home_tv)
    TextView mHomeTv;
    @BindView(R.id.m_order_tv)
    TextView mOrderTv;
    @BindView(R.id.m_run_errands_tv)
    TextView mRunErrandsTv;
    @BindView(R.id.m_mine_tv)
    TextView mMineTv;

    @BindView(R.id.m_home_ll)
    LinearLayout mHomeLl;
    @BindView(R.id.m_order_ll)
    LinearLayout mOrderLl;
    @BindView(R.id.m_run_errands_ll)
    LinearLayout mRunErrandsLl;
    @BindView(R.id.m_mine_ll)
    LinearLayout mMineLl;

    @BindView(R.id.bottom_ll)
    LinearLayout bottomLl;

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mFragments.add(new HomeFragment());//首页
        mFragments.add(new OrderFragment());//订单
        mFragments.add(new RunErrandsFragment());//跑腿
        mFragments.add(new MineFragment());//我的

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.setFragments(mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.setCurrentItem(0);

        //默认选中首页
        showView(0);

        initPermission();

        new UpdateAppManager(this, 1).getUpdateMsg(); // 检查更新

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
        }

        if ("1".equals(Global.isRider) || "1".equals(Global.isMerchant)) {
            if ("1".equals(Global.isRider)) {
                //如果是骑手，实时上传经纬度
                LogUtil.d(TAG, "是骑手");
                requestUpdateSystemVersion();
            }

            if ("1".equals(Global.isMerchant)) {
                //如果是商家，每隔30秒查一次订单
                LogUtil.d(TAG, "是商家");
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        } else {
            LogUtil.d(TAG, "不是骑手，也不是商家");
            Intent stopIntent = new Intent(this, LocationService.class);
            stopService(stopIntent);
        }

    }

    @OnClick({R.id.m_home_ll, R.id.m_order_ll, R.id.m_run_errands_ll, R.id.m_mine_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_home_ll://点击了首页
                showView(0);
                break;
            case R.id.m_order_ll://点击了订单
                showView(1);
                RxBus.get().post(ZLBusAction.Refresh_Order, "Refresh_Order");
                break;
            case R.id.m_run_errands_ll://点击了跑腿
                showView(2);
                break;
            case R.id.m_mine_ll://点击了我的
                showView(3);
                RxBus.get().post(ZLBusAction.Request_User_Info, "Request_User_Info");
                break;
        }
    }

    public void showView(int i) {

        mHomeTv.setTextColor(this.getResources().getColor(i == 0 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mHomeIv.setImageResource(i == 0 ? R.mipmap.icon_home_checked : R.mipmap.icon_home_normal);

        mOrderTv.setTextColor(this.getResources().getColor(i == 1 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mOrderIv.setImageResource(i == 1 ? R.mipmap.icon_order_checked : R.mipmap.icon_order_normal);

        mRunErrandsTv.setTextColor(this.getResources().getColor(i == 2 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mRunErrandsIv.setImageResource(i == 2 ? R.mipmap.icon_run_errands_checked :
                R.mipmap.icon_run_errands_normal);

        mMineTv.setTextColor(this.getResources().getColor(i == 3 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mMineIv.setImageResource(i == 3 ? R.mipmap.icon_mine_checked :
                R.mipmap.icon_mine_normal);

        viewPager.setCurrentItem(i, false);

    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragments = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setFragments(ArrayList<Fragment> mFragments) {
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    private void initPermission() {
        // 自定义申请多个权限
        PermissionUtils.checkMorePermissions(this, requestPermissions, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                //  已授予权限
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                // 上一次申请权限被拒绝，可用于向用户说明权限原因，然后调用权限申请方法。
                PermissionUtils.requestMorePermissions(MainActivity.this, requestPermissions,
                        REQUEST_CODE_PERMISSIONS);
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                // 第一次申请权限或被禁止申请权限，建议直接调用申请权限方法。
                PermissionUtils.requestMorePermissions(MainActivity.this, requestPermissions, REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    /**
     * 获取系统参数
     */
    private void requestUpdateSystemVersion() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = "201";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestUpdateSystemVersion(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "系统参数：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                PermissionUtils.onRequestMorePermissionsResult(this, requestPermissions, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {

                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        //有未授权的权限
                        initPermission();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        PermissionUtils.showToAppSettingDialog(MainActivity.this);
                    }
                });
        }
    }

    private long time = 0;

    @Override
    public void onBackPressed() {
        long temp = System.currentTimeMillis();
        if (time == 0 || temp - time > 1000 * 2) {
            time = temp;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
    }
}