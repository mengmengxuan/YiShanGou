package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.dialog.ChooseCityDialog;
import com.yunlankeji.yishangou.globle.RequestCode;
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

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/11/9
 * Describe:添加新地址页面
 */
public class EditAddressActivity extends BaseActivity {
    private static final String TAG = "EditAddressActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.part_line)
    View partLine;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_sure_tv)
    TextView mSureTv;
    @BindView(R.id.m_name_et)
    EditText mNameEt;
    @BindView(R.id.m_phone_et)
    EditText mPhoneEt;
    @BindView(R.id.m_arrow_phone_iv)
    ImageView mArrowPhoneIv;
    @BindView(R.id.m_area_tv)
    TextView mAreaTv;
    @BindView(R.id.m_area_detail_et)
    EditText mAreaDetailEt;
    @BindView(R.id.m_arrow_area_iv)
    ImageView mArrowAreaIv;
    @BindView(R.id.m_default_address_switch)
    Switch mDefaultAddressSwitch;
    private Boolean isEdit;// false新增 true修改
    private String mPrivince;
    private String mCity;
    private String mArea;
    private String id;
    private String latitude;//
    private String longtitude;
    private String area;
    private String address;

    @Override
    public int setLayout() {
        return R.layout.activity_edit_address;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("添加新地址");

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            mSureTv.setText("确认修改");
            Data address = (Data) getIntent().getSerializableExtra("address");
            id = address.id;
            //收货人姓名
            mNameEt.setText(address.receiveName);
            //收货人手机号
            mPhoneEt.setText(address.receivePhone);
            //所在地区
            mAreaTv.setText(address.location);
            //详细地址
            mAreaDetailEt.setText(address.adress);

            if ("1".equals(address.isDefault)) {
                mDefaultAddressSwitch.setChecked(true);
            } else {
                mDefaultAddressSwitch.setChecked(false);
            }
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_area_tv, R.id.m_sure_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_area_tv://所在地区
                //跳转至地图页面选地址
                intent.setClass(this, ChooseAreaActivity.class);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_ADDRESS_FROM_MAP);

                break;
            case R.id.m_sure_tv://保存
                //收货人
                String receiverName = mNameEt.getText().toString();
                //手机号码
                String receiverPhone = mPhoneEt.getText().toString();
                //所在地区
                String receiverArea = mAreaTv.getText().toString();
                //详细地址
                String receiverAreaDetail = mAreaDetailEt.getText().toString();
                //是否为默认地址
                boolean checked = mDefaultAddressSwitch.isChecked();

                if (TextUtils.isEmpty(receiverName)) {
                    ToastUtil.showShort("请输入收货人姓名");
                } else if (TextUtils.isEmpty(receiverPhone)) {
                    ToastUtil.showShort("请输入手机号码");
                } else if (!Utils.isMobile(receiverPhone)) {
                    ToastUtil.showShort("手机号格式不正确！");
                } else if (TextUtils.isEmpty(receiverArea)) {
                    ToastUtil.showShort("请选择所在地区");
                } else if (TextUtils.isEmpty(receiverAreaDetail)) {
                    ToastUtil.showShort("请输入详细地址");
                } else {
                    if (isEdit) {
                        //编辑地址
                        requestUpdateMemberAddress(receiverName, receiverPhone, receiverArea, receiverAreaDetail, checked);

                    } else {
                        //添加新地址
                        requestAddReceiverAddress(receiverName, receiverPhone, receiverArea, receiverAreaDetail, checked);
                    }
                }
                break;
        }
    }

    /**
     * @param receiverName
     * @param receiverPhone
     * @param receiverArea
     * @param receiverAreaDetail
     * @param checked
     */
    private void requestUpdateMemberAddress(String receiverName, String receiverPhone, String receiverArea, String receiverAreaDetail, boolean checked) {
        showLoading();
        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        paramInfo.memberCode = Global.memberCode;
        paramInfo.receiveName = receiverName;
        paramInfo.receivePhone = receiverPhone;
        paramInfo.adress = receiverAreaDetail;
        paramInfo.location = receiverArea;
        paramInfo.latitude = latitude;
        paramInfo.longitude = longitude;
        if (checked) {
            paramInfo.isDefault = "1";
        } else {
            paramInfo.isDefault = "0";
        }
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMemberAddress(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "修改地址：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("修改成功");
                finish();
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

    /**
     * 添加新地址
     *
     * @param name               收货人姓名
     * @param receiverPhone      手机号码
     * @param receiverAreaDetail 详细地址
     * @param checked            是否默认
     */
    private void requestAddReceiverAddress(String name, String receiverPhone, String receiverArea,
                                           String receiverAreaDetail, boolean checked) {
        showLoading();
        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.receiveName = name;
        paramInfo.receivePhone = receiverPhone;
        paramInfo.adress = receiverAreaDetail;
        paramInfo.location = receiverArea;
        paramInfo.latitude = latitude;
        paramInfo.longitude = longitude;
        if (checked) {
            paramInfo.isDefault = "1";
        } else {
            paramInfo.isDefault = "0";
        }

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddReceiverAddress(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "添加新地址：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("添加成功");
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == RequestCode.REQUEST_CODE_ADDRESS_FROM_MAP) {
                //选择地点
                latitude = data.getStringExtra("latitude");
                longtitude = data.getStringExtra("longitude");
                area = data.getStringExtra("area");
                address = data.getStringExtra("address");
                mAreaTv.setText(area + address);

                Log.d(TAG, "onActivityResult  latitude: " + latitude);
                Log.d(TAG, "onActivityResult  longtitude: " + longtitude);
            }

        }
    }
}
