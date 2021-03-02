package com.yunlankeji.yishangou.activity.business;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.hwangjr.rxbus.RxBus;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.CommodityCategoryAdapter;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpPostUtils;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.ZLBusAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/31
 * Describe:商品分类页面
 */
public class GoodsCategoryActivity extends BaseActivity {
    private static final String TAG = "GoodsCategoryActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_right_tv)
    TextView mRightTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_goods_category_rv)
    RecyclerView mGoodsCategoryRv;
    private CommodityCategoryAdapter mCommodityCategoryAdapter;
    private List<Data> items = new ArrayList<>();
    private String mCategoryName;
    private Dialog mAddCategoryDialog;

    @Override
    public int setLayout() {
        return R.layout.activity_goods_category;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("商品分类");
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText("新增分类");
        mRightTv.setTextColor(getResources().getColor(R.color.color_333333));

        mCommodityCategoryAdapter = new CommodityCategoryAdapter(this);
        mCommodityCategoryAdapter.setItems(items);
        mGoodsCategoryRv.setLayoutManager(new LinearLayoutManager(this));
        mGoodsCategoryRv.setAdapter(mCommodityCategoryAdapter);
        mCommodityCategoryAdapter.setOnItemClickedListener(new CommodityCategoryAdapter.OnItemClickedListener() {
            /**
             * 删除分类
             * @param view
             * @param position
             */
            @Override
            public void onItemClicked(View view, int position) {
                //显示删除分类对话框
                showDeleteCategoryDialog(items.get(position).id);
            }
        });
    }

    /**
     * 显示删除分类对话框
     *
     * @param id
     */
    private void showDeleteCategoryDialog(String id) {
        DeleteDialog tagDialog = new DeleteDialog(GoodsCategoryActivity.this);
        tagDialog.setCaption("删除分类")
                .setMessage("是否确定删除此分类？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        //删除分类
                        requestDeleteMerchantMategory(id);
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 删除分类
     *
     * @param id
     */
    private void requestDeleteMerchantMategory(String id) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestDeleteMerchantMategory(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "删除分类：" + JSON.toJSONString(response.data));

                RxBus.get().post(ZLBusAction.Refresh_Merchant_Goods, "Refresh_Merchant_Goods");

                if (items != null) {
                    items.clear();
                }
                requestMerchantCategoryList();

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
                if (code.equals("401")) {
                    ToastUtil.showShort(msg);
                } else {
                    ToastUtil.showShortForNet(msg);
                }
                LogUtil.d(TAG, "请求失败");

            }
        });
    }

    @Override
    public void initData() {
        //获取商铺分类
        requestMerchantCategoryList();
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

                if (data != null) {
                    items.addAll(data);
                    mCommodityCategoryAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.m_back_iv, R.id.m_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_right_tv:
                //显示新增分类的弹窗
                showCreateCategoryDialog();
                break;
        }
    }

    /**
     * 显示新增分类的弹窗
     */
    private void showCreateCategoryDialog() {
        mAddCategoryDialog = new Dialog(GoodsCategoryActivity.this, R.style.DialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_create_category_dialog, null);
        mAddCategoryDialog.setContentView(view);

        ImageView m_cancel_iv = view.findViewById(R.id.m_cancel_iv);
        EditText m_input_category_et = view.findViewById(R.id.m_input_category_et);
        TextView m_sure_tv = view.findViewById(R.id.m_sure_tv);

        m_cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddCategoryDialog.dismiss();
            }
        });

        m_sure_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoryName = m_input_category_et.getText().toString();
                //
                if (TextUtils.isEmpty(mCategoryName)) {
                    ToastUtil.showShort("分类名称不能为空");
                } else {
                    //新增分类
                    requestAddMerchantCategory();
                }
            }
        });

        //获取当前Activity所在的窗体
        Window window = mAddCategoryDialog.getWindow();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = (int) (defaultDisplay.getWidth() * 0.8);
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);

        mAddCategoryDialog.show();

        mAddCategoryDialog.setCanceledOnTouchOutside(true);

    }

    /**
     * 新增分类
     */
    private void requestAddMerchantCategory() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.categoryName = mCategoryName;
        paramInfo.merchantCode = Global.merchantCode;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddMerchantCategory(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "新增分类：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("添加成功");

                RxBus.get().post(ZLBusAction.Refresh_Merchant_Goods, "Refresh_Merchant_Goods");

                mAddCategoryDialog.dismiss();

                if (items != null) {
                    items.clear();
                }
                requestMerchantCategoryList();

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
