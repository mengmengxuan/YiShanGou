package com.yunlankeji.yishangou.fragment.order;

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
import com.yunlankeji.yishangou.activity.runerrands.CheckOrderActivity;
import com.yunlankeji.yishangou.activity.runerrands.ComingActivity;
import com.yunlankeji.yishangou.activity.runerrands.RunErrandsSendedActivity;
import com.yunlankeji.yishangou.activity.runerrands.WaitActivity;
import com.yunlankeji.yishangou.activity.home.StoreDetailActivity;
import com.yunlankeji.yishangou.activity.order.OrderDetailActivity;
import com.yunlankeji.yishangou.activity.order.RunErrandsOrderDetailActivity;
import com.yunlankeji.yishangou.activity.rider.RiderInDeliveryActivity;
import com.yunlankeji.yishangou.adapter.OrderAdapter;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class AllOrderFragment extends BaseFragment {
    private static final String TAG = "AllOrderFragment";

    @BindView(R.id.m_order_list_rv)
    RecyclerView mOrderListRv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private OrderAdapter orderAdapter;
    private List<Data> orders = new ArrayList<>();
    private int currPage = 1;
    private String memberOrderStatus;

    public AllOrderFragment(String memberOrderStatus) {
        this.memberOrderStatus = memberOrderStatus;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_all_order;
    }

    @Override
    protected void initView() {
        orderAdapter = new OrderAdapter(getActivity());
        orderAdapter.setItems(orders);
        mOrderListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderListRv.setAdapter(orderAdapter);
        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (orders.get(position).orderType.equals("0")) {
                    //外卖订单
                    doActivity(OrderDetailActivity.class, orders.get(position).orderNumber, "orderNumber");
                } else {
                    //跑腿订单
                    //0 待派单  1 待接单  2 待取货  3 代配送  4 待收货  5 已完成  6 已取消
                    switch (orders.get(position).orderStatus) {
                        case "0"://立即支付
                            doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            break;
                        case "1"://待接单
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                doActivity(WaitActivity.class);
                            }
                            break;
                        case "2"://待取货
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                doActivity(ComingActivity.class, orders.get(position).orderNumber, "orderNumber");
                            }
                            break;
                        case "3"://待配送
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                doActivity(RiderInDeliveryActivity.class, orders.get(position).orderNumber, "orderNumber");
                            }
                            break;
                        case "4"://待收货，跳转到已送达页面
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                doActivity(RunErrandsSendedActivity.class, orders.get(position).orderNumber, "orderNumber");
                            }
                            break;
                        case "5"://已完成，跳转到跑腿订单详情页面
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), RunErrandsOrderDetailActivity.class);
                                intent.putExtra("orderNumber", orders.get(position).orderNumber);
                                startActivity(intent);
                            }
                            break;
                        case "6":
                            break;
                    }
                }
            }

            @Override
            public void onTitleClicked(View view, int position) {

            }

            @Override
            public void onLookLogisticsClicked(View view, int position) {
                //根据orderType判断是外卖订单还是跑腿订单
                if (orders.get(position).orderType.equals("0")) {
                    //外卖订单
                    //0 待派单  1 待接单  2 待取货  3 代配送  4 待收货  5 已完成  6 已取消
                    switch (orders.get(position).orderStatus) {
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                            doActivity(OrderDetailActivity.class, orders.get(position).orderNumber, "orderNumber");
                            break;
                        case "5":
                        case "6":
                            //再来一单
                            doActivity(StoreDetailActivity.class, orders.get(position).merchantCode, "merchantCode",
                                    orders.get(position).orderNumber, "orderNumber");

                            break;
                    }
                } else if (orders.get(position).orderType.equals("1")) {
                    //跑腿订单
                    //0 待派单  1 待接单  2 待取货  3 代配送  4 待收货  5 已完成  6 已取消
                    switch (orders.get(position).orderStatus) {
                        case "0"://立即支付
                            doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            break;
                        case "1"://待接单
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                doActivity(WaitActivity.class);
                            }
                            break;
                        case "2"://待取货
                        case "3"://待配送
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                doActivity(ComingActivity.class, orders.get(position).orderNumber, "orderNumber");
                            }
                            break;
                        case "4"://待收货，跳转到已送达页面
                            if (orders.get(position).payStatus.equals("0")) {
                                //未支付，跳转到支付页面去支付
                                doActivity(CheckOrderActivity.class, orders.get(position).orderNumber, "orderNumber");
                            } else {
                                doActivity(RunErrandsSendedActivity.class, orders.get(position).orderNumber, "orderNumber");
                            }
                            break;
                        case "5"://已完成
                            break;
                        case "6":
                            break;
                    }

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

    }

    @Override
    public void onResume() {
        super.onResume();
        if (orders != null) {
            orders.clear();
        }
        requestQueryPageList();
    }

    /**
     * 订单
     */
    private void requestQueryPageList() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.size = "10";
        paramInfo.page = currPage + "";
        paramInfo.memberCode = Global.memberCode;
        paramInfo.memberOrderStatus = memberOrderStatus;

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

}
