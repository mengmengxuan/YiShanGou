package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.business.CreateGoodsActivity;
import com.yunlankeji.yishangou.activity.business.GoodsCategoryActivity;
import com.yunlankeji.yishangou.adapter.BusinessGoodsAdapter;
import com.yunlankeji.yishangou.adapter.BusinessGoodsCategoryAdapter;
import com.yunlankeji.yishangou.adapter.GoodsCategoryAdapter;
import com.yunlankeji.yishangou.globle.Global;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/29
 * Describe:
 */
public class GoodsManagerActivity extends BaseActivity {

    private static final String TAG = "GoodsManagerActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_right_tv)
    TextView mRightTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_search_et)
    EditText mSearchEt;
    @BindView(R.id.m_search_rl)
    RelativeLayout mSearchRl;
    @BindView(R.id.m_goods_category_rv)
    RecyclerView mGoodsCategoryRv;
    @BindView(R.id.m_category_name_tv)
    TextView mCategoryNameTv;
    @BindView(R.id.m_goods_rv)
    RecyclerView mGoodsRv;
    @BindView(R.id.m_commit_tv)
    TextView mCommitTv;
    private BusinessGoodsCategoryAdapter mBusinessGoodsCategoryAdapter;
    private List<Data> goodsCategoryItems = new ArrayList<>();
    private BusinessGoodsAdapter mBusinessGoodsAdapter;
    private List<Data> goodsItems = new ArrayList<>();
    private String mCategoryCode;

    @Override
    public int setLayout() {
        return R.layout.activity_goods_manager;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("商铺管理");
        mRightTv.setText("商品分类");
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setTextColor(getResources().getColor(R.color.color_333333));

        //商品分类
        mBusinessGoodsCategoryAdapter = new BusinessGoodsCategoryAdapter(this);
        mBusinessGoodsCategoryAdapter.setItems(goodsCategoryItems);
        mGoodsCategoryRv.setLayoutManager(new LinearLayoutManager(this));
        mGoodsCategoryRv.setAdapter(mBusinessGoodsCategoryAdapter);
        mBusinessGoodsCategoryAdapter.setOnItemClickedListener(new BusinessGoodsCategoryAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(View view, int position) {
                //选中的分类
                String categoryName = goodsCategoryItems.get(position).categoryName;
                mCategoryNameTv.setText(categoryName);

                for (int i = 0; i < goodsCategoryItems.size(); i++) {
                    if (i == position) {
                        goodsCategoryItems.get(i).status = "1";
                        //点击的条目的分类编码
                        mCategoryCode = goodsCategoryItems.get(i).categoryCode;
                        //获取该分类下的商品
                        if (goodsItems != null) {
                            goodsItems.clear();
                        }
                        requestMyMerchantProductList();
                    } else {
                        goodsCategoryItems.get(i).status = "0";
                    }
                }
                mBusinessGoodsCategoryAdapter.notifyDataSetChanged();
            }
        });

        //商品
        mBusinessGoodsAdapter = new BusinessGoodsAdapter(this);
        mBusinessGoodsAdapter.setItem(goodsItems);
        mGoodsRv.setLayoutManager(new LinearLayoutManager(this));
        mGoodsRv.setAdapter(mBusinessGoodsAdapter);
        mBusinessGoodsAdapter.setOnItemClickedListener(new BusinessGoodsAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(GoodsManagerActivity.this, CreateGoodsActivity.class);
                intent.putExtra("from", "edit");
                intent.putExtra("goods", goodsItems.get(position));
                intent.putExtra("id", goodsItems.get(position).id);
                startActivity(intent);
            }
        });

    }

    @Override
    public void initData() {
        //获取商铺分类
        requestMerchantCategoryList();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Refresh_Merchant_Goods)})
    public void refreshMerchantGoods(String status) {
        if (status.equals("Refresh_Merchant_Goods")) {
            if (goodsCategoryItems != null) {
                goodsCategoryItems.clear();
            }
            if (goodsItems != null) {
                goodsItems.clear();
            }
            //获取商铺分类
            requestMerchantCategoryList();
        }
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

                    mCategoryNameTv.setText("分类名");

                    for (int i = 0; i < data.size(); i++) {
                        if (i == 0) {
                            data.get(i).status = "1";
                            //获取第一条分类编码
                            mCategoryCode = data.get(i).categoryCode;
                            //根据分类获取商品数据
                            requestMyMerchantProductList();

                            //取出第一条分类的名称显示
                            String categoryName = data.get(i).categoryName;
                            mCategoryNameTv.setText(categoryName);

                        } else {
                            data.get(i).status = "0";
                        }
                    }
                    goodsCategoryItems.addAll(data);
                    mBusinessGoodsCategoryAdapter.notifyDataSetChanged();
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
     * 根据分类获取商品列表
     */
    private void requestMyMerchantProductList() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.categoryCode = mCategoryCode;
        paramInfo.merchantCode = Global.merchantCode;
        Call<ResponseBean<List<Data>>> call =
                NetWorkManager.getInstance().getRequest().requestMyMerchantProductList(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "获取商品列表：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (data != null) {
                    goodsItems.addAll(data);
                    mBusinessGoodsAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.m_back_iv, R.id.m_right_tv, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_right_tv://商品分类
                intent.setClass(this, GoodsCategoryActivity.class);
                startActivity(intent);

                break;
            case R.id.m_commit_tv://新增商品
                intent.setClass(this, CreateGoodsActivity.class);
                intent.putExtra("from", "add");
                startActivity(intent);
                break;
        }
    }
}
