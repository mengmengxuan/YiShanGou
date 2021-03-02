package com.yunlankeji.yishangou.activity.business;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.widget.wheelview.ItemsRange;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.EditAddressActivity;
import com.yunlankeji.yishangou.activity.mine.ReceiveAddressActivity;
import com.yunlankeji.yishangou.adapter.TimeAdapter;
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
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class WorkTimeActivity extends BaseActivity {

    private static final String TAG = "WorkTimeActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_time_rv)
    RecyclerView mTimeRv;
    @BindView(R.id.m_right_tv)
    TextView mRightTv;

    private TimeAdapter timeAdapter;
    private List<Data> times = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_work_time;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("忙碌时间");
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setTextColor(getResources().getColor(R.color.color_333333));
        mRightTv.setText("新增");

        timeAdapter = new TimeAdapter(this);
        timeAdapter.setItems(times);
        mTimeRv.setLayoutManager(new LinearLayoutManager(this));
        mTimeRv.setAdapter(timeAdapter);
        timeAdapter.setOnItemClickListener(new TimeAdapter.OnItemClickListener() {
            @Override
            public void onEditClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(WorkTimeActivity.this, CreateBusyTimeActivity.class);
                intent.putExtra("title", "修改时间");
                intent.putExtra("startTime", times.get(position).startTime);
                intent.putExtra("endTime", times.get(position).endTime);
                intent.putExtra("id", times.get(position).id);
                startActivity(intent);
            }

            @Override
            public void onDeleteClicked(View view, int position) {
                showDeleteDialog(times.get(position).id);
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (times != null) {
            times.clear();
        }
        //获取忙碌时间
        requestGetMerchantBusyTime();
    }

    /**
     * 获取忙碌时间
     */
    private void requestGetMerchantBusyTime() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = Global.merchantCode;
        Call<ResponseBean<List<Data>>> call =
                NetWorkManager.getInstance().getRequest().requestGetMerchantBusyTime(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "获取忙碌时间：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (data != null) {
                    times.addAll(data);
                    timeAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.m_back_iv, R.id.m_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_right_tv://新增
                Intent intent = new Intent();
                intent.setClass(this, CreateBusyTimeActivity.class);
                intent.putExtra("title", "新增时间");
                startActivity(intent);

                break;
        }
    }

    //删除忙碌时间弹窗
    private void showDeleteDialog(String id) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption("删除时间")
                .setMessage("是否确定删除该时间？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        //删除时间
                        requestDeleteMerchantBusyTime(id);
                        dialog.dismiss();
                    }
                }).show();
    }

    private void requestDeleteMerchantBusyTime(String id) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestDeleteMerchantBusyTime(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "删除时间：" + JSON.toJSONString(response.data));
                if (times != null) {
                    times.clear();
                }
                //获取忙碌时间
                requestGetMerchantBusyTime();

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
