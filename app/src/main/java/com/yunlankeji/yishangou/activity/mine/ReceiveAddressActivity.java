package com.yunlankeji.yishangou.activity.mine;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.AddressAdapter;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.globle.ResultCode;
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

/**
 * Create by Snooker on 2020/11/9
 * Describe:收货地址页面
 */
public class ReceiveAddressActivity extends BaseActivity {

    private static final String TAG = "ReceiveAddressActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.part_line)
    View partLine;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_address_rv)
    RecyclerView mAddressRv;
    private AddressAdapter mAddressAdapter;
    private List<Data> items = new ArrayList<>();
    private Intent intent;
    private Boolean isSelectAddress;

    @Override
    public int setLayout() {
        return R.layout.activity_receive_address;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("地址管理");

        isSelectAddress = getIntent().getBooleanExtra("isSelectAddress", false);

        mAddressAdapter = new AddressAdapter(this);
        mAddressAdapter.setItems(items);
        mAddressRv.setLayoutManager(new LinearLayoutManager(this));
        mAddressRv.setAdapter(mAddressAdapter);

        intent = new Intent();

        mAddressAdapter.setOnItemClickedListener(new AddressAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (isSelectAddress) {
                    intent.putExtra("address", items.get(position));
                    setResult(ResultCode.RESULT_CODE_ADDRESS, intent);
                    finish();
                } else {
                    return;
                }
            }

            @Override
            public void onEditViewClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(ReceiveAddressActivity.this, EditAddressActivity.class);
                intent.putExtra("address", items.get(position));
                intent.putExtra("isEdit", true);
                startActivity(intent);
            }

            @Override
            public void onDeleteViewClicked(View view, int position) {
                showDeleteDialog(items.get(position).id);
            }
        });
    }

    @Override
    public void initData() {
//        requestReceiverAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取收货地址
        if (items != null) {
            items.clear();
        }
        requestMemberAddress();
    }

    /**
     * 获取收货地址
     */
    private void requestMemberAddress() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestMemberAddress(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "收货地址：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;

                if (data != null) {
                    items.clear();
                    items.addAll(data);
                    mAddressAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    @OnClick({R.id.m_back_iv, R.id.m_add_address_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_add_address_tv://添加新地址
                intent.setClass(ReceiveAddressActivity.this, EditAddressActivity.class);
                startActivity(intent);
                break;
        }
    }

    //删除地址弹窗
    private void showDeleteDialog(String id) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption("删除地址")
                .setMessage("是否确定删除该地址？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        requestDeleteMemberAddress(id);
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 删除地址
     *
     * @param id
     */
    private void requestDeleteMemberAddress(String id) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestDeleteMemberAddress(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "删除地址：" + JSON.toJSONString(response.data));
                if (items != null) {
                    items.clear();
                }
                requestMemberAddress();
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

}
