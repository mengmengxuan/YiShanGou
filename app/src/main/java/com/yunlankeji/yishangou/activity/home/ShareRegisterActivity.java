package com.yunlankeji.yishangou.activity.home;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.personal.baseutils.utils.Utils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.dialog.SavePicDialog;
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
import com.yunlankeji.yishangou.utils.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/7
 * Describe:分享推广拿佣金页面
 */
public class ShareRegisterActivity extends BaseActivity {

    private static final String TAG = "ShareRegisterActivity";

    /**
     * 文本类型
     */
    public static int TEXT = 0;

    /**
     * 图片类型
     */
    public static int DRAWABLE = 1;

    @BindView(R.id.m_root_rl)
    RelativeLayout mRootRl;
    @BindView(R.id.m_title_ll)
    LinearLayout mTitleLl;
    @BindView(R.id.m_share_reward_ll)
    LinearLayout m_share_reward_ll;
    @BindView(R.id.m_share_tv)
    TextView m_share_tv;
    @BindView(R.id.m_share_desc_tv)
    TextView m_share_desc_tv;
    @BindView(R.id.m_qr_code_iv)
    ImageView m_qr_code_iv;
    @BindView(R.id.m_share_wechat_ll)
    LinearLayout m_share_wechat_ll;
    private boolean QrCode;
    String filePath = Environment.getExternalStorageDirectory() + "/yishangou/qrcode/";//用于存储我的推广码二维码
    private UMImage umImage;

    @Override
    public int setLayout() {
        return R.layout.activity_share_register;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mTitleLl);

        //分享推广拿佣金背景图
        mRootRl.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(ShareRegisterActivity.this)
                        .asDrawable()
                        .load(R.mipmap.bg_share_register)
                        .into(new SimpleTarget<Drawable>(mRootRl.getWidth() / 2, mRootRl.getHeight() / 2) {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                mRootRl.setBackground(resource);
                            }
                        });
            }
        });

        //分享奖励背景
        m_share_reward_ll.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(ShareRegisterActivity.this)
                        .asDrawable()
                        .load(R.mipmap.bg_share_reward)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                m_share_reward_ll.setBackground(resource);
                            }
                        });
            }
        });
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
                        m_qr_code_iv.setImageBitmap(BitmapFactory.decodeFile(filePath));
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

    @OnClick({R.id.m_back_iv, R.id.m_share_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_share_tv:
                try {
                    Bitmap bmp = ConstantUtil.getBitmap(ShareRegisterActivity.this, m_share_wechat_ll);
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
        if (!packageName.isEmpty() && !isAvilible(ShareRegisterActivity.this, packageName)) {// 判断APP是否存在
            Toast.makeText(ShareRegisterActivity.this, "请先安装" + appname, Toast.LENGTH_SHORT)
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

    @OnLongClick({R.id.m_qr_code_iv})
    public void onViewLongClicked(View view) {
        switch (view.getId()) {
            case R.id.m_qr_code_iv:
                //弹出保存图片弹窗
                showSavePicDialog();
                break;
        }
    }

    /**
     * 弹出保存图片弹窗
     */
    private void showSavePicDialog() {
        SavePicDialog savePicDialog = new SavePicDialog(ShareRegisterActivity.this);
        savePicDialog.showAtLocation(m_qr_code_iv, Gravity.BOTTOM,
                Utils.dip2px(getApplicationContext(), 9),
                Utils.dip2px(getApplicationContext(), 0));
        savePicDialog.setOnStatusChangedListener(new SavePicDialog.OnStatusChangedListener() {
            @Override
            public void onStatusChanged(View view) {
                Log.d(TAG, "onStatusChanged: " + view.getId());
                switch (view.getId()) {
                    case R.id.m_save_tv:
                        try {
                            Bitmap bitmap = ConstantUtil.getBitmap(ShareRegisterActivity.this, m_qr_code_iv);
                            ConstantUtil.onSaveBitmap(bitmap, ShareRegisterActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        savePicDialog.dismiss();
                        break;
                    case R.id.m_cancel_tv:
                        savePicDialog.dismiss();
                        break;
                }
            }
        });
    }
}
