package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.GoodsManagerActivity;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/30
 * Describe:
 */
public class BusinessGoodsAdapter extends RecyclerView.Adapter<BusinessGoodsAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickedListener mOnItemClickedListener = null;

    public BusinessGoodsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_business_goods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //商品头像
        Glide.with(context)
                .load(items.get(position).productLogo)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .into(holder.mGoodsLogoIv);

        //商品名称
        holder.mGoodsNameTv.setText(items.get(position).productName);

        //月销
        if (TextUtils.isEmpty(items.get(position).saleCount)) {
            holder.mMonthlySaleTv.setText("月销0");
        } else {
            holder.mMonthlySaleTv.setText("月销" + items.get(position).saleCount);
        }

        //价格
        if (TextUtils.isEmpty(items.get(position).price)) {
            holder.mGoodsPriceTv.setText("￥0");
        } else {
            holder.mGoodsPriceTv.setText("￥" + items.get(position).price);
        }

        //库存
        if (TextUtils.isEmpty(items.get(position).stock)) {
            holder.mStockTv.setText("库存0");
        } else {
            holder.mStockTv.setText("库存" + items.get(position).stock);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onItemClicked(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItem(List<Data> items) {
        this.items = items;
    }

    public interface OnItemClickedListener {
        void onItemClicked(View view, int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
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
        @BindView(R.id.m_stock_tv)
        TextView mStockTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
