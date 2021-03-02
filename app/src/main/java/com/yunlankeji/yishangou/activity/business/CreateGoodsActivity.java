package com.yunlankeji.yishangou.activity.business;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.interior.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hwangjr.rxbus.RxBus;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.dialog.ChoiceDialog;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.globle.RequestCode;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.DataCleanManager;
import com.yunlankeji.yishangou.utils.FileUtil;
import com.yunlankeji.yishangou.utils.Glide4Engine;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.ZLBusAction;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import top.zibin.luban.Luban;

/**
 * Create by Snooker on 2020/12/30
 * Describe:
 */
public class CreateGoodsActivity extends BaseActivity {
    private static final String TAG = "CreateGoodsActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_sold_out_rl)
    RelativeLayout mSoldOutRl;//是否告罄
    @BindView(R.id.m_delete_goods_tv)
    TextView mDeleteGoodsTv;//删除商品
    @BindView(R.id.m_goods_name_et)
    EditText mGoodsNameEt;//
    @BindView(R.id.m_good_price_et)
    EditText mGoodPriceEt;//
    @BindView(R.id.m_specification_et)
    EditText mSpecificationEt;//
    @BindView(R.id.m_goods_stock_et)
    EditText mGoodsStockEt;//
    @BindView(R.id.m_goods_category_tv)
    TextView mGoodsCategoryTv;//
    @BindView(R.id.m_cover_pic_fl)
    FrameLayout mCoverPicFl;//
    @BindView(R.id.m_cover_pic_iv)
    ImageView mCoverPicIv;//
    @BindView(R.id.m_detail_pic_fl)
    FrameLayout mDetailPicFl;//
    @BindView(R.id.m_detail_pic_iv)
    ImageView mDetailPicIv;//
    @BindView(R.id.m_sure_tv)
    TextView mSureTv;//
    @BindView(R.id.m_sold_out_switch)
    Switch mSoldOutSwitch;//

    private String from;
    private ArrayList<String> items = new ArrayList<>();//分类文字集合
    private ArrayList<Data> categoryItems = new ArrayList<>();//分类集合
    private int type;
    private int count = 1;
    private List<String> pathList = new ArrayList<>();
    private static final String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory() + "/yishangou/goods/";
    private File compressedImage;
    private String baseEncode;
    private String goodsCoverPicUrl;
    private String goodsDetailPicUrl;
    private String mGoodsName;
    private String mGoodsPrice;
    private String mGoodsSpecification;
    private String mGoodsStock;
    private String mGoodsCategory;
    private String categoryCode;
    private Data goods;
    private String id;

    @Override
    public int setLayout() {
        return R.layout.activity_create_goods;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        from = getIntent().getStringExtra("from");
        goods = (Data) getIntent().getSerializableExtra("goods");
        id = getIntent().getStringExtra("id");
    }

    @Override
    public void initData() {
        //获取商品分类
        requestMerchantCategoryList();

        if (from != null) {
            if (from.equals("add")) {
                //新增商品
                mTitleTv.setText("新增商品");
                mSoldOutRl.setVisibility(View.GONE);
                //删除商品
                mDeleteGoodsTv.setVisibility(View.INVISIBLE);
            } else if (from.equals("edit")) {
                //商品详情
                mTitleTv.setText("商品详情");
                mSoldOutRl.setVisibility(View.VISIBLE);
                //删除商品
                mDeleteGoodsTv.setVisibility(View.VISIBLE);

                if (goods != null) {
                    //商品名称
                    mGoodsNameEt.setText(goods.productName);
                    //商品价格
                    mGoodPriceEt.setText(goods.price);
                    //商品规格
                    mSpecificationEt.setText(goods.sku);
                    //商品库存
                    mGoodsStockEt.setText(goods.stock);
                    //商品分类
                    mGoodsCategoryTv.setText(goods.categoryName);
                    //商品分类编码
                    categoryCode = goods.categoryCode;
                    //是否告罄
                    String isSaleOut = goods.isSaleOut;
                    if (isSaleOut.equals("1")) {
                        mSoldOutSwitch.setChecked(true);
                    } else {
                        mSoldOutSwitch.setChecked(false);
                    }
                    //封面图
                    goodsCoverPicUrl = goods.productLogo;
                    Glide.with(CreateGoodsActivity.this)
                            .load(goods.productLogo)
                            .into(mCoverPicIv);
                    //详情图
                    goodsDetailPicUrl = goods.detail;
                    Glide.with(CreateGoodsActivity.this)
                            .load(goods.detail)
                            .into(mDetailPicIv);
                }
            }
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_goods_category_tv, R.id.m_cover_pic_fl, R.id.m_detail_pic_fl, R.id.m_sure_tv,
            R.id.m_delete_goods_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_goods_category_tv://商品分类
                //弹窗选择商品分类的弹窗
                showCategoryDialog();
                break;
            case R.id.m_cover_pic_fl:
                type = 1;
                startChooseImage();
                break;
            case R.id.m_detail_pic_fl:
                type = 2;
                startChooseImage();
                break;
            case R.id.m_sure_tv://保存商品
                //商品名称
                mGoodsName = mGoodsNameEt.getText().toString();
                //商品价格
                mGoodsPrice = mGoodPriceEt.getText().toString();
                //商品规格
                mGoodsSpecification = mSpecificationEt.getText().toString();
                //商品库存
                mGoodsStock = mGoodsStockEt.getText().toString();
                //商品分类
                mGoodsCategory = mGoodsCategoryTv.getText().toString();

                if (TextUtils.isEmpty(mGoodsName)) {
                    ToastUtil.showShort("请输入商品名称");
                } else if (TextUtils.isEmpty(mGoodsPrice)) {
                    ToastUtil.showShort("请输入商品价格");
                } else if (TextUtils.isEmpty(mGoodsSpecification)) {
                    ToastUtil.showShort("请输入商品规格");
                } else if (TextUtils.isEmpty(mGoodsStock)) {
                    ToastUtil.showShort("请输入商品库存");
                } else if (TextUtils.isEmpty(mGoodsCategory)) {
                    ToastUtil.showShort("请输入商品分类");
                } else {
                    if (from != null) {
                        if (from.equals("add")) {
                            //保存商品
                            requestAddMerchantProduct();
                        } else if (from.equals("edit")) {
                            //修改
                            requestUpdateMerchantProduct();
                        }
                    }
                }
                break;
            case R.id.m_delete_goods_tv://删除商品
                showDeleteProductDialog();

                break;
        }
    }

    /**
     * 修改商品
     */
    private void requestUpdateMerchantProduct() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        paramInfo.merchantCode = Global.merchantCode;
        paramInfo.merchantName = Global.merchantName;
        paramInfo.productName = mGoodsName;
        paramInfo.categoryCode = categoryCode;
        paramInfo.stock = mGoodsStock;
        paramInfo.categoryName = mGoodsCategory;
        paramInfo.productLogo = goodsCoverPicUrl;
        paramInfo.detail = goodsDetailPicUrl;
        paramInfo.price = mGoodsPrice;
        paramInfo.sku = mGoodsSpecification;
        if (mSoldOutSwitch.isChecked()) {
            paramInfo.isSaleOut = "1";
        } else {
            paramInfo.isSaleOut = "0";
        }

        LogUtil.d(TAG, "paramInfo.merchantCode --> " + paramInfo.merchantCode);
        LogUtil.d(TAG, "paramInfo.merchantName --> " + paramInfo.merchantName);
        LogUtil.d(TAG, "paramInfo.productName --> " + paramInfo.productName);
        LogUtil.d(TAG, "paramInfo.categoryCode --> " + paramInfo.categoryCode);
        LogUtil.d(TAG, "paramInfo.categoryName --> " + paramInfo.categoryName);
        LogUtil.d(TAG, "paramInfo.productLogo --> " + paramInfo.productLogo);
        LogUtil.d(TAG, "paramInfo.price --> " + paramInfo.price);
        LogUtil.d(TAG, "paramInfo.sku --> " + paramInfo.sku);
        LogUtil.d(TAG, "paramInfo.isSaleOut --> " + paramInfo.isSaleOut);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMerchantProduct(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "修改商品：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("修改商品成功");

                RxBus.get().post(ZLBusAction.Refresh_Merchant_Goods, "Refresh_Merchant_Goods");
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

    private void showDeleteProductDialog() {
        DeleteDialog tagDialog = new DeleteDialog(CreateGoodsActivity.this);
        tagDialog.setCaption("删除商品")
                .setMessage("是否确定删除此商品？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        //删除商品
                        requestDeleteMerchantProduct();
                    }
                }).show();
    }

    /**
     * 删除商品
     */
    private void requestDeleteMerchantProduct() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestDeleteMerchantProduct(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "删除商品：" + JSON.toJSONString(response.data));
                RxBus.get().post(ZLBusAction.Refresh_Merchant_Goods, "Refresh_Merchant_Goods");
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
     * 保存商品
     */
    private void requestAddMerchantProduct() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = Global.merchantCode;
        paramInfo.merchantName = Global.merchantName;
        paramInfo.productName = mGoodsName;
        paramInfo.categoryCode = categoryCode;
        paramInfo.stock = mGoodsStock;
        paramInfo.categoryName = mGoodsCategory;
        paramInfo.productLogo = goodsCoverPicUrl;
        paramInfo.detail = goodsDetailPicUrl;
        paramInfo.price = mGoodsPrice;
        paramInfo.sku = mGoodsSpecification;
        paramInfo.isSaleOut = "0";

        LogUtil.d(TAG, "paramInfo.merchantCode --> " + paramInfo.merchantCode);
        LogUtil.d(TAG, "paramInfo.merchantName --> " + paramInfo.merchantName);
        LogUtil.d(TAG, "paramInfo.productName --> " + paramInfo.productName);
        LogUtil.d(TAG, "paramInfo.categoryCode --> " + paramInfo.categoryCode);
        LogUtil.d(TAG, "paramInfo.categoryName --> " + paramInfo.categoryName);
        LogUtil.d(TAG, "paramInfo.productLogo --> " + paramInfo.productLogo);
        LogUtil.d(TAG, "paramInfo.price --> " + paramInfo.price);
        LogUtil.d(TAG, "paramInfo.sku --> " + paramInfo.sku);
        LogUtil.d(TAG, "paramInfo.isSaleOut --> " + paramInfo.isSaleOut);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddMerchantProduct(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "保存商品：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("保存商品成功");

                RxBus.get().post(ZLBusAction.Refresh_Merchant_Goods, "Refresh_Merchant_Goods");
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
     * 相册
     */
    private void startChooseImage() {
        Matisse.from(this)
                //MimeType.ofImage()：选择图片 MimeType.ofVideo()：//选择视频  MimeType.ofAll()：//选择视频和图片
                .choose(MimeType.ofImage())
                //下面两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.yunlankeji.yishangou.provider", "/yishangou/cut/"))
//                .captureStrategy(new CaptureStrategy(true, "com.yunlankeji.water.provider", "/water/capture_images"))
                //设置最大选择张数
                .maxSelectable(count)
                //选择方向
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                //界面中缩略图的质量
                .thumbnailScale(0.85f)
                //Glide加载方式
                .imageEngine(new Glide4Engine())
                .theme(R.style.Matisse_Dracula)//暗黑模式  R.style.Matisse_Zhihu：蓝色主题
                .forResult(RequestCode.REQUEST_CODE_ADD_GOODS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == RequestCode.REQUEST_CODE_ADD_GOODS) {
                pathList = Matisse.obtainPathResult(data);
                //压缩图片
                compressImage(pathList);
            }
        }
    }

    /**
     * 压缩图片，相册选择
     *
     * @param uris
     */
    private void compressImage(List<String> uris) {
        try {
            List<File> files =
                    Luban.with(BaseApplication.getAppContext()).load(uris).setTargetDir(ConstantUtil.getImagePath()).get();
            if (files != null && files.size() > 0) {
                for (File f : files) {
                    File compressedImg = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(IMAGE_FILE_PATH)
                            .compressToFile(f);
                    compressedImage = compressedImg;
                    baseEncode = ConstantUtil.getBase64Str(compressedImage);
                    requestUploadFile();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传图片
     */
    private void requestUploadFile() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.Base64 = baseEncode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestUploadImage(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "上传图片：" + JSON.toJSONString(response));
                Data data = (Data) response.data;
                String imageUrl = data.obj;
                if (!TextUtils.isEmpty(imageUrl)) {
                    if (type == 1) {
                        goodsCoverPicUrl = imageUrl;
                        Glide.with(CreateGoodsActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mCoverPicIv);
                    } else if (type == 2) {
                        goodsDetailPicUrl = imageUrl;
                        Glide.with(CreateGoodsActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mDetailPicIv);
                    }
                }
                FileUtil.deleteFile(compressedImage);
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showLongForNet(msg);
                LogUtil.d(TAG, msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showLongForNet(msg);
                LogUtil.d(TAG, msg);
            }
        });
    }

    /**
     * 弹窗选择商品分类的弹窗
     */
    private void showCategoryDialog() {
        ChoiceDialog choicePhoneBodyDialog = new ChoiceDialog(CreateGoodsActivity.this);
        choicePhoneBodyDialog.setTitle("选择商品分类");
        choicePhoneBodyDialog.initData(items);
        choicePhoneBodyDialog.setOnChooseListener(new ChoiceDialog.OnChooseListener() {
            @Override
            public void onClick(String type, int position) {
                mGoodsCategoryTv.setText(type);
                categoryCode = categoryItems.get(position).categoryCode;
            }

        });
        choicePhoneBodyDialog.showAtLocation(mGoodsCategoryTv, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 获取商铺分类
     */
    private void requestMerchantCategoryList() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = Global.merchantCode;

        LogUtil.d(TAG, "paramInfo.merchantCode --> " + paramInfo.merchantCode);

        Call<ResponseBean<List<Data>>> call =
                NetWorkManager.getInstance().getRequest().requestMerchantCategoryList(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商品分类：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (data != null && data.size() > 0) {
                    categoryItems.addAll(data);
                    for (Data datum : data) {
                        items.add(datum.categoryName);
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

}
