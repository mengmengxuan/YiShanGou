package com.yunlankeji.yishangou.activity.mine;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.home.ShareRegisterActivity;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.QRCodeUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class ShareActivity extends BaseActivity {

    private static final String TAG = "ShareActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_android_code_iv)
    ImageView mAndroidCodeIv;
    @BindView(R.id.m_ios_code_iv)
    ImageView mIosCodeIv;
    @BindView(R.id.m_share_rl)
    RelativeLayout m_share_rl;
    private boolean QrCode;
    String filePath = Environment.getExternalStorageDirectory() + "/yishangou/qrcode/";//用于存储我的推广码二维码

    @Override
    public int setLayout() {
        return R.layout.activity_share;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("邀请");

//        Bitmap androidCodeBitmap = createQRCodeBitmap("android");
//        Bitmap iosCodeBitmap = createQRCodeBitmap("ios");
//        mAndroidCodeIv.setImageBitmap(androidCodeBitmap);
//        mIosCodeIv.setImageBitmap(iosCodeBitmap);
    }

    @Override
    public void initData() {
        requestUpdateSystemVersion();
    }

    private void requestUpdateSystemVersion() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = "188";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestUpdateSystemVersion(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "二维码：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    String content =
                            data.propertyValue + Global.inviteCode;
                    Log.d(TAG, "initView: " + content);
                    QrCode = QRCodeUtil.createQRImage(getApplicationContext(), "black", content, Utils.dip2px(getApplicationContext(), 114), Utils.dip2px(getApplicationContext(), 114), null, filePath);
                    if (QrCode) {
                        mAndroidCodeIv.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        mIosCodeIv.setImageBitmap(BitmapFactory.decodeFile(filePath));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    @OnClick({R.id.m_back_iv, R.id.m_share_tv, R.id.m_android_download_tv, R.id.m_ios_download_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_android_download_tv://Android下载
                break;
            case R.id.m_ios_download_tv://ios下载
                break;
            case R.id.m_share_tv://分享到微信
                try {
                    Bitmap bmp = ConstantUtil.getBitmap(ShareActivity.this, m_share_rl);
                    shareWeChatFriend("分享推广", "扫码下载", ShareRegisterActivity.DRAWABLE, bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 分享到微信好友
     *
     * @param msgTitle (分享标题)
     * @param msgText  (分享内容)
     * @param type     (分享类型)
     * @param drawable (分享图片，若分享类型为AndroidShare.TEXT，则可以为null)
     */
    public void shareWeChatFriend(String msgTitle, String msgText, int type,
                                  Bitmap drawable) {
        shareMsg("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI", "微信",
                msgTitle, msgText, type, drawable);
    }

    /**
     * 点击分享的代码
     *
     * @param packageName  (包名,跳转的应用的包名)
     * @param activityName (类名,跳转的页面名称)
     * @param appname      (应用名,跳转到的应用名称)
     * @param msgTitle     (标题)
     * @param msgText      (内容)
     * @param type         (发送类型：text or pic 微信朋友圈只支持pic)
     */
    @SuppressLint("NewApi")
    private void shareMsg(String packageName, String activityName,
                          String appname, String msgTitle, String msgText, int type,
                          Bitmap drawable) {
        if (!packageName.isEmpty() && !isAvilible(ShareActivity.this, packageName)) {// 判断APP是否存在
            Toast.makeText(ShareActivity.this, "请先安装" + appname, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent intent = new Intent("android.intent.action.SEND");
        if (type == ShareRegisterActivity.TEXT) {
            intent.setType("text/plain");
        } else if (type == ShareRegisterActivity.DRAWABLE) {
            intent.setType("image/*");
            final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                    getContentResolver(), drawable, null, null));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!packageName.isEmpty()) {
            intent.setComponent(new ComponentName(packageName, activityName));
            startActivity(intent);
        } else {
            startActivity(Intent.createChooser(intent, msgTitle));
        }
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

}
