package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hwangjr.rxbus.RxBus;
import com.personal.baseutils.widget.GridViewForScrollView;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.UploadWechatReceivePaymentAdapter;
import com.yunlankeji.yishangou.globle.Global;
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
import com.yunlankeji.yishangou.utils.ZLBusAction;
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

import static com.yunlankeji.yishangou.globle.RequestCode.REQUEST_CODE_INVOICE;

/**
 * Create by Snooker on 2020/12/25
 * Describe:微信提现
 */
public class WechatWithdrawActivity extends BaseActivity {

    private static final String TAG = "WechatWithdrawActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.part_line)
    View partLine;//partLine
    @BindView(R.id.m_wechat_receive_payment_gv)
    GridViewForScrollView mWechatReceivePaymentGv;
    private UploadWechatReceivePaymentAdapter mUploadWechatReceivePaymentAdapter;
    private int count = 1;//最多选择4张图片
    private ArrayList<String> items = new ArrayList<>();//上传图片后，返回的url集合
    List<String> pathList = new ArrayList<>();//选中的所有图片和视频的路径
    private File cutImage, compressedImage;
    private String baseEncode;
    private String mFirstImageUrl;
    private String amount;
    private static final String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory() + "/yishangou/cut/";

    @Override
    public int setLayout() {
        return R.layout.activity_wechat;
    }

    @Override
    protected void initView() {

        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("微信");

        amount = getIntent().getStringExtra("amount");

        mUploadWechatReceivePaymentAdapter = new UploadWechatReceivePaymentAdapter(this);
        mUploadWechatReceivePaymentAdapter.setCount(count);
        mUploadWechatReceivePaymentAdapter.setItems(items);
        mUploadWechatReceivePaymentAdapter.setChoiceListener(new UploadWechatReceivePaymentAdapter.OnChooseListener() {
            @Override
            public void onChoose(int position, boolean isCheck) {
                if (isCheck) {
                    items.remove(position);
                    mUploadWechatReceivePaymentAdapter.setItems(items);
                    mUploadWechatReceivePaymentAdapter.notifyDataSetChanged();
                } else {
                    startChooseImage();
                }
            }
        });
        mWechatReceivePaymentGv.setAdapter(mUploadWechatReceivePaymentAdapter);

    }

    @OnClick({R.id.m_back_iv, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_commit_tv:
                if (TextUtils.isEmpty(mFirstImageUrl)) {
                    ToastUtil.showShort("请上传您的微信收款码");
                } else {
                    requestAddMemberCash();
                }
                break;
        }
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
                //设置最大选择张数
                .maxSelectable(count)
                //选择方向
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                //界面中缩略图的质量
                .thumbnailScale(0.85f)
                //Glide加载方式
                .imageEngine(new Glide4Engine())
                .theme(R.style.Matisse_Dracula)//暗黑模式  R.style.Matisse_Zhihu：蓝色主题
                .forResult(REQUEST_CODE_INVOICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_INVOICE) {
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
                    requestUploadFile(baseEncode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传头像
     */
    private void requestUploadFile(String baseEncode) {
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
//                ToastUtil.show("提交成功");
                items.clear();
                Data data = (Data) response.data;
                mFirstImageUrl = data.obj;
                items.add(mFirstImageUrl);
                mUploadWechatReceivePaymentAdapter.notifyDataSetChanged();
                FileUtil.deleteFile(compressedImage);

            }

            @Override
            public void onFailure(String msg) {
                LogUtil.d(TAG, msg);
                hideLoading();
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                LogUtil.d(TAG, msg);
//                ToastUtil.showShort(msg);
            }
        });

    }

    //提现
    private void requestAddMemberCash() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.memberName = Global.memberName;
        paramInfo.cashAccount = amount;
        paramInfo.cashType = "2";
        paramInfo.wxUrl = mFirstImageUrl;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddMemberCash(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "提现：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("提现成功");
                setResult(ResultCode.RESULT_CODE_WITHDRAW);

                RxBus.get().post(ZLBusAction.Refresh_Withdraw_Record, "Refresh_Withdraw_Record");
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

}
