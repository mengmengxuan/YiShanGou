package com.yunlankeji.yishangou.activity.mine;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.SecondLineTeamAdapter;
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
import retrofit2.http.HTTP;

/**
 * Create by Snooker on 2020/12/25
 * Describe:他人邀请的人
 */
public class OthersInvitationActivity extends BaseActivity {
    private static final String TAG = "OthersInvitationActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.part_line)
    View partLine;//partLine
    @BindView(R.id.m_other_invitation_rv)
    RecyclerView mOtherInvitationRv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private SecondLineTeamAdapter mSecondLineTeamAdapter;
    private List<Data> items = new ArrayList<>();
    private String mMemberCode;
    private int currPage = 1;

    @Override
    public int setLayout() {
        return R.layout.fragment_other_invitation;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("邀请");

        mMemberCode = getIntent().getStringExtra("memberCode");

        mSecondLineTeamAdapter = new SecondLineTeamAdapter(this);
        mOtherInvitationRv.setLayoutManager(new LinearLayoutManager(this));
        mSecondLineTeamAdapter.setItems(items);
        mOtherInvitationRv.setAdapter(mSecondLineTeamAdapter);

    }

    @Override
    public void initData() {
        //获取下级的下级
        requestSubordinate();
    }

    /**
     * 获取下级的下级
     */
    private void requestSubordinate() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = mMemberCode;
        paramInfo.size = "10";
        paramInfo.page = currPage + "";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestSubordinate(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "下级的下级：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;

                if (data != null) {
                    items.addAll(data.data);
                    mSecondLineTeamAdapter.notifyDataSetChanged();

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

    @OnClick(R.id.m_back_iv)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
        }
    }

}
