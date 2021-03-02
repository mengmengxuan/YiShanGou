package com.yunlankeji.yishangou.activity.rider;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.hwangjr.rxbus.RxBus;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.MainActivity;
import com.yunlankeji.yishangou.fragment.rider.RiderMineFragment;
import com.yunlankeji.yishangou.fragment.rider.RiderOrderCenterFragment;
import com.yunlankeji.yishangou.utils.ZLBusAction;
import com.yunlankeji.yishangou.view.NoSlideViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by Snooker on 2021/1/2
 * Describe:骑手主页面
 */
public class RiderOrderCenterActivity extends BaseActivity {

    ArrayList<Fragment> mFragments = new ArrayList<>();
    PagerAdapter pagerAdapter;
    @BindView(R.id.viewPager)
    NoSlideViewPager viewPager;

    @BindView(R.id.m_rider_order_center_iv)
    AppCompatImageView mRiderOrderCenterIv;
    @BindView(R.id.m_rider_mine_iv)
    AppCompatImageView mRiderMineIv;

    @BindView(R.id.m_rider_order_center_tv)
    TextView mRiderOrderCenterTv;
    @BindView(R.id.m_rider_mine_tv)
    TextView mRiderMineTv;

    @BindView(R.id.m_rider_order_center_ll)
    LinearLayout mRiderOrderCenterLl;
    @BindView(R.id.m_rider_mine_ll)
    LinearLayout mRiderMineLl;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_order_center;
    }

    @Override
    protected void initView() {

        mFragments.add(new RiderOrderCenterFragment());//骑手订单大厅
        mFragments.add(new RiderMineFragment());//骑手我的页面

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.setFragments(mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.setCurrentItem(0);

        //默认选中首页
        showView(0);

    }

    @OnClick({R.id.m_rider_order_center_ll, R.id.m_rider_mine_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_rider_order_center_ll://点击了订单大厅
                showView(0);
                break;
            case R.id.m_rider_mine_ll://点击了我的
                showView(1);
                break;
        }
    }

    public void showView(int i) {

        mRiderOrderCenterTv.setTextColor(this.getResources().getColor(i == 0 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mRiderOrderCenterIv.setImageResource(i == 0 ? R.mipmap.icon_rider_order_center_checked : R.mipmap.icon_rider_order_center_normal);

        mRiderMineTv.setTextColor(this.getResources().getColor(i == 1 ? R.color.color_F36C17 : R.color
                .text_third_999999));
        mRiderMineIv.setImageResource(i == 1 ? R.mipmap.icon_rider_mine_checked : R.mipmap.icon_rider_mine_normal);

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

}
