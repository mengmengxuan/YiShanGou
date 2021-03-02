package com.yunlankeji.yishangou.activity.home;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.ImageAdapter;
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
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

//菜品详情页面
public class FoodDetailActivity extends BaseActivity {

    private static final String TAG = "FoodDetailActivity";
    private List<Data> imageList = new ArrayList<>();

    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.banner)
    ConvenientBanner banner;
    @BindView(R.id.m_food_name_tv)
    TextView mFoodNameTv;
    @BindView(R.id.m_had_sold_tv)
    TextView mHadSoldTv;
    @BindView(R.id.m_price_tv)
    TextView mPriceTv;
    @BindView(R.id.m_add_shopping_car_tv)
    TextView mAddShoppingCarTv;
    @BindView(R.id.m_goods_detail_iv)
    ImageView mGoodsDetailIv;

    private String id;
    private List<String> bannerItems = new ArrayList<>();//轮播图集合
    private String num;
    private String productCode;
    private String mMerchantCode;
    private String from;

    @Override
    public int setLayout() {
        return R.layout.activity_food_detail;
    }

    @Override
    protected void initView() {
//        ImageAdapter imageAdapter = new ImageAdapter(this);
//        imageAdapter.setItems(imageList);
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setVisibility(View.GONE);
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_transparent));

        id = getIntent().getStringExtra("id");
        from = getIntent().getStringExtra("from");

    }

    @Override
    public void initData() {
        //商品详情
        requestProductDetail();
    }

    private void requestProductDetail() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestProductDetail(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商品详情：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    //判断是否是选择的城市
                    if (from.equals("home")) {
                        String city = (String) SPUtils.get(FoodDetailActivity.this, "chooseCity", "");
                        LogUtil.d(TAG, "data.cityName --> " + data.cityName);
                        LogUtil.d(TAG, "city --> " + city);
                        if (data.cityName.contains(city)) {
                            //选择的城市是当前城市
                            mAddShoppingCarTv.setVisibility(View.VISIBLE);
                        } else {
                            mAddShoppingCarTv.setVisibility(View.GONE);
                        }
                    } else if (from.equals("storeDetail")) {
                        Boolean isLocationCity = (Boolean) SPUtils.get(FoodDetailActivity.this, "isLocationCity", false);
                        if (isLocationCity) {
                            //选择的城市是当前城市
                            mAddShoppingCarTv.setVisibility(View.VISIBLE);
                        } else {
                            mAddShoppingCarTv.setVisibility(View.GONE);
                        }
                    }

                    //取出商品编码
                    productCode = data.productCode;
                    //取出商家编号
                    mMerchantCode = data.merchantCode;

                    bannerItems.add(data.productLogo);
                    banner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                    banner.setPages(new CBViewHolderCreator<LocalImageHolderView>() {
                        @Override
                        public LocalImageHolderView createHolder() {
                            return new LocalImageHolderView();
                        }
                    }, bannerItems);

                    //商品名称
                    mFoodNameTv.setText(data.productName);
                    //月售
                    if (TextUtils.isEmpty(data.saleCount)) {
                        mHadSoldTv.setText("月售 0");
                    } else {
                        mHadSoldTv.setText("月售" + data.saleCount);
                    }
                    //价格
                    mPriceTv.setText("￥" + data.price);
                    //商品详情
//                    RichText.fromHtml(data.detail).into(mGoodsDetailTv);
                    Glide.with(FoodDetailActivity.this)
                            .load(data.detail)
                            .into(mGoodsDetailIv);

                    //数量
                    if (data.num != null) {
                        num = data.num;
                    } else {
                        num = "0";
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

    //为了方便改写，来实现复杂布局的切换
    private static class LocalImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) { //你可以通过layout文件来创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            Glide.with(context)
                    .load(data)
                    .into(imageView);
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_add_shopping_car_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_add_shopping_car_tv://加入购物车
                //加入购物车
                requestUpdateShopCart();
                break;
        }
    }

    /**
     * 加入购物车
     */
    private void requestUpdateShopCart() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.num = String.valueOf(Integer.parseInt(num) + 1);
        paramInfo.productCode = productCode;
        paramInfo.memberCode = Global.memberCode;

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateShopCart(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "加入购物车：" + JSON.toJSONString(response.data));

                //跳转到商铺详情页面
                Intent intent = new Intent();
                intent.setClass(FoodDetailActivity.this, StoreDetailActivity.class);
                intent.putExtra("merchantCode", mMerchantCode);
                startActivity(intent);
                finish();

               /* if (bannerItems != null) {
                    bannerItems.clear();
                }
                requestProductDetail();*/
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
