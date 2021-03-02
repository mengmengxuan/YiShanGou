package com.yunlankeji.yishangou.activity.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.CategoryAdapter;
import com.yunlankeji.yishangou.adapter.HotSellerAdapter;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/26
 * Describe:更多分类页面
 */
public class MoreCategoryActivity extends BaseActivity {

    private static final String TAG = "MoreCategoryActivity";

    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_more_category_rv)
    RecyclerView mMoreCategoryRv;
    @BindView(R.id.m_hot_seller_rv)
    RecyclerView mHotSellerRv;
    @BindView(R.id.m_invite_register_iv)
    ImageView mInviteRegisterIv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private int currPage = 1;
    private CategoryAdapter mCategoryAdapter;
    private HotSellerAdapter mHotSellerAdapter;
    private List<Data> categoryItems = new ArrayList<>();//分类的数据源
    private List<Data> hotSellerItems = new ArrayList<>();//热销商家的数据源
    private String mCityName;

    @Override
    public int setLayout() {
        return R.layout.activity_more_category;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("更多分类");

        mCityName = getIntent().getStringExtra("cityName");

        //邀请好友注册的图片
        mInviteRegisterIv.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(MoreCategoryActivity.this).asBitmap().load(R.mipmap.icon_invite_register).into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mInviteRegisterIv.setImageBitmap(resource);
                    }
                });
            }
        });

        //分类列表
        mCategoryAdapter = new CategoryAdapter(this);
        mCategoryAdapter.setItems(categoryItems);
        mCategoryAdapter.setFrom("more_category");
        mMoreCategoryRv.setLayoutManager(new GridLayoutManager(this, 5));
        mMoreCategoryRv.setAdapter(mCategoryAdapter);

        //点击分类列表
        mCategoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                //点击跳转到店铺列表页面
                Intent intent = new Intent();
                intent.setClass(MoreCategoryActivity.this, CategorizeActivity.class);
                intent.putExtra("merchantTypeCode", categoryItems.get(position).merchantTypeCode);
                intent.putExtra("title", categoryItems.get(position).merchantTypeName);
                startActivity(intent);

//                doActivity(MoreCategoryActivity.this, CategorizeActivity.class, categoryItems.get(position).merchantTypeName, "title");
            }

            @Override
            public void onNormalItemClicked(View view, int position) {

            }

            @Override
            public void onLastItemClicked(View view, int position) {
            }
        });

        //热销商家列表
        mHotSellerAdapter = new HotSellerAdapter(this);
        mHotSellerAdapter.setItems(hotSellerItems);
        mHotSellerRv.setLayoutManager(new LinearLayoutManager(this));
        mHotSellerRv.setAdapter(mHotSellerAdapter);
        mHotSellerAdapter.setOnItemClickListener(new HotSellerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(MoreCategoryActivity.this, StoreDetailActivity.class);
                intent.putExtra("merchantCode", hotSellerItems.get(position).merchantCode);
                startActivity(intent);
            }
        });

    }

    @Override
    public void initData() {
        super.initData();
        //热销商家
        requestHotSeller();
        //分类
        requestHomeCategory();
    }

    @OnClick({R.id.m_back_iv, R.id.m_search_ll, R.id.m_invite_register_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_search_ll:
                doActivity(this, SearchActivity.class);
                break;
            case R.id.m_invite_register_iv://邀请好友注册
                Intent intent = new Intent();
                intent.setClass(this, ShareRegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 热销商家
     */
    private void requestHotSeller() {

        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.page = currPage + "";
        paramInfo.size = "10";
        paramInfo.latitude = latitude;
        paramInfo.longitude = longitude;
        paramInfo.orderByType = "saleCount";
        paramInfo.cityName = mCityName;

        LogUtil.d(TAG, "latitude --> " + latitude);
        LogUtil.d(TAG, "longitude --> " + longitude);
        LogUtil.d(TAG, "paramInfo.cityName --> " + paramInfo.cityName);
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestHotMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "热销商家：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;

                if (data != null) {
                    hotSellerItems.addAll(data.data);
                    mHotSellerAdapter.notifyDataSetChanged();

                    if (data.currPage >= data.pageCount) {
                        //不能加载更多了
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    } else {
                        //加载更多
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }
                    }
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                    }

                }

            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });

    }

    /**
     * 获取分类
     */
    private void requestHomeCategory() {
        ParamInfo paramInfo = new ParamInfo();
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestHomeCategory(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "首页分类：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (data != null && data.size() > 0) {
                    categoryItems.addAll(data);
                }
                mCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }
}
