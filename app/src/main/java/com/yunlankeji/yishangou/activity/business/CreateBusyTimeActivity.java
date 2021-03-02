package com.yunlankeji.yishangou.activity.business;

import android.service.quicksettings.Tile;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/30
 * Describe:新增和修改时间页面
 */
public class CreateBusyTimeActivity extends BaseActivity {
    private static final String TAG = "CreateBusyTimeActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.part_line)
    View partLine;//partLine
    @BindView(R.id.m_start_hour_et)
    EditText mStartHourEt;
    @BindView(R.id.m_start_minute_et)
    EditText mStartMinuteEt;
    @BindView(R.id.m_end_hour_et)
    EditText mEndHourEt;
    @BindView(R.id.m_end_minute_et)
    EditText mEndMinuteEt;
    @BindView(R.id.m_commit_tv)
    TextView mCommitTv;
    private String mStartHour;
    private String mStartMinute;
    private String mEndHour;
    private String mEndMinute;
    private String id;

    @Override
    public int setLayout() {
        return R.layout.activity_create_busy_time;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);

        String title = getIntent().getStringExtra("title");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        id = getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(title)) {
            mTitleTv.setText(title);

            if (title.equals("新增时间")) {
                mCommitTv.setText("确认新增");
            } else if (title.equals("修改时间")) {
                mCommitTv.setText("确认修改");
            }
        }

        if (!TextUtils.isEmpty(startTime)) {
            String[] split = startTime.split(":");
            mStartHourEt.setText(split[0]);
            mStartMinuteEt.setText(split[1]);
        }

        if (!TextUtils.isEmpty(endTime)) {
            String[] split = endTime.split(":");
            mEndHourEt.setText(split[0]);
            mStartMinuteEt.setText(split[1]);
        }

    }

    @OnClick({R.id.m_back_iv, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_commit_tv:
                mStartHour = mStartHourEt.getText().toString();
                mStartMinute = mStartMinuteEt.getText().toString();
                mEndHour = mEndHourEt.getText().toString();
                mEndMinute = mEndMinuteEt.getText().toString();

                if (TextUtils.isEmpty(mStartHour)) {
                    ToastUtil.showShort("请输入开始时间");
                } else if (TextUtils.isEmpty(mStartMinute)) {
                    ToastUtil.showShort("请输入开始时间");
                } else if (TextUtils.isEmpty(mEndHour)) {
                    ToastUtil.showShort("请输入结束时间");
                } else if (TextUtils.isEmpty(mEndMinute)) {
                    ToastUtil.showShort("请输入结束时间");
                } else {
                    if (id != null) {
                        //修改时间
                        requestUpdateMerchantBusyTime();

                    } else {
                        //新增时间
                        requestAddMerchantBusyTime();
                    }
                }
                break;
        }
    }

    private void requestUpdateMerchantBusyTime() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        paramInfo.startTime = mStartHour + ":" + mStartMinute;
        paramInfo.endTime = mEndHour + ":" + mEndMinute;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMerchantBusyTime(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "修改时间：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("修改成功");
                finish();
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
     * 新增时间
     */
    private void requestAddMerchantBusyTime() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = Global.merchantCode;
        paramInfo.merchantName = Global.merchantName;
        paramInfo.startTime = mStartHour + ":" + mStartMinute;
        paramInfo.endTime = mEndHour + ":" + mEndMinute;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddMerchantBusyTime(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "新增时间：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("新增成功");
                finish();
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
