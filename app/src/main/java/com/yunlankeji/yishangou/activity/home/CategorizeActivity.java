package com.yunlankeji.yishangou.activity.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.BusinessAdapter;
import com.yunlankeji.yishangou.adapter.GoodsAdapter;
import com.yunlankeji.yishangou.adapter.StoreListAdapter;
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

public class CategorizeActivity extends BaseActivity {

    private final String TAG = "CategorizeActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_comprehensive_tv)
    TextView mComprehensiveTv;//综合排序
    @BindView(R.id.m_fast_tv)
    TextView mFastTv;//派送最快
    @BindView(R.id.m_sales_tv)
    TextView mSalesTv;//销量最高
    @BindView(R.id.m_early_tv)
    TextView mEarlyTv;//入驻最早
    @BindView(R.id.m_late_tv)
    TextView mLateTv;//入驻最迟
    @BindView(R.id.m_store_list_rv)
    RecyclerView mStoreListRv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.m_keyword_et)
    EditText mKeywordEt;

    private int currPage = 1;
    private StoreListAdapter mStoreListAdapter;
    private List<Data> storeList = new ArrayList<>();
    private String title;
    private String type = "all";
    private String keyword;
    private String merchantTypeCode;

    @Override
    public int setLayout() {
        return R.layout.activity_categorize;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        title = getIntent().getStringExtra("title");
        merchantTypeCode = getIntent().getStringExtra("merchantTypeCode");
        mTitleTv.setText(title);

        //商家列表
        mStoreListAdapter = new StoreListAdapter(this);
        mStoreListRv.setLayoutManager(new LinearLayoutManager(this));
        mStoreListAdapter.setItems(storeList);
        mStoreListRv.setAdapter(mStoreListAdapter);

        mStoreListAdapter.setOnItemClickListener(new StoreListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                doActivity(CategorizeActivity.this, StoreDetailActivity.class, storeList.get(position).merchantCode, "merchantCode");
            }
        });

        showView(1);
        //下来刷新和上拉加载
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currPage++;
                requestQueryMerchantPage();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currPage = 1;
                if (storeList != null) {
                    storeList.clear();
                }
                requestQueryMerchantPage();
            }
        });
    }

    @Override
    public void initData() {
        //获取商家列表
        requestQueryMerchantPage();
    }

    @OnClick({R.id.m_back_iv, R.id.m_search_iv, R.id.m_comprehensive_tv, R.id.m_fast_tv, R.id.m_sales_tv, R.id.m_early_tv, R.id.m_late_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_search_iv://点击搜索
                keyword = mKeywordEt.getText().toString().trim();
                storeList.clear();
                requestQueryMerchantPage();
                break;
            case R.id.m_comprehensive_tv://综合排序
                showView(1);
                type = "all";
                storeList.clear();
                requestQueryMerchantPage();
                break;
            case R.id.m_fast_tv://派送最快
                showView(2);
                type = "distance";
                storeList.clear();
                requestQueryMerchantPage();
                break;
            case R.id.m_sales_tv://销量最高
                showView(3);
                type = "saleCount";
                storeList.clear();
                requestQueryMerchantPage();
                break;
            case R.id.m_early_tv://入驻最早
                showView(4);
                type = "idAsc";
                storeList.clear();
                requestQueryMerchantPage();
                break;
            case R.id.m_late_tv://入驻最迟
                showView(5);
                type = "idDesc";
                storeList.clear();
                requestQueryMerchantPage();
                break;
        }
    }

    //选项颜色控制
    public void showView(int i) {
        mComprehensiveTv.setTextColor(i == 1 ? this.getResources().getColor(R.color.color_F36C17) : this.getResources().getColor(R.color.color_666666));
        mFastTv.setTextColor(i == 2 ? this.getResources().getColor(R.color.color_F36C17) : this.getResources().getColor(R.color.color_666666));
        mSalesTv.setTextColor(i == 3 ? this.getResources().getColor(R.color.color_F36C17) : this.getResources().getColor(R.color.color_666666));
        mEarlyTv.setTextColor(i == 4 ? this.getResources().getColor(R.color.color_F36C17) : this.getResources().getColor(R.color.color_666666));
        mLateTv.setTextColor(i == 5 ? this.getResources().getColor(R.color.color_F36C17) : this.getResources().getColor(R.color.color_666666));
    }

    /**
     * 具体分类
     */
    private void requestQueryMerchantPage() {
        showLoading();
        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.page = currPage + "";
        paramInfo.size = "10";
        paramInfo.latitude = latitude;
        paramInfo.longitude = longitude;
        paramInfo.typeName = title;
        paramInfo.orderByType = type;
        paramInfo.merchantTypeCode = merchantTypeCode;
        if (!TextUtils.isEmpty(keyword)) {
            paramInfo.productName = keyword;
        }
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestHotMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "具体分类：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;

                if (data != null) {
                    storeList.addAll(data.data);
                    mStoreListAdapter.notifyDataSetChanged();

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
}
