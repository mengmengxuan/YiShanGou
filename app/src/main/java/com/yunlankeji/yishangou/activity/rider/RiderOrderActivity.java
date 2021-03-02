package com.yunlankeji.yishangou.activity.rider;

import android.content.Intent;
import android.view.View;
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
import com.yunlankeji.yishangou.adapter.RiderOrderAdapter;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

//配送订单(骑手端)
public class RiderOrderActivity extends BaseActivity {

    private static final String TAG = "RiderOrderActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.m_order_list_rv)
    RecyclerView mOrderListRv;

    private RiderOrderAdapter riderOrderAdapter;
    private List<Data> orders = new ArrayList<>();
    private int currPage = 1;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_order;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("配送订单");

        riderOrderAdapter = new RiderOrderAdapter(this);
        riderOrderAdapter.setItems(orders);
        mOrderListRv.setLayoutManager(new LinearLayoutManager(this));
        mOrderListRv.setAdapter(riderOrderAdapter);
        riderOrderAdapter.setOnItemClickListener(new RiderOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (orders.get(position).orderType.equals("0")) {
                    //外卖订单
                    //0 待派单  1 待接单  2 待取货  3 代配送  4 待收货  5 已完成  6 已取消
                    if (orders.get(position).orderStatus.equals("2")) {
                        //跳转到去取货页面
                        Intent intent = new Intent();
                        intent.setClass(RiderOrderActivity.this, RiderPickUpActivity.class);
                        intent.putExtra("sendLatitude", orders.get(position).sendLatitude);
                        intent.putExtra("sendLongitude", orders.get(position).sendLongitude);
                        intent.putExtra("receiveLatitude", orders.get(position).receiveLatitude);
                        intent.putExtra("receiveLongitude", orders.get(position).receiveLongitude);
                        intent.putExtra("id", orders.get(position).id);
                        startActivity(intent);
                    } else if (orders.get(position).orderStatus.equals("3")) {
                        //跳转到送货中页面
                        Intent intent = new Intent();
                        intent.setClass(RiderOrderActivity.this, RiderInDeliveryActivity.class);
                        intent.putExtra("id", orders.get(position).id);
                        startActivity(intent);
                    } else if (orders.get(position).orderStatus.equals("4")) {
                        //跳转到确认送达页面
                        Intent intent = new Intent();
                        intent.setClass(RiderOrderActivity.this, RiderConfirmDelivery.class);
                        intent.putExtra("id", orders.get(position).id);
                        startActivity(intent);
                    }
                } else if (orders.get(position).orderType.equals("1")) {
                    Intent intent = new Intent();
                    //跑腿订单
                    //0 待派单  1 待接单  2 待取货  3 待配送  4 待收货  5 已完成  6 已取消
                    switch (orders.get(position).orderStatus) {
                        case "2"://待取货
                            //跳转到去取货页面
                            intent.setClass(RiderOrderActivity.this, RiderPickUpActivity.class);
                            intent.putExtra("sendLatitude", orders.get(position).sendLatitude);
                            intent.putExtra("sendLongitude", orders.get(position).sendLongitude);
                            intent.putExtra("receiveLatitude", orders.get(position).receiveLatitude);
                            intent.putExtra("receiveLongitude", orders.get(position).receiveLongitude);
                            intent.putExtra("id", orders.get(position).id);
                            startActivity(intent);
                            break;
                        case "3"://待配送
                            doActivity(RiderOrderActivity.this, RiderInDeliveryActivity.class, orders.get(position).id,
                                    "id");
                            break;
                        case "4"://待收货，跳转到确认送达页面
                            if ("1".equals(orders.get(position).riderStatus)) {
                                //orderStatus为4 riderStatus为1 说明骑手已送达，但用为点击确认送达
                                ToastUtil.showShort("等待用户确认送达");
                            } else {
                                intent.setClass(RiderOrderActivity.this, RiderConfirmDelivery.class);
                                intent.putExtra("id", orders.get(position).id);
                                startActivity(intent);
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
    public void initData() {
        super.initData();
        requestQueryPageList();
    }

    @OnClick({R.id.m_back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
        }
    }

    /**
     * 订单
     */
    private void requestQueryPageList() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.size = "10";
        paramInfo.page = currPage + "";
        paramInfo.riderCode = Global.riderCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryPageList(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "订单列表：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;

                if (data != null) {
                    orders.addAll(data.data);
                    riderOrderAdapter.notifyDataSetChanged();
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
