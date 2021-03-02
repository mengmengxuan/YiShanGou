package com.yunlankeji.yishangou.activity.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.utils.MD5Util;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.MainActivity;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.m_register_now_tv)
    TextView mRegisterNowTv;//立即注册按钮
    @BindView(R.id.m_phone_ll)
    LinearLayout mPhoneLl;
    @BindView(R.id.m_invite_code_ll)
    LinearLayout mInviteCodeLl;
    @BindView(R.id.m_message_code_ll)
    LinearLayout mMessageCodeLl;
    @BindView(R.id.m_password_ll)
    LinearLayout mPasswordLl;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题
    @BindView(R.id.m_login_code_tv)
    TextView mLoginCodeTv;//验证码登录按钮
    @BindView(R.id.m_login_password_tv)
    TextView mLoginPasswordTv;//密码登录按钮
    @BindView(R.id.m_phone_et)
    EditText mPhoneEt;//电话号码
    @BindView(R.id.m_invite_code_et)
    EditText mInviteCodeEt;//邀请码
    @BindView(R.id.m_message_code_et)
    EditText mMessageCodeEt;//短信验证码
    @BindView(R.id.m_password_et)
    EditText mPasswordEt;//密码
    @BindView(R.id.m_get_code_tv)
    TextView mGetCodeTv;//发送验证码按钮
    @BindView(R.id.m_eye_cb)
    CheckBox mEyecb;//眼睛按钮
    @BindView(R.id.m_login_tv)
    TextView mLoginTv;//登录按钮(注册)

    int leftTime = 60;//计时
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
    private String mVerifyCode;//验证码
    private int type = 0;//0密码登录1验证码登录2注册

    //获取短信验证码计时器
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            leftTime--;
            if (leftTime > 0) {
                mHandler.postDelayed(this, 1000);
                if (mGetCodeTv != null) {
                    mGetCodeTv.setText("剩余" + leftTime + "s");
                    mGetCodeTv.setTextColor(Color.GRAY);
                    mGetCodeTv.setClickable(false);
                    mGetCodeTv.setFocusable(false);
                    mGetCodeTv.setFocusableInTouchMode(false);
                }
            } else {
                if (mGetCodeTv != null) {
                    mGetCodeTv.setText("重新发送");
                    mGetCodeTv.setTextColor(getResources().getColor(R.color.color_F94040));
                    mGetCodeTv.setClickable(true);
                    mGetCodeTv.setFocusable(true);
                    mGetCodeTv.setFocusableInTouchMode(true);
                }
            }
        }
    };

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        Data userInfo = (Data) SPUtils.get(LoginActivity.this, "userInfo", null);

        if (userInfo != null) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //设置密码是否可见
        mEyecb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //从密码不可见模式变为密码可见模式
                    mPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //从密码可见模式变为密码不可见模式
                    mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mPasswordEt.setSelection(mPasswordEt.getText().length());
            }
        });
    }

    @OnClick({R.id.m_register_now_tv, R.id.m_login_code_tv, R.id.m_login_password_tv, R.id.m_get_code_tv, R.id.m_login_tv, R.id.m_agreement_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.m_register_now_tv://切换注册
                showView(2);
                break;
            case R.id.m_login_code_tv://切换验证码登录切换
                showView(1);
                break;
            case R.id.m_login_password_tv://切换密码登录
                showView(0);
                break;
            case R.id.m_get_code_tv://获取验证码
                String phone = mPhoneEt.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort("请输入手机号");
                } else if (!Utils.isMobile(phone)) {
                    ToastUtil.showShort("手机号格式不正确");
                } else if (type == 1) {
                    sendVerificationCode(phone, "1");
                } else if (type == 2) {
                    sendVerificationCode(phone, "0");
                }
                break;
            case R.id.m_login_tv://登录
                phone = mPhoneEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                String verifyCode = mMessageCodeEt.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort("请输入手机号");
                } else if (!Utils.isMobile(phone)) {
                    ToastUtil.showShort("手机号格式不正确");
                } else if (TextUtils.isEmpty(password) && (type == 0 || type == 2)) {
                    ToastUtil.showShort("请输入密码");
                } else if ((password.length() < 6) && (type == 0 || type == 2)) {
                    ToastUtil.showShort("密码长度不能小于6");
                } else if (TextUtils.isEmpty(verifyCode) && (type == 1 || type == 2)) {
                    ToastUtil.showShort("请输入验证码");
                } else if (type == 2) {
                    requestRegister(phone, password);
                } else {
                    requestLogin(phone, password);
                }
                break;
            case R.id.m_agreement_tv://隐私协议跳转
                Intent intent = new Intent();
                intent.setClass(this, ProtocolActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取验证码
     *
     * @param registerPhone
     */
    private void sendVerificationCode(String registerPhone, String type) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.phone = registerPhone;
        paramInfo.verificationType = type;
        LogUtil.d(TAG, paramInfo.phone);
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().sendVerificationCode(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
//                LogUtil.d(this, JSON.toJSONString(response.data));

                leftTime = 60;
                mHandler.postDelayed(runnable, 0);
                LogUtil.d(TAG, "请求成功");

                String data = (String) response.data;
                if (data != null) {
                    ToastUtil.showShort(data);
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

    //登录
    public void requestLogin(String phone, String password) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.phone = phone;
        if (type == 0) {
            paramInfo.password = MD5Util.encrypt(password);
        } else {
            paramInfo.code = mMessageCodeEt.getText().toString().trim() + "";
        }
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestLogin(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "登录成功：" + JSON.toJSONString(response.data));

                Data userInfo = (Data) response.data;

                SPUtils.put(LoginActivity.this, "userInfo", userInfo);

                ToastUtil.showShort("登录成功");
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
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

    //注册
    public void requestRegister(String phone, String password) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.phone = phone;
        paramInfo.code = mMessageCodeEt.getText().toString().trim() + "";
        paramInfo.password = MD5Util.encrypt(password);
        paramInfo.inviteCode = mInviteCodeEt.getText().toString().trim() + "";

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestRegister(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "请求成功");
                LogUtil.d(TAG, JSON.toJSONString(response.data));
                ToastUtil.showShort("注册成功");
                showView(0);
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

    //功能切换时控制view的显示与隐藏
    public void showView(int i) {
        if (i == 0) {
            type = 0;
            mTitleTv.setText("登录");
            mInviteCodeLl.setVisibility(View.GONE);
            mMessageCodeLl.setVisibility(View.GONE);
            mPasswordLl.setVisibility(View.VISIBLE);
            mLoginPasswordTv.setVisibility(View.GONE);
            mLoginCodeTv.setVisibility(View.VISIBLE);
            mLoginTv.setText("登录");
            mPasswordEt.setHint("请输入密码");
            mRegisterNowTv.setVisibility(View.VISIBLE);
        } else if (i == 1) {
            type = i;
            mTitleTv.setText("登录");
            mInviteCodeLl.setVisibility(View.GONE);
            mMessageCodeLl.setVisibility(View.VISIBLE);
            mPasswordLl.setVisibility(View.GONE);
            mLoginPasswordTv.setVisibility(View.VISIBLE);
            mLoginCodeTv.setVisibility(View.GONE);
            mLoginTv.setText("登录");
            mRegisterNowTv.setVisibility(View.VISIBLE);
        } else if (i == 2) {
            type = 2;
            mTitleTv.setText("注册");
            mInviteCodeLl.setVisibility(View.VISIBLE);
            mMessageCodeLl.setVisibility(View.VISIBLE);
            mPasswordLl.setVisibility(View.VISIBLE);
            mLoginTv.setText("立即注册");
            mPasswordEt.setHint("请设置密码");
            mRegisterNowTv.setVisibility(View.GONE);
        }
    }

}
