package com.yunlankeji.yishangou.activity.runerrands;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.globle.RequestCode;
import com.yunlankeji.yishangou.globle.ResultCode;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class SendAddressActivity extends BaseActivity {

    private static final String TAG = "SendAddressActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_area_tv)
    TextView mAreaTv;
    @BindView(R.id.m_address_detail_tv)
    TextView mAddressDetailTv;
    @BindView(R.id.m_door_num_et)
    EditText mDoorNumEt;
    @BindView(R.id.m_name_et)
    EditText mNameEt;
    @BindView(R.id.m_phone_et)
    EditText mPhoneEt;

    private String title;
    private String latitude;
    private String longitude;
    private String area;
    private String address;
    private String doorNum;
    private String name;
    private String phone;

    @Override
    public int setLayout() {
        return R.layout.activtity_send_address;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            mTitleTv.setText(title);
        }
    }

    @Override
    public void initData() {
        super.initData();
        area = getIntent().getStringExtra("area");
        address = getIntent().getStringExtra("address");
        doorNum = getIntent().getStringExtra("doorNum");
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");

        if (!TextUtils.isEmpty(area)) {
            mAreaTv.setText(area);
        }
        if (!TextUtils.isEmpty(address)) {
            mAddressDetailTv.setVisibility(View.VISIBLE);
            mAddressDetailTv.setText(address);
        }
        if (!TextUtils.isEmpty(doorNum)) {
            mDoorNumEt.setText(doorNum);
        }
        if (!TextUtils.isEmpty(name)) {
            mNameEt.setText(name);
        }
        if (!TextUtils.isEmpty(phone)) {
            mPhoneEt.setText(phone);
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_select_address_rl, R.id.m_sure_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_select_address_rl:
                intent.setClass(this, RunErrandsSelectAddressActivity.class);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_ADDRESS_FROM_MAP);
                break;
            case R.id.m_sure_tv:
                doorNum = mDoorNumEt.getText().toString().trim();
                name = mNameEt.getText().toString().trim();
                phone = mPhoneEt.getText().toString().trim();
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.showShort("请选择地址");
                } else if (TextUtils.isEmpty(doorNum)) {
                    ToastUtil.showShort("请填写门牌号");
                } else if (TextUtils.isEmpty(name)) {
                    ToastUtil.showShort("请填写姓名");
                } else if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort("请填写联系电话");
                } else if (!Utils.isMobile(phone)) {
                    ToastUtil.showShort("号码格式不对");
                } else {
                    intent.putExtra("doorNum", doorNum);
                    intent.putExtra("name", name);
                    intent.putExtra("phone", phone);
                    intent.putExtra("area", area);
                    intent.putExtra("address", address);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    if (title.equals("发货地址")) {
                        setResult(ResultCode.RESULT_CODE_SEND_ADDRESS, intent);
                        finish();
                    } else if (title.equals("收货地址")) {
                        setResult(ResultCode.RESULT_CODE_RECEIVE_ADDRESS, intent);
                        finish();
                    }
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == RequestCode.REQUEST_CODE_ADDRESS_FROM_MAP) {
                //选择地点
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                area = data.getStringExtra("area");
                address = data.getStringExtra("address");
                mAreaTv.setText(area);
                mAddressDetailTv.setText(address);
                mAddressDetailTv.setVisibility(View.VISIBLE);
                Log.d(TAG, "onActivityResult  latitude: " + latitude);
                Log.d(TAG, "onActivityResult  longitude: " + longitude);
            }

        }
    }

}
