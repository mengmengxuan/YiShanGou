package com.yunlankeji.yishangou.fragment.business;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.business.BusinessOrderDetailActivity;
import com.yunlankeji.yishangou.activity.home.PayActivity;
import com.yunlankeji.yishangou.activity.home.PushOrderActivity;
import com.yunlankeji.yishangou.activity.home.StoreDetailActivity;
import com.yunlankeji.yishangou.activity.order.OrderDetailActivity;
import com.yunlankeji.yishangou.adapter.BusinessOrderAdapter;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class BusinessOrderFragment extends BaseFragment {
    private static final String TAG = "BusinessOrderFragment";

    @BindView(R.id.m_order_list_rv)
    RecyclerView mOrderListRv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private BusinessOrderAdapter orderAdapter;
    private List<Data> orders = new ArrayList<>();
    private int currPage = 1;

    private String merchantOrderStatus;

    public BusinessOrderFragment(String merchantOrderStatus) {
        this.merchantOrderStatus = merchantOrderStatus;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_all_order;
    }

    @Override
    protected void initView() {
        super.initView();

        orderAdapter = new BusinessOrderAdapter(getActivity());
        orderAdapter.setItems(orders);
        mOrderListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderListRv.setAdapter(orderAdapter);
        orderAdapter.setOnItemClickListener(new BusinessOrderAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                doActivity(BusinessOrderDetailActivity.class, orders.get(position).orderNumber, "orderNumber");
            }

            @Override
            public void onTitleClicked(View view, int position) {
            }

            @Override
            public void onLookLogisticsClicked(View view, int position) {
                if (orders.get(position).orderStatus.equals("0")) {
                    //派单
                    requestUpdateMemberOrderStatus(orders.get(position).orderNumber);
                } else {
                    //查看物流
                    doActivity(BusinessOrderDetailActivity.class, orders.get(position).orderNumber, "orderNumber");
                }
            }
        });

        //下来刷新和上拉加载
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currPage++;
                requestQueryPageList();

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currPage = 1;
                if (orders != null) {
                    orders.clear();
                }
                requestQueryPageList();

            }
        });
    }

    @Override
    protected void initData() {
        orders.clear();
        requestQueryPageList();
    }

    /**
     * 订单
     */
    private void requestQueryPageList() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.size = "10";
        paramInfo.page = currPage + "";
        paramInfo.merchantCode = Global.merchantCode;
        paramInfo.merchantOrderStatus = merchantOrderStatus;
//        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryPageList(paramInfo);
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryPageList(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "订单列表：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;

                if (data != null) {
                    orders.addAll(data.data);
                    orderAdapter.notifyDataSetChanged();
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
     * 派单
     */
    private void requestUpdateMemberOrderStatus(String orderNumber) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = orderNumber;
        paramInfo.orderStatus = "1";
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMemberOrderStatus(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "订单列表：" + JSON.toJSONString(response.data));
                currPage = 1;
                if (orders != null) {
                    orders.clear();
                }
                requestQueryPageList();
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
