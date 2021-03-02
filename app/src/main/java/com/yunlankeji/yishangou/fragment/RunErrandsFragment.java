package com.yunlankeji.yishangou.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.MainActivity;
import com.yunlankeji.yishangou.activity.runerrands.CheckOrderActivity;
import com.yunlankeji.yishangou.activity.runerrands.ComingActivity;
import com.yunlankeji.yishangou.activity.runerrands.RunErrandsSendedActivity;
import com.yunlankeji.yishangou.activity.runerrands.SendAddressActivity;
import com.yunlankeji.yishangou.activity.runerrands.WaitActivity;
import com.yunlankeji.yishangou.adapter.RunErrandsDialogAdapter;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.globle.RequestCode;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.ListUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/21
 * Describe:跑腿
 */
public class RunErrandsFragment extends BaseFragment {

    private static final String TAG = "RunErrandsFragment";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_send_name_tv)
    TextView mSendNameTv;
    @BindView(R.id.m_send_phone_tv)
    TextView mSendPhoneTv;
    @BindView(R.id.m_send_address_tv)
    TextView mSendAddressTv;
    @BindView(R.id.m_receive_name_tv)
    TextView mReceiveNameTv;
    @BindView(R.id.m_receive_phone_tv)
    TextView mReceivePhoneTv;
    @BindView(R.id.m_receive_address_tv)
    TextView mReceiveAddressTv;
    @BindView(R.id.m_help_deliver_tv)
    TextView mHelpDeliverTv;
    @BindView(R.id.m_help_get_tv)
    TextView mHelpGetTv;
    @BindView(R.id.m_exchange_address_iv)
    ImageView mExchangeAddressIv;
    @BindView(R.id.m_good_rl)
    RelativeLayout mGoodRl;
    @BindView(R.id.m_good_tv)
    TextView mGoodTv;
    @BindView(R.id.m_remark_et)
    EditText mMarkEt;
    @BindView(R.id.m_delivery_fee_ll)
    LinearLayout mDeliveryFeeLl;
    @BindView(R.id.m_delivery_fee_tv)
    TextView mDeliveryFeeTv;
    @BindView(R.id.m_no_count_tv)
    TextView mNoCountTv;
    @BindView(R.id.m_had_one_order_rl)
    RelativeLayout mHadOneOrderRl;

    private int type;//0帮我送1帮我取
    private List<Data> runErrandsOrders;//进行中跑腿单

    private String sendLongitude;
    private String sendLatitude;
    private String sendArea;
    private String sendAddress;
    private String sendDoorNum;
    private String sendName;
    private String sendPhone;

    private String receiveLongitude;
    private String receiveLatitude;
    private String receiveArea;
    private String receiveAddress;
    private String receiveDoorNum;
    private String receiveName;
    private String receivePhone;

    private String longitude;
    private String latitude;
    private String area;
    private String address;
    private String doorNum;
    private String name;
    private String phone;
    private String shippingAccount;
    private String distance;

    private String orderNumber;
    private Data orderDetail;

    private List<String> goodList = Arrays.asList("食品饮料", "鲜花", "蛋糕", "文件", "水果生鲜", "证照证件", "数码家电", "服饰鞋帽", "其他");
    private List<String> valueList = Arrays.asList("50元以下", "50-100元", "100-500元", "500-1000元", "1000-5000元", "5000-10000元");
    private List<Data> goodTypes = new ArrayList<>();
    private List<Data> valueRoutes = new ArrayList<>();
    private String goodType;
    private String valueRoute;
    private int weight = 1;
    private String remark;
    private String good;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_run_errands;
    }

    @Override
    protected void initView() {
        super.initView();
        ConstantUtil.setStatusBar(getActivity(), mRootCl);
        mBackIv.setVisibility(View.GONE);
        mTitleTv.setText("跑腿");
    }

    @Override
    protected void initData() {
        super.initData();
        requestGetRunningErrands();
        for (int i = 0; i < goodList.size(); i++) {
            Data data = new Data();
            data.status = "0";
            data.goodsType = goodList.get(i);
            goodTypes.add(data);
        }
        for (int i = 0; i < valueList.size(); i++) {
            Data data = new Data();
            data.status = "0";
            data.goodsType = valueList.get(i);
            valueRoutes.add(data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestGetRunningErrands();
    }

    @OnClick({R.id.m_help_deliver_tv, R.id.m_help_get_tv, R.id.m_from_ll, R.id.m_to_ll, R.id.m_exchange_address_iv,
            R.id.m_good_rl, R.id.m_commit_tv, R.id.m_look_detail_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_help_deliver_tv://帮我送
                type = 0;
                mHelpDeliverTv.setTextColor(getActivity().getResources().getColor(R.color.color_F36C17));
                mHelpDeliverTv.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_white_left_10));
                mHelpGetTv.setTextColor(getActivity().getResources().getColor(R.color.color_333333));
                mHelpGetTv.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_gray_right_10));
                showSendView();
                showReceiveView();
                break;
            case R.id.m_help_get_tv://帮我取
                type = 1;
                mHelpDeliverTv.setTextColor(getActivity().getResources().getColor(R.color.color_333333));
                mHelpDeliverTv.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_gray_left_10));
                mHelpGetTv.setTextColor(getActivity().getResources().getColor(R.color.color_F36C17));
                mHelpGetTv.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_white_right_10));
                showSendView();
                showReceiveView();
                break;
            case R.id.m_from_ll://发
                intent.setClass(getActivity(), SendAddressActivity.class);
                intent.putExtra("title", "发货地址");
                intent.putExtra("doorNum", sendDoorNum);
                intent.putExtra("name", sendName);
                intent.putExtra("phone", sendPhone);
                intent.putExtra("area", sendArea);
                intent.putExtra("address", sendAddress);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_SEND_ADDRESS);
                break;
            case R.id.m_to_ll://收
                intent.setClass(getActivity(), SendAddressActivity.class);
                intent.putExtra("title", "收货地址");
                intent.putExtra("doorNum", receiveDoorNum);
                intent.putExtra("name", receiveName);
                intent.putExtra("phone", receivePhone);
                intent.putExtra("area", receiveArea);
                intent.putExtra("address", receiveAddress);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_RECEIVE_ADDRESS);
                break;
            case R.id.m_exchange_address_iv://交换
                latitude = sendLatitude;
                longitude = sendLongitude;
                area = sendArea;
                address = sendAddress;
                doorNum = sendDoorNum;
                name = sendName;
                phone = sendPhone;

                sendLatitude = receiveLatitude;
                sendLongitude = receiveLongitude;
                sendArea = receiveArea;
                sendAddress = receiveAddress;
                sendDoorNum = receiveDoorNum;
                sendName = receiveName;
                sendPhone = receivePhone;

                receiveLatitude = latitude;
                receiveLongitude = longitude;
                receiveArea = area;
                receiveAddress = address;
                receiveDoorNum = doorNum;
                receiveName = name;
                receivePhone = phone;

                showSendView();
                mSendPhoneTv.setText(sendPhone);

                showReceiveView();
                mReceivePhoneTv.setText(receivePhone);
                break;
            case R.id.m_good_rl://选择物品信息
                showRunErrandsDialog();
                break;
            case R.id.m_look_detail_tv://查看详情
                if (runErrandsOrders.size() == 1) {
                    // orderStatus 0待派单 1待接单 2待取货 3待配送 4待收货 5已完成 6已取消
                    if (orderDetail.orderStatus.equals("1")) {
                        doActivity(WaitActivity.class);
                    } else if (orderDetail.orderStatus.equals("2")) {
                        doActivity(ComingActivity.class, orderNumber, "orderNumber");
                    } else if (orderDetail.payStatus.equals("0")) {
                        doActivity(CheckOrderActivity.class, orderNumber, "orderNumber");
                    } else if (orderDetail.orderStatus.equals("3")) {
                        doActivity(ComingActivity.class, orderNumber, "orderNumber");
                    } else if (orderDetail.orderStatus.equals("4")) {
                        doActivity(RunErrandsSendedActivity.class, orderNumber, "orderNumber");
                    }
                } else {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity != null) {
                        mainActivity.showView(1);
                    }
                }
                break;
            case R.id.m_commit_tv://立即发单
                good = mGoodTv.getText().toString().trim();
                remark = mMarkEt.getText().toString().trim();
                if (TextUtils.isEmpty(sendAddress)) {
                    ToastUtil.showShort(type == 0 ? "请填写发货地址" : "请填写取货地址");
                } else if (TextUtils.isEmpty(receiveAddress)) {
                    ToastUtil.showShort("请填写收货地址");
                } else if (TextUtils.isEmpty(good)) {
                    ToastUtil.showShort("请选择物品");
                } else if (TextUtils.isEmpty(remark)) {
                    ToastUtil.showShort("请选择备注信息");
                } else {
                    //立即发单
                    requestCreateRunErrandsOrder();
                }
                break;
        }
    }

    /**
     * 显示跑腿物品的弹窗
     */
    private void showRunErrandsDialog() {
        Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_run_errands_good, null);

        //关闭图标
        ImageView negative = view.findViewById(R.id.negative);
        //确定
        TextView positive = view.findViewById(R.id.positive);
        //品类
        RecyclerView mGoodTypeRv = view.findViewById(R.id.m_good_type_rv);
        //价值区间
        RecyclerView mValueRouteRv = view.findViewById(R.id.m_value_route_rv);
        //重量-
        ImageView mLessWeightIv = view.findViewById(R.id.m_less_weight_iv);
        //重量+
        ImageView mAddWeightIv = view.findViewById(R.id.m_add_weight_iv);
        //重量
        TextView mWeightTv = view.findViewById(R.id.m_weight_tv);

        mWeightTv.setText(weight + "");

       /* mWeightEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                weight = Integer.parseInt(mWeightEt.getText().toString().trim());
                if (weight < 1) {
                    weight = 1;
                }
                if (weight > 25) {
                    weight = 25;
                }
                mWeightEt.setText(weight + "");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        mLessWeightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight--;
                if (weight < 1) {
                    weight = 1;
                }
                mWeightTv.setText(weight + "");
            }
        });

        mAddWeightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight++;
                if (weight > 25) {
                    weight = 25;
                }
                mWeightTv.setText(weight + "");
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(goodType)) {
                    ToastUtil.showShort("请选择品类");
                } else if (TextUtils.isEmpty(valueRoute)) {
                    ToastUtil.showShort("请选择价值");
                } else {
                    mGoodTv.setText(goodType + "/" + valueRoute + "/" + weight + "kg");
                    dialog.dismiss();
                    getShippingAccount();
                }
            }
        });

        RunErrandsDialogAdapter goodTypeAdapter = new RunErrandsDialogAdapter(getActivity());
        goodTypeAdapter.setItems(goodTypes);
        mGoodTypeRv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mGoodTypeRv.setAdapter(goodTypeAdapter);
        goodTypeAdapter.setOnItemClickListener(new RunErrandsDialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                goodType = goodTypes.get(position).goodsType;
                for (int i = 0; i < goodTypes.size(); i++) {
                    if (i == position) {
                        goodTypes.get(i).status = "1";
                    } else {
                        goodTypes.get(i).status = "0";
                    }
                }
                goodTypeAdapter.notifyDataSetChanged();
            }
        });

        RunErrandsDialogAdapter valueRouteAdapter = new RunErrandsDialogAdapter(getActivity());
        valueRouteAdapter.setItems(valueRoutes);
        mValueRouteRv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mValueRouteRv.setAdapter(valueRouteAdapter);

        valueRouteAdapter.setOnItemClickListener(new RunErrandsDialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                valueRoute = valueRoutes.get(position).goodsType;
                for (int i = 0; i < valueRoutes.size(); i++) {
                    if (i == position) {
                        valueRoutes.get(i).status = "1";
                    } else {
                        valueRoutes.get(i).status = "0";
                    }
                }
                valueRouteAdapter.notifyDataSetChanged();
            }
        });

        //弹窗点击周围空白处弹出层自动消失弹窗消失(false时为点击周围空白处弹出层不自动消失)
        dialog.setCanceledOnTouchOutside(true);
        //将布局设置给Dialog
        dialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();//显示对话框
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //监听弹窗消失
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == RequestCode.REQUEST_CODE_SEND_ADDRESS) {
                sendLatitude = data.getStringExtra("latitude");
                sendLongitude = data.getStringExtra("longitude");
                sendArea = data.getStringExtra("area");
                sendAddress = data.getStringExtra("address");
                sendDoorNum = data.getStringExtra("doorNum");
                sendName = data.getStringExtra("name");
                sendPhone = data.getStringExtra("phone");

                LogUtil.d(TAG, "sendLatitude --> " + sendLatitude);
                LogUtil.d(TAG, "sendLongitude --> " + sendLongitude);

                mSendNameTv.setText(sendName);
                mSendAddressTv.setText(sendAddress + sendArea + sendDoorNum);
                mSendPhoneTv.setVisibility(View.VISIBLE);
                mSendPhoneTv.setText(sendPhone);
                getShippingAccount();
            }
            if (requestCode == RequestCode.REQUEST_CODE_RECEIVE_ADDRESS) {
                receiveLatitude = data.getStringExtra("latitude");
                receiveLongitude = data.getStringExtra("longitude");
                receiveArea = data.getStringExtra("area");
                receiveAddress = data.getStringExtra("address");
                receiveDoorNum = data.getStringExtra("doorNum");
                receiveName = data.getStringExtra("name");
                receivePhone = data.getStringExtra("phone");

                LogUtil.d(TAG, "receiveLatitude --> " + receiveLatitude);
                LogUtil.d(TAG, "receiveLongitude --> " + receiveLongitude);

                mReceiveNameTv.setText(receiveName);
                mReceiveAddressTv.setText(receiveAddress + receiveArea + receiveDoorNum);
                mReceivePhoneTv.setVisibility(View.VISIBLE);
                mReceivePhoneTv.setText(receivePhone);
                getShippingAccount();
            }

        }
    }

    public void showSendView() {
        if (TextUtils.isEmpty(sendName)) {
            mSendNameTv.setText(type == 0 ? "从哪里发货?" : "从哪里取货?");
        } else {
            mSendNameTv.setText(sendName);
        }
        if (TextUtils.isEmpty(sendAddress)) {
            mSendAddressTv.setText(type == 0 ? "点击填写发货信息" : "点击填写取货信息");
        } else {
            mSendAddressTv.setText(sendAddress + sendArea + sendDoorNum);
        }
    }

    public void showReceiveView() {
        if (TextUtils.isEmpty(receiveName)) {
            mReceiveNameTv.setText(type == 0 ? "要寄到哪里?" : "要送到哪里?");
        } else {
            mReceiveNameTv.setText(receiveName);
        }
        if (TextUtils.isEmpty(receiveAddress)) {
            mReceiveAddressTv.setText(type == 0 ? "点击填写收货信息" : "点击填写收货信息");
        } else {
            mReceiveAddressTv.setText(receiveAddress + receiveArea + receiveDoorNum);
        }
    }

    public void getShippingAccount() {
        good = mGoodTv.getText().toString().trim();
        if (TextUtils.isEmpty(sendAddress) || TextUtils.isEmpty(receiveAddress) || TextUtils.isEmpty(good)) {
            return;
        }
        requestGetDistributionCash();
    }

    /**
     * 获取跑腿配送费
     */
    private void requestGetDistributionCash() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.receiveLongitude = receiveLongitude;
        paramInfo.receiveLatitude = receiveLatitude;
        paramInfo.sendLongitude = sendLongitude;
        paramInfo.sendLatitude = sendLatitude;
        paramInfo.goodsType = goodType;
        paramInfo.goodsCost = valueRoute;
        paramInfo.weight = weight + "";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestGetDistributionCash(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "骑手信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    shippingAccount = data.shippingAccount;
                    distance = data.distance;
                    if (!TextUtils.isEmpty(data.shippingAccount)) {
                        mDeliveryFeeLl.setVisibility(View.VISIBLE);
                        mNoCountTv.setVisibility(View.GONE);
                        mDeliveryFeeTv.setText(data.shippingAccount);
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

    /**
     * 立即发单
     */
    private void requestCreateRunErrandsOrder() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.memberName = Global.memberName;
        paramInfo.shippingAccount = shippingAccount;
        paramInfo.distance = distance;
        paramInfo.sendType = type + "";
        paramInfo.receiveName = receiveName;
        paramInfo.receivePhone = receivePhone;
        paramInfo.receiveAdress = receiveAddress + receiveArea + receiveDoorNum;
        paramInfo.receiveLongitude = receiveLongitude;
        paramInfo.receiveLatitude = receiveLatitude;
        paramInfo.sendName = sendName;
        paramInfo.sendPhone = sendPhone;
        paramInfo.sendAdress = sendAddress + sendArea + sendDoorNum;
        paramInfo.sendLongitude = sendLongitude;
        paramInfo.sendLatitude = sendLatitude;
        paramInfo.goodsType = goodType;
        paramInfo.goodsCost = valueRoute;
        paramInfo.weight = weight + "";

        LogUtil.d(TAG, "sendLongitude --> " + sendLongitude);
        LogUtil.d(TAG, "sendLatitude --> " + sendLatitude);
        LogUtil.d(TAG, "shippingAccount --> " + shippingAccount);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddRunningErrands(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "骑手信息：" + JSON.toJSONString(response.data));
                doActivity(WaitActivity.class);
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
     * 获取订单状态
     */
    private void requestGetRunningErrands() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestGetRunningErrands(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "跑腿订单信息：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (!ListUtil.isListEmpty(data)) {
                    mHadOneOrderRl.setVisibility(View.VISIBLE);
                    runErrandsOrders = data;
                    if (data.size() == 1) {
                        orderNumber = data.get(0).orderNumber;
                        requestQueryOrderDetail();
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

    /**
     * 订单详情
     */
    private void requestQueryOrderDetail() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = orderNumber;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryOrderDetail(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "订单详情：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    orderDetail = data;
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
