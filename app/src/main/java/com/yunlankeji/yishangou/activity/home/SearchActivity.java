package com.yunlankeji.yishangou.activity.home;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.widget.GridViewForScrollView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.HistorySearchAdapter;
import com.yunlankeji.yishangou.adapter.SearchAdapter;
import com.yunlankeji.yishangou.adapter.SearchResultAdapter;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.globle.Global;
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
import com.yunlankeji.yishangou.view.ShowView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/26
 * Describe:搜索页面
 */
public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_search_et)
    EditText mSearchEt;
    @BindView(R.id.m_search_tv)
    TextView mSearchTv;
    @BindView(R.id.m_cancel_iv)
    ImageView mCancelIv;
    @BindView(R.id.m_history_search_gv)
    GridViewForScrollView mHistorySearchGv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.m_search_result_rv)
    RecyclerView mSearchResultRv;
    @BindView(R.id.show_view)
    ShowView showView;
    @BindView(R.id.m_history_search_ll)
    LinearLayout mHistorySearchLl;
    private String mKeyword;
    private SearchAdapter mSearchAdapter;
    private List<Data> items = new ArrayList<>();
    private int currPage = 1;
    private HistorySearchAdapter mHistorySearchAdapter;
    private List<Data> historyItems = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("搜索");

        //历史搜索
        mHistorySearchAdapter = new HistorySearchAdapter(this);
        mHistorySearchAdapter.setItems(historyItems);
        mHistorySearchGv.setAdapter(mHistorySearchAdapter);

        mSearchAdapter = new SearchAdapter(this);
        mSearchAdapter.setItems(items);
        mSearchResultRv.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultRv.setAdapter(mSearchAdapter);
        mSearchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, StoreDetailActivity.class);
                intent.putExtra("merchantCode", items.get(position).merchantCode);
                startActivity(intent);
            }
        });

        //下来刷新和上拉加载
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currPage++;
                requestHotMerchant();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currPage = 1;
                if (items != null) {
                    items.clear();
                }
                requestHotMerchant();
            }
        });

        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mHistorySearchLl.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.GONE);
                    //获取历史搜索
                    requestHistorySearch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        //获取历史搜索
        requestHistorySearch();
    }

    /**
     * 获取历史搜索
     */
    private void requestHistorySearch() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<List<Data>>> call =
                NetWorkManager.getInstance().getRequest().requestHistorySearch(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "历史搜索：" + JSON.toJSONString(response.data));

                List<Data> data = (List<Data>) response.data;

                if (data != null) {
                    historyItems.clear();
                    historyItems.addAll(data);
                    mHistorySearchAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.m_back_iv, R.id.m_search_tv, R.id.m_cancel_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_search_tv:
                mKeyword = mSearchEt.getText().toString();
                if (TextUtils.isEmpty(mKeyword)) {
                    ToastUtil.showShort("请输入关键词");
                } else {
                    requestHotMerchant();
                }
                break;
            case R.id.m_cancel_iv://清空历史搜索
                showDeleteHistorySearchDialog();

                break;
        }
    }

    private void showDeleteHistorySearchDialog() {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption("删除")
                .setMessage("是否删除历史搜索？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                        //删除历史搜索
                        requestDeleteAll();
                    }
                }).show();
    }

    /**
     * 删除历史搜索
     */
    private void requestDeleteAll() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestDeleteAll(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "删除历史搜索：" + JSON.toJSONString(response.data));
                //获取历史搜索
                requestHistorySearch();
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

    private void requestHotMerchant() {
        showLoading();
        String longitude = (String) SPUtils.get(this, "longitude", "");
        String latitude = (String) SPUtils.get(this, "latitude", "");
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.productName = mKeyword;
        paramInfo.longitude = longitude;
        paramInfo.latitude = latitude;
        paramInfo.page = currPage + "";
        paramInfo.size = "10";
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestHotMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "搜素结果：" + JSON.toJSONString(response.data));

                mHistorySearchLl.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);

                Data data = (Data) response.data;
                if (data != null) {
                    items.clear();
                    items.addAll(data.data);
                    mSearchAdapter.notifyDataSetChanged();

                    if (items.size() == 0) {
                        showView.show(ShowView.NO_DATA);
                    } else {
                        showView.hide();
                    }

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

    @OnItemClick(R.id.m_history_search_gv)
    public void onItemClicked(int position) {
        mSearchEt.setText(historyItems.get(position).seacrhName);
        mKeyword = mSearchEt.getText().toString();
        mSearchEt.setSelection(mKeyword.length());
        requestHotMerchant();
    }
}
