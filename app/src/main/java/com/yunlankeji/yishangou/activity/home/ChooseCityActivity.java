package com.yunlankeji.yishangou.activity.home;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.zaaach.citypicker.adapter.CityListAdapter;
import com.zaaach.citypicker.adapter.InnerListener;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.adapter.decoration.DividerItemDecoration;
import com.zaaach.citypicker.adapter.decoration.SectionItemDecoration;
import com.zaaach.citypicker.db.DBManager;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;
import com.zaaach.citypicker.util.ScreenUtil;
import com.zaaach.citypicker.view.SideIndexBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by Snooker on 2021/1/4
 * Describe:城市选择页面
 */
public class ChooseCityActivity extends BaseActivity implements InnerListener, SideIndexBar.OnIndexTouchedChangedListener {

    @BindView(R.id.m_root_ll)
    LinearLayout mRootLl;
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_search_et)
    EditText mSearchEt;
    @BindView(R.id.cp_city_recyclerview)
    RecyclerView cpCityRecyclerview;
    @BindView(R.id.cp_side_index_bar)
    SideIndexBar cpSideIndexBar;
    @BindView(R.id.cp_overlay)
    TextView cpOverlay;
    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<City> mResults;
    private List<HotCity> mHotCities;
    private int locateState;
    private LocatedCity mLocatedCity;
    private DBManager dbManager;
    private OnPickListener mOnPickListener;

    @Override
    public int setLayout() {
        return R.layout.activity_choose_city;
    }

    @Override
    protected void initView() {

        ConstantUtil.setStatusBar(this, mRootLl);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cpCityRecyclerview.setLayoutManager(mLayoutManager);
        cpCityRecyclerview.setHasFixedSize(true);
        cpCityRecyclerview.addItemDecoration(new SectionItemDecoration(this, mAllCities), 0);
        cpCityRecyclerview.addItemDecoration(new DividerItemDecoration(this), 1);
        mAdapter = new CityListAdapter(this, mAllCities, mHotCities, locateState);
        mAdapter.autoLocate(true);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        cpCityRecyclerview.setAdapter(mAdapter);
        cpCityRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        cpSideIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(this));
        cpSideIndexBar.setOverlayTextView(cpOverlay)
                .setOnIndexChangedListener(this);

    }

    @Override
    public void initData() {
        //初始化热门城市
        if (mHotCities == null || mHotCities.isEmpty()) {
            mHotCities = new ArrayList<>();
            mHotCities.add(new HotCity("北京", "北京", "101010100"));
            mHotCities.add(new HotCity("上海", "上海", "101020100"));
            mHotCities.add(new HotCity("广州", "广东", "101280101"));
            mHotCities.add(new HotCity("深圳", "广东", "101280601"));
            mHotCities.add(new HotCity("天津", "天津", "101030100"));
            mHotCities.add(new HotCity("杭州", "浙江", "101210101"));
            mHotCities.add(new HotCity("南京", "江苏", "101190101"));
            mHotCities.add(new HotCity("成都", "四川", "101270101"));
            mHotCities.add(new HotCity("武汉", "湖北", "101200101"));
        }
        //初始化定位城市，默认为空时会自动回调定位
        if (mLocatedCity == null) {
            mLocatedCity = new LocatedCity(getString(com.zaaach.citypicker.R.string.cp_locating), "未知", "0");
            locateState = LocateState.LOCATING;
        } else {
            locateState = LocateState.SUCCESS;
        }

        dbManager = new DBManager(this);
        mAllCities = dbManager.getAllCities();
        mAllCities.add(0, mLocatedCity);
        mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
        mResults = mAllCities;
    }

    @Override
    public void dismiss(int position, City data) {
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
    }

    @Override
    public void locate() {
        if (mOnPickListener != null) {
            mOnPickListener.onLocate();
        }
    }

    public void setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
    }

    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
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
