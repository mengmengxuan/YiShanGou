package com.yunlankeji.yishangou.activity.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.GoodsAdapter;
import com.yunlankeji.yishangou.adapter.GoodsCategoryAdapter;
import com.yunlankeji.yishangou.adapter.SelectedGoodsAdapter;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/28
 * Describe:店铺详情
 */
public class StoreDetailActivity extends BaseActivity {
    private static final String TAG = "StoreDetailActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_merchant_logo_iv)
    ImageView mMerchantLogoIv;
    @BindView(R.id.m_merchant_name_tv)
    TextView mMerchantNameTv;
    @BindView(R.id.m_monthly_sale_tv)
    TextView mMonthlySaleTv;
    @BindView(R.id.m_same_city_delivery_tv)
    TextView mSameCityDeliveryTv;
    @BindView(R.id.m_distance_tv)
    TextView mDistanceTv;
    @BindView(R.id.m_goods_category_rv)
    RecyclerView mGoodsCategoryRv;
    @BindView(R.id.m_goods_rv)
    RecyclerView mGoodsRv;
    @BindView(R.id.m_shopping_car_iv)
    ImageView mShoppingCarIv;
    @BindView(R.id.m_goods_count_tv)
    TextView mGoodsCountTv;
    @BindView(R.id.m_price_tv)
    TextView mPriceTv;
    @BindView(R.id.m_delivery_fee_tv)
    TextView mDeliveryFeeTv;
    @BindView(R.id.m_settle_tv)
    TextView mSettleTv;
    @BindView(R.id.m_store_info_rl)
    RelativeLayout mStoreInfoRl;
    @BindView(R.id.m_busy_tv)
    TextView mBusyTv;
    @BindView(R.id.m_shopping_car_rl)
    RelativeLayout mShoppingCarRl;
    private GoodsCategoryAdapter mGoodsCategoryAdapter;
    private List<Data> goodsCategoryItems = new ArrayList<>();//分类数据源
    private GoodsAdapter mGoodsAdapter;
    private List<Data> goodsItems = new ArrayList<>();//商品数据源
    private String mMerchantCode;
    private String mCategoryCode;
    private int count;
    private String mSelectedNum;
    private List<Data> mCartList = new ArrayList<>();
    private String mPrice;
    private SelectedGoodsAdapter mSelectedGoodsAdapter;
    private TextView mDialogSelectedGoodsCountTv;
    private TextView mDialogClearShoppingCarTv;
    private RecyclerView mDialogSelectedGoodsRv;
    private ImageView mDialogShoppingCarIv;
    private TextView mDialogGoodsCountTv;
    private TextView mDialogPriceTv;
    private TextView mDialogDeliveryFeeTv;
    private TextView mDialogSettleTv;
    private double mStartFee;
    private String mDeliveryFee;
    private String mMerchantName;
    private String ids;
    private TextView mDialogBusyTv;
    private String isBusy;
    private String orderNumber;
    private String mPackingMoney;

    @Override
    public int setLayout() {
        return R.layout.activity_store_detail;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("店铺详情");
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mBackIv.setImageResource(R.mipmap.icon_arrow_white_left);
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_F36C17));

        mMerchantCode = getIntent().getStringExtra("merchantCode");
        orderNumber = getIntent().getStringExtra("orderNumber");

        //判断是否是当前城市
        Boolean isLocationCity = (Boolean) SPUtils.get(this, "isLocationCity", false);
        if (isLocationCity) {
            //选择的城市是当前城市
            //结算按钮
            mSettleTv.setClickable(true);
            mSettleTv.setFocusable(true);
            mSettleTv.setFocusableInTouchMode(true);

            //购物车按钮
            mShoppingCarRl.setClickable(true);
            mShoppingCarRl.setFocusable(true);
            mShoppingCarRl.setFocusableInTouchMode(true);
        } else {
            //结算按钮
            mSettleTv.setClickable(false);
            mSettleTv.setFocusable(false);
            mSettleTv.setFocusableInTouchMode(false);

            //购物车按钮
            mShoppingCarRl.setClickable(false);
            mShoppingCarRl.setFocusable(false);
            mShoppingCarRl.setFocusableInTouchMode(false);
        }

        //分类适配器
        mGoodsCategoryAdapter = new GoodsCategoryAdapter(this);
        mGoodsCategoryRv.setLayoutManager(new LinearLayoutManager(this));
        mGoodsCategoryAdapter.setItems(goodsCategoryItems);
        mGoodsCategoryRv.setAdapter(mGoodsCategoryAdapter);
        mGoodsCategoryAdapter.setOnItemClickedListener(new GoodsCategoryAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(View view, int position) {
                for (int i = 0; i < goodsCategoryItems.size(); i++) {
                    if (i == position) {
                        goodsCategoryItems.get(i).status = "1";
                        //点击的条目的分类编码
                        mCategoryCode = goodsCategoryItems.get(i).categoryCode;
                        //获取该分类下的商品
                        if (goodsItems != null) {
                            goodsItems.clear();
                        }
                        requestMerchantProduct();
                    } else {
                        goodsCategoryItems.get(i).status = "0";
                    }
                }
                mGoodsCategoryAdapter.notifyDataSetChanged();
            }
        });

        //商品适配器
        mGoodsAdapter = new GoodsAdapter(this);
        mGoodsRv.setLayoutManager(new LinearLayoutManager(this));
        mGoodsAdapter.setItems(goodsItems);
        mGoodsRv.setAdapter(mGoodsAdapter);
        mGoodsAdapter.setOnItemClickedListener(new GoodsAdapter.OnItemClickedListener() {
            /**
             * 条目点击事件
             * @param view
             * @param position
             */
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(StoreDetailActivity.this, FoodDetailActivity.class);
                intent.putExtra("id", goodsItems.get(position).id);
                intent.putExtra("from", "storeDetail");
                startActivity(intent);
            }

            /**
             * 加号点击事件
             * @param view
             * @param position
             */
            @Override
            public void onPlusClicked(View view, int position) {
                //往购物车里添加商品
                //获取点击的商品的数量
                String num = goodsItems.get(position).num;
                //获取productCode
                String productCode = goodsItems.get(position).productCode;

                if (TextUtils.isEmpty(num)) {
                    num = "0";
                }
                count = Integer.parseInt(num);
                count++;
                goodsItems.get(position).num = count + "";
                mGoodsAdapter.notifyDataSetChanged();

                //更新购物车
                requestUpdateShopCart(productCode);
            }

            /**
             * 减号点击事件
             * @param view
             * @param position
             */
            @Override
            public void onLessClicked(View view, int position) {
                //从购物车里删除
                //先获取点击的商品数量
                String num = goodsItems.get(position).num;
                //获取productCode
                String productCode = goodsItems.get(position).productCode;

                if (TextUtils.isEmpty(num)) {
                    num = "0";
                }

                count = Integer.parseInt(num);
                count--;
                goodsItems.get(position).num = count + "";
                mGoodsAdapter.notifyDataSetChanged();

                //更新购物车
                requestUpdateShopCart(productCode);
            }
        });
    }

    @Override
    public void initData() {
        //获取商家信息
        requestMerchant();

        //获取商品分类
        requestMerchantCategory();

        //获取购物车数据
        requestShopCartData();

        //获取配送费
        requestShippingAccount();

        if (orderNumber != null) {
            //再来一单，获取购物车数据
            requestBuyOrderAgain();
        }
    }

    /**
     * 再来一单，获取购物车数据
     */
    private void requestBuyOrderAgain() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = orderNumber;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestBuyOrderAgain(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "再来一单，获取购物车数据：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;
                if (data != null) {
                    //已选数量
                    mSelectedNum = data.num;
                    //价格
                    mPrice = data.price;
                    //选择的商品列表
                    if (mCartList != null) {
                        mCartList.clear();
                    }
                    mCartList = data.cartList;
                    if (mSelectedGoodsAdapter != null) {
                        mSelectedGoodsAdapter.notifyDataSetChanged();
                    }

                    //购物车数量
                    if (TextUtils.isEmpty(data.num) || data.num.equals("0")) {
                        mGoodsCountTv.setVisibility(View.GONE);
                        //价格
                        mPriceTv.setText("暂无商品");
                        mPriceTv.setTextColor(getResources().getColor(R.color.color_999999));

                    } else {
                        mGoodsCountTv.setVisibility(View.VISIBLE);
                        mGoodsCountTv.setText(data.num);

                        //价格
                        if (TextUtils.isEmpty(data.price) || data.price.equals("0.0")) {
                            mPriceTv.setText("￥0");
                        } else {
                            mPriceTv.setText("￥" + data.price);

                            //结算按钮
                            if (Double.parseDouble(data.price) > mStartFee) {
                                //总价大于起送费，显示去结算
                                mSettleTv.setText("去结算");
                                mSettleTv.setBackgroundResource(R.drawable.shape_orange_rect_16);
                            } else {
                                mSettleTv.setText("差￥" + (mStartFee - Double.parseDouble(data.price)) + "起送");
                            }

                        }
                        mPriceTv.setTextColor(getResources().getColor(R.color.color_F36C17));

                    }

                    //购物车图标
                    if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
                        Glide.with(StoreDetailActivity.this)
                                .load(R.mipmap.icon_shoppingcar_nogoods)
                                .into(mShoppingCarIv);
                    } else {
                        Glide.with(StoreDetailActivity.this)
                                .load(R.mipmap.icon_shopping_car)
                                .into(mShoppingCarIv);
                    }

                    //购物车ids
                    ids = data.ids;

                    //是否忙碌
                    isBusy = data.isBusy;
                    if (isBusy.equals("1")) {
                        mBusyTv.setVisibility(View.VISIBLE);
                    } else {
                        mBusyTv.setVisibility(View.GONE);
                    }

                    //弹窗中的数据渲染
                    //已选数量
                    if (mDialogSelectedGoodsCountTv != null) {
                        mDialogSelectedGoodsCountTv.setText("（共" + mSelectedNum + "件商品）");
                    }

                    //购物车图标
                    if (mDialogShoppingCarIv != null) {
                        if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
                            Glide.with(StoreDetailActivity.this)
                                    .load(R.mipmap.icon_shoppingcar_nogoods)
                                    .into(mDialogShoppingCarIv);
                        } else {
                            Glide.with(StoreDetailActivity.this)
                                    .load(R.mipmap.icon_shopping_car)
                                    .into(mDialogShoppingCarIv);
                        }
                    }

                    //购物车数量
                    if (mDialogGoodsCountTv != null) {
                        if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
                            mDialogGoodsCountTv.setVisibility(View.GONE);
                            //价格
                            mDialogPriceTv.setText("暂无商品");
                            mDialogPriceTv.setTextColor(getResources().getColor(R.color.color_999999));
                        } else {
                            mDialogGoodsCountTv.setVisibility(View.VISIBLE);
                            mDialogGoodsCountTv.setText(mSelectedNum);

                            //价格
                            if (mDialogPriceTv != null) {
                                if (TextUtils.isEmpty(mPrice) || mPrice.equals("0.0")) {
                                    mDialogPriceTv.setText("￥0");
                                } else {
                                    mDialogPriceTv.setText("￥" + mPrice);

                                    //结算按钮
                                    if (Double.parseDouble(data.price) > mStartFee) {
                                        //总价大于起送费，显示去结算
                                        mDialogSettleTv.setText("去结算");
                                        mDialogSettleTv.setBackgroundResource(R.drawable.shape_orange_rect_16);
                                    } else {
                                        mDialogSettleTv.setText("差￥" + (mStartFee - Double.parseDouble(data.price)) + "起送");
                                    }

                                }
                                mDialogPriceTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                            }

                        }
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
     * 更新购物车
     *
     * @param productCode
     */
    private void requestUpdateShopCart(String productCode) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.productCode = productCode;
        paramInfo.num = count + "";
        paramInfo.memberCode = Global.memberCode;

        LogUtil.d(TAG, "paramInfo.productCode --> " + paramInfo.productCode);
        LogUtil.d(TAG, "paramInfo.num --> " + paramInfo.num);
        LogUtil.d(TAG, "paramInfo.memberCode --> " + paramInfo.memberCode);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateShopCart(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "更新购物车：" + JSON.toJSONString(response.data));

                //获取购物车数据
                requestShopCartData();

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
     * 获取购物车数据
     */
    private void requestShopCartData() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.merchantCode = mMerchantCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestShopCartData(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "购物车：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;
                if (data != null) {
                    //已选数量
                    mSelectedNum = data.num;
                    //价格
                    mPrice = data.price;
                    //选择的商品列表
                    if (mCartList != null) {
                        mCartList.clear();
                        mCartList.addAll(data.cartList);
                    }
                    if (mSelectedGoodsAdapter != null) {
                        mSelectedGoodsAdapter.notifyDataSetChanged();
                    }

                    //购物车数量
                    if (TextUtils.isEmpty(data.num) || data.num.equals("0")) {
                        mGoodsCountTv.setVisibility(View.GONE);
                        //价格
                        mPriceTv.setText("暂无商品");
                        mPriceTv.setTextColor(getResources().getColor(R.color.color_999999));

                    } else {
                        mGoodsCountTv.setVisibility(View.VISIBLE);
                        mGoodsCountTv.setText(data.num);

                        //价格
                        if (TextUtils.isEmpty(data.price) || data.price.equals("0.0")) {
                            mPriceTv.setText("￥0");
                        } else {
                            mPriceTv.setText("￥" + data.price);

                            //结算按钮
                            if (Double.parseDouble(data.price) > mStartFee) {
                                //总价大于起送费，显示去结算
                                mSettleTv.setText("去结算");
                                mSettleTv.setBackgroundResource(R.drawable.shape_orange_rect_16);
                            } else {
                                mSettleTv.setText("差￥" + (mStartFee - Double.parseDouble(data.price)) + "起送");
                            }

                        }
                        mPriceTv.setTextColor(getResources().getColor(R.color.color_F36C17));

                    }

                    //购物车图标
                    if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
                        Glide.with(StoreDetailActivity.this)
                                .load(R.mipmap.icon_shoppingcar_nogoods)
                                .into(mShoppingCarIv);
                    } else {
                        Glide.with(StoreDetailActivity.this)
                                .load(R.mipmap.icon_shopping_car)
                                .into(mShoppingCarIv);
                    }

                    //购物车ids
                    ids = data.ids;

                    //是否忙碌
                    isBusy = data.isBusy;
                    if (isBusy.equals("1")) {
                        mBusyTv.setVisibility(View.VISIBLE);

                    } else {
                        mBusyTv.setVisibility(View.GONE);
                    }

                    //弹窗中的数据渲染
                    //已选数量
                    if (mDialogSelectedGoodsCountTv != null) {
                        mDialogSelectedGoodsCountTv.setText("（共" + mSelectedNum + "件商品）");
                    }

                    //购物车图标
                    if (mDialogShoppingCarIv != null) {
                        if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
                            Glide.with(StoreDetailActivity.this)
                                    .load(R.mipmap.icon_shoppingcar_nogoods)
                                    .into(mDialogShoppingCarIv);
                        } else {
                            Glide.with(StoreDetailActivity.this)
                                    .load(R.mipmap.icon_shopping_car)
                                    .into(mDialogShoppingCarIv);
                        }
                    }

                    //购物车数量
                    if (mDialogGoodsCountTv != null) {
                        if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
                            mDialogGoodsCountTv.setVisibility(View.GONE);
                            //价格
                            mDialogPriceTv.setText("暂无商品");
                            mDialogPriceTv.setTextColor(getResources().getColor(R.color.color_999999));
                        } else {
                            mDialogGoodsCountTv.setVisibility(View.VISIBLE);
                            mDialogGoodsCountTv.setText(mSelectedNum);

                            //价格
                            if (mDialogPriceTv != null) {
                                if (TextUtils.isEmpty(mPrice) || mPrice.equals("0.0")) {
                                    mDialogPriceTv.setText("￥0");
                                } else {
                                    mDialogPriceTv.setText("￥" + mPrice);

                                    //结算按钮
                                    if (Double.parseDouble(data.price) > mStartFee) {
                                        //总价大于起送费，显示去结算
                                        mDialogSettleTv.setText("去结算");
                                        mDialogSettleTv.setBackgroundResource(R.drawable.shape_orange_rect_16);
                                    } else {
                                        mDialogSettleTv.setText("差￥" + (mStartFee - Double.parseDouble(data.price)) + "起送");
                                    }

                                }
                                mDialogPriceTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                            }
                        }
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
     * 根据分类获取商品信息
     */
    private void requestMerchantProduct() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.categoryCode = mCategoryCode;
        paramInfo.merchantCode = mMerchantCode;
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<List<Data>>> call =
                NetWorkManager.getInstance().getRequest().requestMerchantProduct(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商品列表：" + JSON.toJSONString(response.data));

                List<Data> data = (List<Data>) response.data;

                if (data != null && data.size() > 0) {
                    goodsItems.addAll(data);
                    mGoodsAdapter.notifyDataSetChanged();
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
     * 获取配送费
     */
    private void requestShippingAccount() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.merchantCode = mMerchantCode;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestShippingAccount(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "配送费：" + JSON.toJSONString(response.data));

                double data = (double) response.data;
                mDeliveryFee = String.valueOf(data);
                //配送费
                if (mDeliveryFeeTv != null) {
                    mDeliveryFeeTv.setText("配送费￥" + mDeliveryFee);
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
     * 获取商品分类
     */
    private void requestMerchantCategory() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = mMerchantCode;
        Call<ResponseBean<List<Data>>> call =
                NetWorkManager.getInstance().getRequest().requestMerchantCategory(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商品分类：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;

                if (data != null && data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        if (i == 0) {
                            data.get(i).status = "1";
                            //获取分类编码
                            mCategoryCode = data.get(i).categoryCode;
                            //根据分类获取商品数据
                            requestMerchantProduct();
                        } else {
                            data.get(i).status = "0";
                        }
                    }
                    goodsCategoryItems.addAll(data);
                    mGoodsCategoryAdapter.notifyDataSetChanged();
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
     * 获取商家信息
     */
    private void requestMerchant() {
        showLoading();
        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = mMerchantCode;
        paramInfo.latitude = latitude;
        paramInfo.longitude = longitude;

        LogUtil.d(TAG, "  paramInfo.merchantCode --> " + paramInfo.merchantCode);

        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商家信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                //商铺logo
                Glide.with(StoreDetailActivity.this)
                        .load(data.merchantLogo)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                        .into(mMerchantLogoIv);

                //商铺名称
                mMerchantName = data.merchantName;
                mMerchantNameTv.setText(data.merchantName);

                //月销
                if (TextUtils.isEmpty(data.saleCount)) {
                    mMonthlySaleTv.setText("月销 0");
                } else {
                    mMonthlySaleTv.setText("月销" + data.saleCount);
                }

                //距离
                Boolean isLocationCity = (Boolean) SPUtils.get(StoreDetailActivity.this, "isLocationCity", false);
                if (isLocationCity) {
                    mDistanceTv.setText(data.distance + "km");
                } else {
                    mDistanceTv.setText("超出配送范围");
                }

                //打包费
                mPackingMoney = data.packingMoney;

                /*//配送费
                if (TextUtils.isEmpty(data.shippingAccount)) {
                    mDeliveryFeeTv.setText("无配送费");
                } else {
                    mDeliveryFeeTv.setText("配送费￥" + data.shippingAccount);
                }

                //弹窗中的配送费
                if (mDialogDeliveryFeeTv != null) {
                    if (TextUtils.isEmpty(data.startingFee)) {
                        mDialogDeliveryFeeTv.setText("￥ 0起送");
                    } else {
                        mDialogDeliveryFeeTv.setText("￥ " + data.startingFee + "起送");
                    }
                }*/

                //起送费
                if (TextUtils.isEmpty(data.startingFee)) {
                    mSettleTv.setText("￥ 0起送");
                    //取出起送费
                    mStartFee = 0.0;
                } else {
                    mSettleTv.setText("￥ " + data.startingFee + "起送");
                    //取出起送费
                    mStartFee = Double.parseDouble(data.startingFee);
                }

                //弹窗中的起送费
                if (mDialogSettleTv != null) {
                    if (TextUtils.isEmpty(data.startingFee)) {
                        mDialogSettleTv.setText("￥ 0起送");
                    } else {
                        mDialogSettleTv.setText("￥ " + data.startingFee + "起送");
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

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Pay_Success)})
    public void paySuccess(String status) {
        if ("Pay_Success".equals(status)) {
            finish();
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_shopping_car_rl, R.id.m_store_info_rl, R.id.m_settle_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_store_info_rl://点击商家信息
                intent.setClass(this, BusinessInformationActivity.class);
                intent.putExtra("merchantCode", mMerchantCode);
                startActivity(intent);
                break;
            case R.id.m_shopping_car_rl://点击购物车按钮
                //弹出购物车
                if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
                    ToastUtil.showShort("暂无商品");
                } else {
                    showShoppingCar();
                }
                break;
            case R.id.m_settle_tv://点击结算
                intent.setClass(this, PushOrderActivity.class);
                intent.putExtra("carList", (Serializable) mCartList);
                intent.putExtra("deliveryFee", mDeliveryFee);
                intent.putExtra("merchantCode", mMerchantCode);
                intent.putExtra("merchantName", mMerchantName);
                intent.putExtra("packingMoney", mPackingMoney);
                intent.putExtra("ids", ids);
                startActivity(intent);
                break;
        }
    }

    /**
     * 弹出购物车
     */
    private void showShoppingCar() {
        Dialog dialog = new Dialog(StoreDetailActivity.this, R.style.CustomDialog);
        View view = LayoutInflater.from(StoreDetailActivity.this).inflate(R.layout.dialog_shopping_car, null);

        //左上角商品件数
        mDialogSelectedGoodsCountTv = view.findViewById(R.id.m_dialog_selected_goods_count_tv);
        //清空购物车
        mDialogClearShoppingCarTv = view.findViewById(R.id.m_dialog_clear_shopping_car_tv);
        //已选商品列表
        mDialogSelectedGoodsRv = view.findViewById(R.id.m_dialog_selected_goods_rv);
        //购物车图标
        mDialogShoppingCarIv = view.findViewById(R.id.m_dialog_shopping_car_iv);
        //购物车数量
        mDialogGoodsCountTv = view.findViewById(R.id.m_dialog_goods_count_tv);
        //价格
        mDialogPriceTv = view.findViewById(R.id.m_dialog_price_tv);
        //忙碌
        mDialogBusyTv = view.findViewById(R.id.m_dialog_busy_tv);
        //配送费
        mDialogDeliveryFeeTv = view.findViewById(R.id.m_dialog_delivery_fee_tv);
        //结算
        mDialogSettleTv = view.findViewById(R.id.m_dialog_settle_tv);

        //已选商品
        mSelectedGoodsAdapter = new SelectedGoodsAdapter(this);
        mDialogSelectedGoodsRv.setLayoutManager(new LinearLayoutManager(this));
        mSelectedGoodsAdapter.setItems(mCartList);
        mDialogSelectedGoodsRv.setAdapter(mSelectedGoodsAdapter);
        mSelectedGoodsAdapter.setOnItemClickedListener(new SelectedGoodsAdapter.OnItemClickedListener() {
            /**
             * 加号点击事件
             *
             * @param view
             * @param position
             */
            @Override
            public void onPlusItemClicked(View view, int position) {
                //往购物车里添加商品
                //获取点击的商品的数量
                String num = mCartList.get(position).num;
                //获取productCode
                String productCode = mCartList.get(position).productCode;

                if (TextUtils.isEmpty(num)) {
                    num = "0";
                }
                count = Integer.parseInt(num);
                count++;
                mCartList.get(position).num = count + "";
//                mSelectedGoodsAdapter.notifyDataSetChanged();

                //更新购物车
                requestUpdateShopCart(productCode);
            }

            /**
             * 减号点击事件
             *
             * @param view
             * @param position
             */
            @Override
            public void onLessItemClicked(View view, int position) {
                //从购物车里删除
                //先获取点击的商品数量
                String num = mCartList.get(position).num;
                //获取productCode
                String productCode = mCartList.get(position).productCode;

                if (TextUtils.isEmpty(num)) {
                    num = "0";
                }

                count = Integer.parseInt(num);
                count--;
                mCartList.get(position).num = count + "";
//                mSelectedGoodsAdapter.notifyDataSetChanged();

                //更新购物车
                requestUpdateShopCart(productCode);
            }
        });

        //已选数量
        mDialogSelectedGoodsCountTv.setText("（共" + mSelectedNum + "件商品）");

        //清空购物车
        mDialogClearShoppingCarTv.setOnClickListener(v -> {
            //调用清空购物车接口
            requestDeleteShopCart(dialog);
        });

        //购物车图标
        if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
            Glide.with(StoreDetailActivity.this)
                    .load(R.mipmap.icon_shoppingcar_nogoods)
                    .into(mDialogShoppingCarIv);
        } else {
            Glide.with(StoreDetailActivity.this)
                    .load(R.mipmap.icon_shopping_car)
                    .into(mDialogShoppingCarIv);
        }

        //购物车数量
        if (TextUtils.isEmpty(mSelectedNum) || mSelectedNum.equals("0")) {
            mDialogGoodsCountTv.setVisibility(View.GONE);
        } else {
            mDialogGoodsCountTv.setVisibility(View.VISIBLE);
            mDialogGoodsCountTv.setText(mSelectedNum);
        }

        //配送费
        if (TextUtils.isEmpty(mDeliveryFee) || mDeliveryFee.equals("0.0")) {
            mDialogDeliveryFeeTv.setText("无配送费");
        } else {
            mDialogDeliveryFeeTv.setText("配送费￥" + mDeliveryFee);
        }

        //价格
        if (TextUtils.isEmpty(mPrice) || mPrice.equals("0.0")) {
            mDialogPriceTv.setText("暂无商品");
            mDialogPriceTv.setTextColor(getResources().getColor(R.color.color_999999));
        } else {
            mDialogPriceTv.setText("￥" + mPrice);
            mDialogPriceTv.setTextColor(getResources().getColor(R.color.color_F36C17));
        }

        //是否忙碌
        if (isBusy.equals("1")) {
            mDialogBusyTv.setVisibility(View.VISIBLE);
        } else {
            mDialogBusyTv.setVisibility(View.GONE);
        }

        //结算按钮
        if (Double.parseDouble(mPrice) > mStartFee) {
            //总价大于起送费，显示去结算
            mDialogSettleTv.setText("去结算");
            mDialogSettleTv.setBackgroundResource(R.drawable.shape_orange_rect_16);
        } else {
            mDialogSettleTv.setText("差￥" + (mStartFee - Double.parseDouble(mPrice)) + "起送");
        }

        //结算
        mDialogSettleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StoreDetailActivity.this, PushOrderActivity.class);
                intent.putExtra("carList", (Serializable) mCartList);
                intent.putExtra("deliveryFee", mDeliveryFee);
                intent.putExtra("merchantCode", mMerchantCode);
                intent.putExtra("merchantName", mMerchantName);
                intent.putExtra("ids", ids);
                startActivity(intent);
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
        wlp.height = ConstantUtil.dip2px(430);
        window.setAttributes(wlp);

        dialog.show();//显示对话框

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //监听弹窗消失
            }
        });
    }

    /**
     * 清空购物车
     *
     * @param dialog
     */
    private void requestDeleteShopCart(Dialog dialog) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.ids = ids;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestDeleteShopCart(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "清空购物车：" + JSON.toJSONString(response.data));
                dialog.dismiss();

                //获取商家信息
                requestMerchant();

                if (goodsCategoryItems != null) {
                    goodsCategoryItems.clear();
                }
                //获取商品分类
                requestMerchantCategory();

                if (goodsItems != null) {
                    goodsItems.clear();
                }
                //获取购物车数据
                requestShopCartData();
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
            }
        });

    }
}
