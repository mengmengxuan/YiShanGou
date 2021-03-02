package com.yunlankeji.yishangou.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ToastUtils;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.order.OrderDetailActivity;
import com.yunlankeji.yishangou.adapter.OrderPagerAdapter;
import com.yunlankeji.yishangou.fragment.order.AllOrderFragment;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.ZLBusAction;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Create by Snooker on 2020/12/21
 * Describe:订单
 */
public class OrderFragment extends BaseFragment {

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;

    private static final String[] CHANNELS = new String[]{"全部订单", "进行中", "已完成"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(getActivity(), mRootCl);
        mBackIv.setVisibility(View.GONE);
        mTitleTv.setText("订单");

    }

    @Override
    public void onResume() {
        super.onResume();
        fragments.add(new AllOrderFragment("all"));//全部订单
        fragments.add(new AllOrderFragment("load"));//进行中
        fragments.add(new AllOrderFragment("complete"));//已完成

        initMagicIndicator();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Refresh_Order)})
    public void refreshOrder(String status) {
        if (status.equals("Refresh_Order")) {
            fragments.clear();
            fragments.add(new AllOrderFragment("all"));//全部订单
            fragments.add(new AllOrderFragment("load"));//进行中
            fragments.add(new AllOrderFragment("complete"));//已完成

            initMagicIndicator();
        }
    }

    private void initMagicIndicator() {
        OrderPagerAdapter orderPagerAdapter = new OrderPagerAdapter(getChildFragmentManager());
        orderPagerAdapter.setFragments(fragments);
        viewPager.setAdapter(orderPagerAdapter);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setScrollPivotX(0.25f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.color_333333));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.color_333333));
                simplePagerTitleView.setTextSize(14);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setYOffset(UIUtil.dip2px(context, 3));
                indicator.setColors(getResources().getColor(R.color.color_F36C17));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

}
