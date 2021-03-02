package com.yunlankeji.yishangou.activity.business;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.fragment.OrderFragment;
import com.yunlankeji.yishangou.fragment.business.BusinessOrderCenterFragment;
import com.yunlankeji.yishangou.fragment.business.MerchantCenterFragment;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.view.NoSlideViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BusinessOrderCenterActivity extends BaseActivity {

    @BindView(R.id.m_order_center_iv)
    AppCompatImageView mOrderCenterIv;
    @BindView(R.id.m_merchant_center_iv)
    AppCompatImageView mMerchantCenterIv;
    @BindView(R.id.m_order_center_tv)
    TextView mOrderCenterTv;
    @BindView(R.id.m_merchant_center_tv)
    TextView mMerchantCenterTv;
    @BindView(R.id.m_order_center_ll)
    LinearLayout mOrderCenterLl;
    @BindView(R.id.m_merchant_center_ll)
    LinearLayout mMerchantCenterLl;
    @BindView(R.id.viewPager)
    NoSlideViewPager viewPager;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private PagerAdapter pagerAdapter;

    @Override
    public int setLayout() {
        return R.layout.activity_business_center;
    }

    @Override
    protected void initView() {
        mFragments.add(new BusinessOrderCenterFragment());//订单中心
        mFragments.add(new MerchantCenterFragment());//店铺管理

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.setFragments(mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.setCurrentItem(0);

        //默认选中订单大厅
        showView(0);

    }

    @OnClick({R.id.m_order_center_ll, R.id.m_merchant_center_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_order_center_ll://点击了订单中心
                showView(0);
                break;
            case R.id.m_merchant_center_ll://点击了商铺管理
                showView(1);
                break;
        }
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

    public void showView(int i) {

        mOrderCenterTv.setTextColor(getResources().getColor(i == 0 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mOrderCenterIv.setImageResource(i == 0 ? R.mipmap.icon_order_center_checked : R.mipmap.icon_order_center);

        mMerchantCenterTv.setTextColor(getResources().getColor(i == 1 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mMerchantCenterIv.setImageResource(i == 1 ? R.mipmap.icon_merchant_center_checked : R.mipmap.icon_merchant_center);

        viewPager.setCurrentItem(i, false);

    }

}
