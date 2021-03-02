package com.yunlankeji.yishangou.activity.home;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.utils.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.SpecialZoneCommodityAdapter;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.Api;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.QRCodeUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/8
 * Describe:特惠专区页面
 */
public class SpecialZoneActivity extends BaseActivity {

    private static final String TAG = "SpecialZoneActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_special_commodity_rv)
    RecyclerView m_special_commodity_rv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    private SpecialZoneCommodityAdapter mSpecialZoneCommodityAdapter;
    private List<Data> items = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_special_zone;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_F36C17));
        mTitleTv.setText("特惠专区");
        mBackIv.setImageResource(R.mipmap.icon_arrow_white_left);

        mSpecialZoneCommodityAdapter = new SpecialZoneCommodityAdapter(this);
        mSpecialZoneCommodityAdapter.setItems(items);
        m_special_commodity_rv.setLayoutManager(new LinearLayoutManager(this));
        m_special_commodity_rv.setAdapter(mSpecialZoneCommodityAdapter);
        mSpecialZoneCommodityAdapter.setOnItemClickedListener(new SpecialZoneCommodityAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(View view, int position) {
//                doActivity(FoodDetailActivity.class, items.get(position).id, "id");
                Intent intent = new Intent();
                intent.setClass(SpecialZoneActivity.this, FoodDetailActivity.class);
                intent.putExtra("id", items.get(position).id);
                intent.putExtra("from", "storeDetail");
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        //获取特惠专区列表
        requestHotProduct();
    }

    /**
     * 获取特惠专区列表
     */
    private void requestHotProduct() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.page = "1";
        paramInfo.size = "10";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestHotProduct(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "特惠专区：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    items.addAll(data.data);
                    mSpecialZoneCommodityAdapter.notifyDataSetChanged();

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
                LogUtil.d(TAG, msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, msg);
            }
        });
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
