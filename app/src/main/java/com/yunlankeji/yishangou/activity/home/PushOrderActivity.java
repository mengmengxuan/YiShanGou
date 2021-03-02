package com.yunlankeji.yishangou.activity.home;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.ReceiveAddressActivity;
import com.yunlankeji.yishangou.adapter.OrderFoodAdapter;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.globle.RequestCode;
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
import com.yunlankeji.yishangou.utils.ZLBusAction;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * 提交订单页面
 */
public class PushOrderActivity extends BaseActivity {

    private static final String TAG = "PushOrderActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_order_food_rv)
    RecyclerView mOrderFoodRv;//订单商品列表
    @BindView(R.id.m_address_tv)
    TextView mAddressTv;//地址
    @BindView(R.id.m_name_tv)
    TextView mNameTv;//姓名
    @BindView(R.id.m_phone_tv)
    TextView mPhoneTv;//电话
    @BindView(R.id.m_packing_fee_tv)
    TextView mPackingFeeTv;//打包费
    @BindView(R.id.m_delivery_fee_tv)
    TextView mDeliveryFeeTv;//配送费
    @BindView(R.id.m_remark_et)
    EditText mRemarkEt;//备注
    @BindView(R.id.m_price_tv)
    TextView mPriceTv;//总价
    @BindView(R.id.m_push_tv)
    TextView mPushTv;//提交订单
    @BindView(R.id.m_business_name_tv)
    TextView mBusinessNameTv;//商家名称
    @BindView(R.id.m_receive_member_info_rl)
    RelativeLayout mReceiveMemberInfoRl;//收件人姓名、电话

    private List<Data> mCarList;
    private String mDeliveryFee;
    private double totalPrice;
    private String mMerchantCode;
    private Data mAddressBean;
    private String mMerchantName;
    private String ids;
    private double price;
    private String mPackingMoney;

    @Override
    public int setLayout() {
        return R.layout.activity_push_order;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("提交订单");

        //选中的商品列表
        mCarList = (List<Data>) getIntent().getSerializableExtra("carList");
        LogUtil.d(TAG, "mCarList --> " + JSON.toJSONString(mCarList));

        //打包费
        mPackingMoney = getIntent().getStringExtra("packingMoney");
        LogUtil.d(TAG, "mPackingMoney --> " + JSON.toJSONString(mPackingMoney));

        //配送费
        mDeliveryFee = getIntent().getStringExtra("deliveryFee");
        LogUtil.d(TAG, "mDeliveryFee --> " + mDeliveryFee);

        //商家编号
        mMerchantCode = getIntent().getStringExtra("merchantCode");
        LogUtil.d(TAG, "mMerchantCode --> " + mMerchantCode);

        //商家名称
        mMerchantName = getIntent().getStringExtra("merchantName");
        LogUtil.d(TAG, "mMerchantName --> " + mMerchantName);

        //购物车ids
        ids = getIntent().getStringExtra("ids");
        LogUtil.d(TAG, "ids --> " + ids);

        OrderFoodAdapter orderFoodAdapter = new OrderFoodAdapter(this);
        orderFoodAdapter.setItems(mCarList);
        mOrderFoodRv.setLayoutManager(new LinearLayoutManager(this));
        mOrderFoodRv.setAdapter(orderFoodAdapter);

    }

    @Override
    public void initData() {
        //TODO 获取默认地址
        requestDefaultAddress();

        //设置商家名称
        mBusinessNameTv.setText(mMerchantName);

        //总金额
        if (mCarList != null && mCarList.size() > 0) {
            for (Data data : mCarList) {
                totalPrice += Double.parseDouble(data.num) * Double.parseDouble(data.price);
                //不加配送费的总金额
                price += Double.parseDouble(data.num) * Double.parseDouble(data.price);
            }
        }

        //加上配送费
        if (!TextUtils.isEmpty(mDeliveryFee)) {
            totalPrice += Double.parseDouble(mDeliveryFee);
        }

        //加上打包费
        if (!TextUtils.isEmpty(mPackingMoney)) {
            totalPrice += Double.parseDouble(mPackingMoney);

            //不加配送费
            price += Double.parseDouble(mPackingMoney);
        }

        //设置总金额
        mPriceTv.setText("￥" + totalPrice);

        //设置配送费
        if (TextUtils.isEmpty(mDeliveryFee)) {
            mDeliveryFeeTv.setText("￥0");
        } else {
            mDeliveryFeeTv.setText("￥" + mDeliveryFee);
        }

        //设置打包费
        if (TextUtils.isEmpty(mPackingMoney)) {
            mPackingFeeTv.setText("￥0");
        } else {
            mPackingFeeTv.setText("￥" + mPackingMoney);
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Pay_Success)})
    public void paySuccess(String status) {
        if ("Pay_Success".equals(status)) {
            finish();
        }
    }

    /**
     * 获取默认地址
     */
    private void requestDefaultAddress() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestDefaultAddress(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "默认地址：" + JSON.toJSONString(response.data));
                mAddressBean = (Data) response.data;
                if (mAddressBean != null) {
                    mReceiveMemberInfoRl.setVisibility(View.VISIBLE);

                    //姓名
                    mNameTv.setText(mAddressBean.receiveName);
                    //手机号
                    mPhoneTv.setText(mAddressBean.receivePhone);
                    //收货地址
                    mAddressTv.setText(mAddressBean.location + mAddressBean.adress);

                    //获取地址id，用于获取配送费
                    String id = mAddressBean.id;
                    requestShippingAccount(id);
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

    @OnClick({R.id.m_back_iv, R.id.m_select_address_rl, R.id.m_push_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_select_address_rl://选择地址
                intent.setClass(this, ReceiveAddressActivity.class);
                intent.putExtra("isSelectAddress", true);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_INHEAD);
                break;
            case R.id.m_push_tv://提交订单

//                m_price_tv
                String totalPrice = mPriceTv.getText().toString();

                if (mAddressBean == null) {
                    ToastUtil.showShort("请选择地址");
                } else {
                    String remark = mRemarkEt.getText().toString();

                    //创建订单
                    requestCreateMemberOrder(remark, totalPrice);

                }

                break;
        }
    }

    /**
     * 创建订单
     *
     * @param remark
     * @param totalPrice
     */
    private void requestCreateMemberOrder(String remark, String totalPrice) {

        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.cartIds = ids;
        paramInfo.memberCode = Global.memberCode;
        paramInfo.merchantCode = mMerchantCode;
        paramInfo.orderType = "0";
        paramInfo.receiveAdressId = mAddressBean.id;
        paramInfo.remark = remark;

        LogUtil.d(TAG, "paramInfo.cartIds --> " + paramInfo.cartIds);
        LogUtil.d(TAG, "paramInfo.memberCode --> " + paramInfo.memberCode);
        LogUtil.d(TAG, "paramInfo.merchantCode --> " + paramInfo.merchantCode);
        LogUtil.d(TAG, "paramInfo.orderType --> " + paramInfo.orderType);
        LogUtil.d(TAG, "paramInfo.receiveAdressId --> " + paramInfo.receiveAdressId);
        LogUtil.d(TAG, "paramInfo.remark --> " + paramInfo.remark);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestCreateMemberOrder(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();

                LogUtil.d(TAG, "提交订单：" + JSON.toJSONString(response.data));
                String orderNum = (String) response.data;

                Intent intent = new Intent();
                intent.setClass(PushOrderActivity.this, PayActivity.class);
                intent.putExtra("orderNum", orderNum);
                intent.putExtra("ids", ids);
                intent.putExtra("totalPrice", totalPrice);
                startActivity(intent);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == ResultCode.RESULT_CODE_ADDRESS && requestCode == RequestCode.REQUEST_CODE_INHEAD) {
                mReceiveMemberInfoRl.setVisibility(View.VISIBLE);
                mAddressBean = (Data) data.getSerializableExtra("address");

                LogUtil.d(TAG, "addressBean --> " + JSON.toJSONString(mAddressBean));
                //姓名
                mNameTv.setText(mAddressBean.receiveName);
                //手机号
                mPhoneTv.setText(mAddressBean.receivePhone);
                //收货地址
                mAddressTv.setText(mAddressBean.location + mAddressBean.adress);

                //获取地址id，用于获取配送费
                String id = mAddressBean.id;
                requestShippingAccount(id);
            }
        }
    }

    /**
     * 获取配送费
     *
     * @param id
     */
    private void requestShippingAccount(String id) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.receiveAdressId = id;
        paramInfo.merchantCode = mMerchantCode;

        LogUtil.d(TAG, "paramInfo.receiveAdressId --> " + paramInfo.receiveAdressId);
        LogUtil.d(TAG, "paramInfo.merchantCode --> " + paramInfo.merchantCode);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestShippingAccount(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "配送费：" + JSON.toJSONString(response.data));

                double data = (double) response.data;
                mDeliveryFee = String.valueOf(data);
                //配送费
                mDeliveryFeeTv.setText("￥" + mDeliveryFee);

                //设置总金额
                mPriceTv.setText("￥" + (price + data));

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
