package com.yunlankeji.yishangou.activity.home;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.baseutils.widget.GridViewForScrollView;
import com.personal.baseutils.widget.wheelview.ItemsRange;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.FilterLabelAdapter;
import com.yunlankeji.yishangou.adapter.StoreListAdapter;
import com.yunlankeji.yishangou.bean.StatusBean;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.http.POST;

/**
 * Create by Snooker on 2020/12/26
 * Describe:
 */
public class StoreListActivity extends BaseActivity {
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_search_et)
    EditText mSearchEt;
    @BindView(R.id.m_search_tv)
    TextView mSearchTv;
    @BindView(R.id.m_filter_gv)
    GridViewForScrollView mFilterGv;
    @BindView(R.id.m_store_list_rv)
    RecyclerView mStoreListRv;
    private FilterLabelAdapter mFilterLabelAdapter;
    private StoreListAdapter mStoreListAdapter;
    private List<Data> storeList = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activtity_store_list;
    }

    @Override
    protected void initView() {

        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("");
        String from = getIntent().getStringExtra("from");

        //初始化刷选标签
        ArrayList<StatusBean> filterLabelItems = initFilterLabelData();

        //筛选标签
        mFilterLabelAdapter = new FilterLabelAdapter(this);
        mFilterLabelAdapter.setItems(filterLabelItems);
        mFilterGv.setAdapter(mFilterLabelAdapter);

        mFilterGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < filterLabelItems.size(); i++) {
                    if (i == position) {
                        filterLabelItems.get(i).status = "1";
                    } else {
                        filterLabelItems.get(i).status = "0";
                    }
                }
                mFilterLabelAdapter.notifyDataSetChanged();
            }
        });

        //商家列表
        mStoreListAdapter = new StoreListAdapter(this);
        mStoreListRv.setLayoutManager(new LinearLayoutManager(this));
        mStoreListAdapter.setItems(storeList);
        mStoreListRv.setAdapter(mStoreListAdapter);
    }

    /**
     * 初始化筛选标签数据集合
     *
     * @return
     */
    private ArrayList<StatusBean> initFilterLabelData() {
        ArrayList<StatusBean> statusBeans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            StatusBean statusBean = new StatusBean();
            if (i == 0) {
                statusBean.title = "综合排序";
                statusBean.status = "1";
            } else if (i == 1) {
                statusBean.title = "配送最快";
                statusBean.status = "0";
            } else if (i == 2) {
                statusBean.title = "销量最高";
                statusBean.status = "0";
            } else if (i == 3) {
                statusBean.title = "入驻最早";
                statusBean.status = "0";
            } else if (i == 4) {
                statusBean.title = "入驻最迟";
                statusBean.status = "0";
            }
            statusBeans.add(statusBean);
        }
        return statusBeans;
    }
}
