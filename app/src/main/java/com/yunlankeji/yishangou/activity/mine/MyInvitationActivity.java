package com.yunlankeji.yishangou.activity.mine;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.MyInvitationPagerAdapter;
import com.yunlankeji.yishangou.fragment.OrderFragment;
import com.yunlankeji.yishangou.fragment.mine.FirstLineTeamFragment;
import com.yunlankeji.yishangou.fragment.mine.SecondLineTeamFragment;
import com.yunlankeji.yishangou.utils.ConstantUtil;

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
import butterknife.OnClick;

/**
 * Create by Snooker on 2020/12/25
 * Describe:我的邀请页面
 */
public class MyInvitationActivity extends BaseActivity {
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.part_line)
    View partLine;//partLine
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;//指示器
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private static final String[] CHANNELS = new String[]{"一线团队", "二线团队"};
    private List<String> mDataList = Arrays.asList(CHANNELS);

    ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_my_invitation;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_F36C17));
        mTitleTv.setText("我的邀请");
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mBackIv.setImageResource(R.mipmap.icon_arrow_white_left);
        partLine.setVisibility(View.GONE);

        FirstLineTeamFragment firstLineTeamFragment = new FirstLineTeamFragment();//一线团队
        SecondLineTeamFragment secondLineTeamFragment = new SecondLineTeamFragment();//一线团队

        fragments.add(firstLineTeamFragment);
        fragments.add(secondLineTeamFragment);

        initMagicIndicator();

    }

    private void initMagicIndicator() {
        MyInvitationPagerAdapter mMyInvitationPagerAdapter = new MyInvitationPagerAdapter(getSupportFragmentManager());
        mMyInvitationPagerAdapter.setFragments(fragments);
        viewPager.setAdapter(mMyInvitationPagerAdapter);
        magicIndicator.setBackgroundColor(getResources().getColor(R.color.color_F8F8F8));
        CommonNavigator commonNavigator = new CommonNavigator(this);
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

    @OnClick({R.id.m_back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
        }

    }
}
