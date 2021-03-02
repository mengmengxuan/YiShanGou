package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.business.BusinessHostActivity;
import com.yunlankeji.yishangou.activity.business.VerifyResultActivity;
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

/**
 * Create by Snooker on 2020/12/29
 * Describe:骑手入驻
 */
public class RiderSettleActivity extends BaseActivity {
    private static final String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory() + "/yishangou/head/";
    private static final String TAG = "RiderSettleActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.iv_positive)
    ImageView ivPositive;//
    @BindView(R.id.iv_other_side)
    ImageView ivOtherSide;//
    @BindView(R.id.m_rider_health_certificate_iv)
    ImageView mRiderHealthCertificateIv;//
    @BindView(R.id.m_rider_real_name_et)
    EditText mRiderRealNameEt;//
    @BindView(R.id.m_rider_phone_et)
    EditText mRiderPhoneEt;//
    @BindView(R.id.m_rider_id_card_num_et)
    EditText mRiderIdCardNumEt;//

    private int type;
    private int count = 1;
    private List<String> pathList = new ArrayList<>();
    private File compressedImage;
    private String baseEncode;
    private String idCardForward;
    private String idCardBackground;
    private String healthCard;
    private String mRiderRealName;
    private String mRiderPhone;
    private String mRiderIdCardNum;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_settler;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("骑手入驻");

    }

    @Override
    public void initData() {
        requestQueryRider();
    }

    /**
     * 获取骑手信息，取出拒绝原因
     */
    private void requestQueryRider() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryRider(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "骑手信息：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;
                if(data!=null) {
                    //姓名
                    mRiderRealNameEt.setText(data.realName);
                    //手机号
                    mRiderPhoneEt.setText(data.phone);
                    //身份证号
                    mRiderIdCardNumEt.setText(data.idCardNo);
                    //身份证正面照
                    idCardForward = data.idCardFrontUrl;
                    Glide.with(RiderSettleActivity.this)
                            .load(data.idCardFrontUrl)
                            .into(ivPositive);

                    //身份证背面照
                    idCardBackground = data.idCardReverseUrl;
                    Glide.with(RiderSettleActivity.this)
                            .load(data.idCardReverseUrl)
                            .into(ivOtherSide);

                    //健康证照片
                    healthCard = data.healthUrl;
                    Glide.with(RiderSettleActivity.this)
                            .load(data.healthUrl)
                            .into(mRiderHealthCertificateIv);
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

    @OnClick({R.id.m_back_iv, R.id.m_commit_tv, R.id.iv_positive, R.id.iv_other_side,
            R.id.m_rider_health_certificate_iv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
          /*  case R.id.m_commit_tv:
                intent.setClass(this, VerifyResultActivity.class);
                startActivity(intent);
                break;*/
            case R.id.iv_positive:
                type = 1;
                startChooseImage();
                break;
            case R.id.iv_other_side:
                type = 2;
                startChooseImage();
                break;
            case R.id.m_rider_health_certificate_iv:
                startChooseImage();
                type = 3;
                break;
            case R.id.m_commit_tv://立即申请
                //真实姓名
                mRiderRealName = mRiderRealNameEt.getText().toString();
                //手机号
                mRiderPhone = mRiderPhoneEt.getText().toString();
                //身份证号
                mRiderIdCardNum = mRiderIdCardNumEt.getText().toString();
                if (TextUtils.isEmpty(mRiderRealName)) {
                    ToastUtil.showShort("请输入真实姓名");
                } else if (TextUtils.isEmpty(mRiderPhone)) {
                    ToastUtil.showShort("请输入手机号码");
                } else if (TextUtils.isEmpty(mRiderIdCardNum)) {
                    ToastUtil.showShort("请输入身份证号");
                } else if (!Utils.isIdCard(mRiderIdCardNum)) {
                    ToastUtil.showShort("身份证号格式不对");
                }else if (TextUtils.isEmpty(idCardForward)) {
                    ToastUtil.showShort("请上传身份证正面");
                } else if (TextUtils.isEmpty(idCardBackground)) {
                    ToastUtil.showShort("请上传身份证反面");
                } else if (TextUtils.isEmpty(healthCard)) {
                    ToastUtil.showShort("请上传健康证");
                }else {
                    //骑手入驻
                    requestAddRider();
                }
                break;
        }
    }

    /**
     * 骑手入驻
     */
    private void requestAddRider() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.realName = mRiderRealName;
        paramInfo.phone = mRiderPhone;
        paramInfo.idCardNo = mRiderIdCardNum;
        paramInfo.idCardFrontUrl = idCardForward;
        paramInfo.idCardReverseUrl = idCardBackground;
        paramInfo.healthUrl = healthCard;

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddRider(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "骑手入驻" + JSON.toJSONString(response.data));
                ToastUtil.showShort("注册成功，我们会尽快为您审核！");

                Intent intent = new Intent();
                intent.setClass(RiderSettleActivity.this, VerifyResultActivity.class);
                intent.putExtra("from", "2");
                intent.putExtra("page", "rider");
                startActivity(intent);
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
                        idCardForward = imageUrl;
                        Glide.with(RiderSettleActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(ivPositive);
                    } else if (type == 2) {
                        idCardBackground = imageUrl;
                        Glide.with(RiderSettleActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(ivOtherSide);
                    } else if (type == 3) {
                        healthCard = imageUrl;
                        Glide.with(RiderSettleActivity.this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(mRiderHealthCertificateIv);
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

}
