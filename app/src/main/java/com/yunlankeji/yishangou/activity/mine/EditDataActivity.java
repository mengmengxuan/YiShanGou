package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.globle.RequestCode;
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
import com.yunlankeji.yishangou.utils.Util;
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

public class EditDataActivity extends BaseActivity {

    private static final String TAG = "EditDataActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_logo_rl)
    RelativeLayout mLogoRl;//更换头像
    @BindView(R.id.m_logo_iv)
    ImageView mLogoIv;//头像
    @BindView(R.id.m_nick_name_et)
    EditText mNickNameEt;//昵称
    @BindView(R.id.m_phone_et)
    EditText mPhoneEt;//电话

    private int count = 1;//图片最大选择张数
    List<String> pathList = new ArrayList<>();//选中的所有图片路径
    private File cutImage, compressedImage;
    private String baseEncode;
    private String headImage;
    private static final String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory() + "/yishangou/head/";
    private String mNickName;
    private String mPhone;

    @Override
    public int setLayout() {
        return R.layout.activity_edit_data;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("修改资料");
    }

    @Override
    public void initData() {
        super.initData();
        mNickNameEt.setText(Global.memberName);
        mPhoneEt.setText(Global.phone);
    }

    @OnClick({R.id.m_back_iv, R.id.m_logo_rl, R.id.m_sure_tv})
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_logo_rl:
                startChooseImage();
                break;
            case R.id.m_sure_tv://确认修改
                //昵称
                mNickName = mNickNameEt.getText().toString();
                //电话
                mPhone = mPhoneEt.getText().toString();

                if (TextUtils.isEmpty(mNickName)) {
                    ToastUtil.showShort("请输入昵称");
                } else if (TextUtils.isEmpty(mPhone)) {
                    ToastUtil.showShort("请输入电话");
                } else if (!Utils.isMobile(mPhone)) {
                    ToastUtil.showShort("手机号格式不正确");
                } else {
                    //修改个人资料
                    requestUpdateMemberInfo();
                }
                break;
        }
    }

    /**
     * 修改个人资料
     */
    private void requestUpdateMemberInfo() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = Global.id;
        paramInfo.logo = headImage;
        paramInfo.memberName = mNickName;
        paramInfo.phone = mPhone;

        LogUtil.d(TAG, "paramInfo.id --> " + paramInfo.id);
        LogUtil.d(TAG, "paramInfo.logo --> " + paramInfo.logo);
        LogUtil.d(TAG, "paramInfo.memberName --> " + paramInfo.memberName);
        LogUtil.d(TAG, "paramInfo.phone --> " + paramInfo.phone);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMemberInfo(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "修改个人资料：" + JSON.toJSONString(response.data));

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
                .captureStrategy(new CaptureStrategy(true, "com.yunlankeji.guangyin.provider", "/guangyin/cut/"))
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
                .forResult(RequestCode.REQUEST_CODE_INHEAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RequestCode.REQUEST_CODE_INHEAD) {
            if (data != null) {
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
     * 上传头像
     */
    private void requestUploadFile() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.Base64 = baseEncode;
        LogUtil.d(TAG, "paramInfo.Base64------------------：" + paramInfo.Base64);
//        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestUploadImage(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "上传图片：" + JSON.toJSONString(response));

                Data data = (Data) response.data;
                headImage = data.obj;

                if (!TextUtils.isEmpty(headImage)) {
                    Glide.with(EditDataActivity.this)
                            .load(headImage)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(mLogoIv);
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
