package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.home.StoreDetailActivity;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/28
 * Describe:
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private final Context context;
    private OnItemClickedListener mOnItemClickedListener = null;
    private List<Data> items = new ArrayList();

    public GoodsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_goods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Data data = items.get(position);

        //判断是否是当前城市
        Boolean isLocationCity = (Boolean) SPUtils.get(context, "isLocationCity", false);
        if (isLocationCity) {
            // 选择的城市是当前城市
            holder.mCountLl.setVisibility(View.VISIBLE);
        } else {
            holder.mCountLl.setVisibility(View.GONE);
        }

        //头像
        Glide.with(context)
                .load(data.productLogo)
                .into(holder.mGoodsLogoIv);

        //名称
        holder.mGoodsNameTv.setText(data.productName);

        //月销
        if (TextUtils.isEmpty(data.saleCount)) {
            holder.mMonthlySaleTv.setText("月销0");
        } else {
            holder.mMonthlySaleTv.setText("月销" + data.saleCount);
        }

        //单价
        if (TextUtils.isEmpty(data.price)) {
            holder.mGoodsPriceTv.setText("￥0.0");
        } else {
            holder.mGoodsPriceTv.setText("￥" + data.price);
        }

        //数量
        if (TextUtils.isEmpty(data.num) || data.num.equals("0")) {
            holder.mLessGoodsIv.setVisibility(View.GONE);
            holder.mGoodsCountTv.setVisibility(View.GONE);
        } else {
            holder.mLessGoodsIv.setVisibility(View.VISIBLE);
            holder.mGoodsCountTv.setVisibility(View.VISIBLE);
            holder.mGoodsCountTv.setText(data.num);
        }

        //条目点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onItemClicked(v, position);
                }
            }
        });

        //加号点击事件
        holder.mAddGoodsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onPlusClicked(v, position);
                }
            }
        });

        //减号点击事件
        holder.mLessGoodsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onLessClicked(v, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.m_goods_logo_iv)
        ImageView mGoodsLogoIv;
        @BindView(R.id.m_goods_name_tv)
        TextView mGoodsNameTv;
        @BindView(R.id.m_monthly_sale_tv)
        TextView mMonthlySaleTv;
        @BindView(R.id.m_goods_price_tv)
        TextView mGoodsPriceTv;
        @BindView(R.id.m_add_goods_iv)
        ImageView mAddGoodsIv;
        @BindView(R.id.m_less_goods_iv)
        ImageView mLessGoodsIv;
        @BindView(R.id.m_goods_count_tv)
        TextView mGoodsCountTv;
        @BindView(R.id.m_count_ll)
        LinearLayout mCountLl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickedListener {
        //条目点击事件
        void onItemClicked(View view, int position);

        //加号点击事件
        void onPlusClicked(View view, int position);

        //减号点击事件
        void onLessClicked(View view, int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
    }
}
