package com.yunlankeji.yishangou.activity.business;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.ChooseAreaActivity;
import com.yunlankeji.yishangou.activity.mine.RiderSettleActivity;
import com.yunlankeji.yishangou.dialog.ChoiceDialog;
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
import com.yunlankeji.yishangou.utils.FileUtil;
import com.yunlankeji.yishangou.utils.Glide4Engine;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import top.zibin.luban.Luban;

//商家入驻
public class BusinessHostActivity extends BaseActivity {

    private static final String TAG = "BusinessHostActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_id_card_forward_iv)
    ImageView mIdCardForwardIv;
    @BindView(R.id.m_id_card_background_iv)
    ImageView mIdCardBackgroundIv;
    @BindView(R.id.m_health_card_iv)
    ImageView mHealthCardIv;
    @BindView(R.id.m_merchant_licence_iv)
    ImageView mMerchantLicenceIv;
    @BindView(R.id.m_merchant_head_iv)
    ImageView mMerchantHeadIv;
    @BindView(R.id.m_real_name_et)
    EditText mRealNameEt;
    @BindView(R.id.m_phone_et)
    EditText mPhoneEt;
    @BindView(R.id.m_merchant_name_et)
    EditText mMerchantNameEt;
    @BindView(R.id.m_address_tv)
    TextView mAddressTv;
    @BindView(R.id.m_address_detail_et)
    EditText mAddressDetailEt;
    @BindView(R.id.m_id_card_num_et)
    EditText mIdCardNumEt;
    @BindView(R.id.m_merchant_type_tv)
    TextView mMerchantTypeTv;
    @BindView(R.id.m_sure_tv)
    TextView mSureTv;

    private int count = 1;//图片最大选择张数
    List<String> pathList = new ArrayList<>();//选中的所有图片路径
    private File cutImage, compressedImage;
    private String baseEncode;
    private String phone;
    private String merchantName;
    private String address;
    private String addressDetail;
    private String idCardNum;
    //5种图片url
    private String idCardForward;
    private String idCardBackground;
    private String healthCard;
    private String merchantLicence;
    private String merchantHead;

    private int type;//12345依次是身份证正反面、健康证、营业执照、店铺头像
    private static final String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory() + "/yishangou/head/";
    private String latitude;
    private String longitude;
    private String area;
    private String realName;
    private List<Data> categoryItems = new ArrayList<>();
    private ArrayList<String> items = new ArrayList<>();
    private String merchantTypeCode;
    private String mMerchantType;
    private String cityName;

    @Override
    public int setLayout() {
        return R.layout.activity_business_host;
    }

    @Override
    protected void initView() {

        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("商家入驻");
    }

    @Override
    public void initData() {
        //查询商家信息，用于回显
        requestQueryMyMerchant();
        Boolean isHost = getIntent().getBooleanExtra("isHost", false);
        if (isHost) {
            mSureTv.setText("确认修改");
            mTitleTv.setText("商家资料");
        }

        //获取分类
        requestHomeCategory();
    }

    /**
     * 获取分类
     */
    private void requestHomeCategory() {
        ParamInfo paramInfo = new ParamInfo();
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestHomeCategory(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "首页分类：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (data != null) {
                    categoryItems.addAll(data);

                    for (Data categoryItem : categoryItems) {
                        items.add(categoryItem.merchantTypeName);
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
     * 查询商家信息，用于回显
     */
    private void requestQueryMyMerchant() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryMyMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "商家信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    //取出5张图片的url
                    idCardForward = data.idCardFrontUrl;
                    idCardBackground = data.idCardReverseUrl;
                    healthCard = data.healthUrl;
                    merchantLicence = data.licenseUrl;
                    merchantHead = data.merchantLogo;

                    //取出经纬度
                    latitude = data.latitude;
                    longitude = data.longitude;

                    //法人真实姓名
                    mRealNameEt.setText(data.realName);
                    //手机号
                    mPhoneEt.setText(data.phone);
                    //店铺名
                    mMerchantNameEt.setText(data.merchantName);
                    //取出店铺类型编号
                    merchantTypeCode = data.merchantTypeCode;
                    //店铺类型
                    mMerchantTypeTv.setText(data.merchantTypeName);
                    //店铺地址
                    mAddressTv.setText(data.location);
                    //取出城市名
                    cityName = data.cityName;
                    //详细地址
                    mAddressDetailEt.setText(data.adress);
                    //身份证号
                    mIdCardNumEt.setText(data.idCardNo);
                    //身份证正面
                    Glide.with(BusinessHostActivity.this)
                            .load(data.idCardFrontUrl)
                            .into(mIdCardForwardIv);
                    //身份证背面
                    Glide.with(BusinessHostActivity.this)
                            .load(data.idCardReverseUrl)
                            .into(mIdCardBackgroundIv);
                    //健康证
                    Glide.with(BusinessHostActivity.this)
                            .load(data.healthUrl)
                            .into(mHealthCardIv);
                    //营业执照
                    Glide.with(BusinessHostActivity.this)
                            .load(data.licenseUrl)
                            .into(mMerchantLicenceIv);
                    //店铺头像
                    Glide.with(BusinessHostActivity.this)
                            .load(data.merchantLogo)
                            .into(mMerchantHeadIv);
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

    @OnClick({R.id.m_back_iv, R.id.m_address_tv, R.id.m_id_card_forward_iv, R.id.m_id_card_background_iv,
            R.id.m_health_card_iv, R.id.m_merchant_licence_iv, R.id.m_merchant_head_iv, R.id.m_sure_tv, R.id.m_merchant_type_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_merchant_type_tv:
                //弹窗选择商铺类型的弹窗
                showMerchantTypeDialog();
                break;
            case R.id.m_address_tv://店铺地址
                Intent intent = new Intent();
                intent.setClass(this, ChooseAreaActivity.class);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_BUSINESS_AREA);
                break;
            case R.id.m_id_card_forward_iv://身份证正面
                type = 1;
                startChooseImage();
                break;
            case R.id.m_id_card_background_iv://身份证反面
                type = 2;
                startChooseImage();
                break;
            case R.id.m_health_card_iv://健康证
                type = 3;
                startChooseImage();
                break;
            case R.id.m_merchant_licence_iv://营业执照
                type = 4;
                startChooseImage();
                break;
            case R.id.m_merchant_head_iv://店铺头像
                type = 5;
                startChooseImage();
                break;
            case R.id.m_sure_tv://立即申请
                realName = mRealNameEt.getText().toString().trim();
                phone = mPhoneEt.getText().toString().trim();
                merchantName = mMerchantNameEt.getText().toString().trim();
                address = mAddressTv.getText().toString().trim();
                addressDetail = mAddressDetailEt.getText().toString().trim();
                idCardNum = mIdCardNumEt.getText().toString().trim();
                mMerchantType = mMerchantTypeTv.getText().toString().trim();
                if (TextUtils.isEmpty(realName)) {
                    ToastUtil.showShort("请输入真实姓名");
                } else if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort("请输入手机号");
                } else if (!Utils.isMobile(phone)) {
                    ToastUtil.showShort("手机号格式不正确！");
                } else if (TextUtils.isEmpty(merchantName)) {
                    ToastUtil.showShort("请输入店铺名称");
                } else if (TextUtils.isEmpty(mMerchantType)) {
                    ToastUtil.showShort("请选择店铺类型");
                } else if (TextUtils.isEmpty(address)) {
                    ToastUtil.showShort("请选择店铺地址");
                } else if (TextUtils.isEmpty(addressDetail)) {
                    ToastUtil.showShort("请输入详细地址");
                } else if (TextUtils.isEmpty(idCardNum)) {
                    ToastUtil.showShort("请输入身份证号");
                } else if (!Utils.isIdCard(idCardNum)) {
                    ToastUtil.showShort("身份证号格式不对");
                } else if (TextUtils.isEmpty(idCardForward)) {
                    ToastUtil.showShort("请上传身份证正面");
                } else if (TextUtils.isEmpty(idCardBackground)) {
                    ToastUtil.showShort("请上传身份证反面");
                } else if (TextUtils.isEmpty(healthCard)) {
                    ToastUtil.showShort("请上传健康证");
                } else if (TextUtils.isEmpty(merchantLicence)) {
                    ToastUtil.showShort("请上传营业执照");
                } else if (TextUtils.isEmpty(merchantHead)) {
                    ToastUtil.showShort("请上传店铺头像");
                } else {
                    //商家入驻
                    requestAddMerchant();
                }
                break;
        }
    }

    /**
     * 弹出选择商铺类型的弹窗
     */
    private void showMerchantTypeDialog() {
        ChoiceDialog choicePhoneBodyDialog = new ChoiceDialog(BusinessHostActivity.this);
        choicePhoneBodyDialog.setTitle("选择店铺类型");
        choicePhoneBodyDialog.initData(items);
        choicePhoneBodyDialog.setOnChooseListener(new ChoiceDialog.OnChooseListener() {
            @Override
            public void onClick(String type, int position) {

                mMerchantTypeTv.setText(type);
                merchantTypeCode = categoryItems.get(position).merchantTypeCode;
            }

        });
        choicePhoneBodyDialog.showAtLocation(mMerchantTypeTv, Gravity.BOTTOM, 0, 0);
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
                .forResult(RequestCode.REQUEST_CODE_HOST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == RequestCode.REQUEST_CODE_HOST) {

                pathList = Matisse.obtainPathResult(data);
                //压缩图片
                compressImage(pathList);
            }

            if (requestCode == RequestCode.REQUEST_CODE_BUSINESS_AREA &&
                    resultCode == ResultCode.RESULT_CODE_ADDRESS_FROM_MAP) {
                //选择地点
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                area = data.getStringExtra("area");
                address = data.getStringExtra("address");
                cityName = data.getStringExtra("cityName");
                mAddressTv.setText(area + address);

                Log.d(TAG, "onActivityResult  latitude: " + latitude);
                Log.d(TAG, "onActivityResult  longtitude: " + longitude);
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
//        LogUtil.d(TAG, "paramInfo.Base64------------------：" + paramInfo.Base64);
//        paramInfo.memberCode = Global.memberCode;
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
                        idCardForward = imageUrl;
                        Glide.with(BusinessHostActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mIdCardForwardIv);
                    } else if (type == 2) {
                        idCardBackground = imageUrl;
                        Glide.with(BusinessHostActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mIdCardBackgroundIv);
                    } else if (type == 3) {
                        healthCard = imageUrl;
                        Glide.with(BusinessHostActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mHealthCardIv);
                    } else if (type == 4) {
                        merchantLicence = imageUrl;
                        Glide.with(BusinessHostActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mMerchantLicenceIv);
                    } else if (type == 5) {
                        merchantHead = imageUrl;
                        Glide.with(BusinessHostActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mMerchantHeadIv);
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
     * 商家入驻
     */
    private void requestAddMerchant() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.realName = realName;
        paramInfo.phone = phone;
        paramInfo.merchantName = merchantName;
        paramInfo.idCardNo = idCardNum;
        paramInfo.idCardFrontUrl = idCardForward;
        paramInfo.idCardReverseUrl = idCardBackground;
        paramInfo.healthUrl = healthCard;
        paramInfo.licenseUrl = merchantLicence;
        paramInfo.merchantLogo = merchantHead;
        paramInfo.merchantTypeName = mMerchantType;
        paramInfo.merchantTypeCode = merchantTypeCode;
        paramInfo.location = address;//店铺地址
        paramInfo.adress = addressDetail;//详细地址
        paramInfo.longitude = longitude;
        paramInfo.latitude = latitude;
        paramInfo.memberCode = Global.memberCode;
        paramInfo.cityName = cityName;

        LogUtil.d(TAG, "paramInfo.realName --> " + paramInfo.realName);
        LogUtil.d(TAG, "paramInfo.phone --> " + paramInfo.phone);
        LogUtil.d(TAG, "paramInfo.merchantName --> " + paramInfo.merchantName);
        LogUtil.d(TAG, "paramInfo.idCardNo --> " + paramInfo.idCardNo);
        LogUtil.d(TAG, "paramInfo.idCardFrontUrl --> " + paramInfo.idCardFrontUrl);
        LogUtil.d(TAG, "paramInfo.idCardReverseUrl --> " + paramInfo.idCardReverseUrl);
        LogUtil.d(TAG, "paramInfo.healthUrl --> " + paramInfo.healthUrl);
        LogUtil.d(TAG, "paramInfo.licenseUrl --> " + paramInfo.licenseUrl);
        LogUtil.d(TAG, "paramInfo.merchantLogo --> " + paramInfo.merchantLogo);
        LogUtil.d(TAG, "paramInfo.location --> " + paramInfo.location);
        LogUtil.d(TAG, "paramInfo.adress --> " + paramInfo.adress);
        LogUtil.d(TAG, "paramInfo.longitude --> " + paramInfo.longitude);
        LogUtil.d(TAG, "paramInfo.latitude --> " + paramInfo.latitude);
        LogUtil.d(TAG, "paramInfo.memberCode --> " + paramInfo.memberCode);
        LogUtil.d(TAG, "paramInfo.cityName --> " + paramInfo.cityName);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddMerchant(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商家入驻：" + JSON.toJSONString(response));
                Intent intent = new Intent();
                intent.setClass(BusinessHostActivity.this, VerifyResultActivity.class);
                intent.putExtra("from", "2");
                intent.putExtra("page", "business");
                finish();
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
}
