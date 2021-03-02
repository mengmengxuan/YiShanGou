package com.yunlankeji.yishangou.fragment.rider;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.rider.RiderRunErrandsOrderDetailActivity;
import com.yunlankeji.yishangou.activity.rider.RiderTakeawayOrderDetailActivity;
import com.yunlankeji.yishangou.adapter.RiderOrderCenterAdapter;
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
import com.yunlankeji.yishangou.utils.ZLBusAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/2
 * Describe:骑手订单大厅
 */
public class RiderOrderCenterFragment extends BaseFragment {

    private static final String TAG = "RiderOrderCenterFragment";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_rider_order_rv)
    RecyclerView mRiderOrderRv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private RiderOrderCenterAdapter mRiderOrderCenterAdapter;
    private List<Data> items = new ArrayList<>();
    private int currPage = 1;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_rider_order_center;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(getActivity(), mRootCl);
        mTitleTv.setText("订单大厅");

        mRiderOrderCenterAdapter = new RiderOrderCenterAdapter(getActivity());
        mRiderOrderRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRiderOrderCenterAdapter.setItems(items);
        mRiderOrderRv.setAdapter(mRiderOrderCenterAdapter);
        mRiderOrderCenterAdapter.setOnItemClickedListener(new RiderOrderCenterAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(View view, int position) {
                //条目点击事件
                //骑手订单详情
                if (items.get(position).orderType.equals("0")) {
                    //外卖订单详情
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), RiderTakeawayOrderDetailActivity.class);
                    intent.putExtra("id", items.get(position).id);
                    intent.putExtra("receiveDistance", items.get(position).receiveDistance);
                    intent.putExtra("releaseTime", items.get(position).releaseTime);
                    startActivity(intent);
                } else if (items.get(position).orderType.equals("1")) {
                    //跑腿订单详情
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), RiderRunErrandsOrderDetailActivity.class);
                    intent.putExtra("id", items.get(position).id);
                    intent.putExtra("receiveDistance", items.get(position).receiveDistance);
                    intent.putExtra("releaseTime", items.get(position).releaseTime);
                    startActivity(intent);
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
                if (items != null) {
                    items.clear();
                }
                requestQueryPageList();

            }
        });
    }

    @Override
    protected void initData() {
        //获取订单列表
        requestQueryPageList();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Refresh_Rider_Order_List)})
    public void refreshRiderOrderList(String status) {
        if (status.equals("Refresh_Rider_Order_List")) {
            //获取订单列表
            if (items != null) {
                items.clear();
            }
            requestQueryPageList();
        }
    }

    /**
     * 获取订单列表
     */
    private void requestQueryPageList() {
        showLoading();
        String longitude = (String) SPUtils.get(getActivity(), "longitude", "");
        String latitude = (String) SPUtils.get(getActivity(), "latitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.size = "10";
        paramInfo.page = currPage + "";
        paramInfo.orderStatus = "1";
        paramInfo.sendLongitude = longitude;
        paramInfo.sendLatitude = latitude;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryPageList(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "订单列表：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;

                if (data != null) {
                    items.addAll(data.data);
                    mRiderOrderCenterAdapter.notifyDataSetChanged();

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

    @OnClick({R.id.m_back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                getActivity().finish();
                break;
        }
    }
}
