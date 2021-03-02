package com.yunlankeji.yishangou.fragment.mine;

import android.content.Intent;
import android.util.Log;
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
import com.yunlankeji.yishangou.activity.mine.OthersInvitationActivity;
import com.yunlankeji.yishangou.adapter.FirstLineTeamAdapter;
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
import java.util.Currency;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/25
 * Describe:
 */
public class FirstLineTeamFragment extends BaseFragment {

    private static final String TAG = "FirstLineTeamFragment";
    @BindView(R.id.m_first_line_team_rv)
    RecyclerView mFirstLineTeamRv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private FirstLineTeamAdapter mFirstLineTeamAdapter;
    private List<Data> items = new ArrayList<>();
    private int currPage = 1;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_first_line_team;
    }

    @Override
    protected void initView() {
        mFirstLineTeamAdapter = new FirstLineTeamAdapter(getActivity());
        mFirstLineTeamRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFirstLineTeamAdapter.setItems(items);
        mFirstLineTeamRv.setAdapter(mFirstLineTeamAdapter);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //上拉加载
                currPage++;
                requestInviteList();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //下来刷新
                currPage = 1;

                if (items != null) {
                    items.clear();
                }
                requestInviteList();
            }
        });

        mFirstLineTeamAdapter.setOnItemClickListener(new FirstLineTeamAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), OthersInvitationActivity.class);
                intent.putExtra("memberCode", items.get(position).memberCode);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {
        //一线团队
        requestInviteList();

    }

    /**
     * 获取一线团队数据
     */
    private void requestInviteList() {
        showLoading();
        //参数:page size level(level状态码等于1是查询一级下线   2是查询二级下线)   inviteCode邀请码
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.page = currPage + "";
        paramInfo.size = "10";
        paramInfo.level = "1";
        paramInfo.memberCode = Global.memberCode;

        LogUtil.d(TAG, " paramInfo.inviteCode --> " + paramInfo.inviteCode);

        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestInviteList(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "一线团队：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;

                if (data != null) {
                    items.addAll(data.data);
                    mFirstLineTeamAdapter.notifyDataSetChanged();

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
