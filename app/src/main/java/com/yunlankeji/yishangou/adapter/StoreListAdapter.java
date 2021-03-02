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
import com.yunlankeji.yishangou.activity.home.StoreListActivity;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/26
 * Describe:商家列表适配器
 */
public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public StoreListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_store_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Data merchant = items.get(position);
        //店铺头像
        Glide.with(context)
                .load(merchant.merchantLogo)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .into(holder.mMerchantLogoIv);

        //店铺名称
        if (TextUtils.isEmpty(merchant.merchantName)) {
            holder.mMerchantNameTv.setText("暂无名称");
        } else {
            holder.mMerchantNameTv.setText(merchant.merchantName);
        }

        //月销
        if (TextUtils.isEmpty(merchant.saleCount)) {
            holder.mMonthlySaleTv.setText("月销 0");
        } else {
            holder.mMonthlySaleTv.setText("月销 " + merchant.saleCount);
        }

        //起送
        if (TextUtils.isEmpty(merchant.orderAccount)) {
            holder.mInitialDeliveryTv.setText("起送￥0");
        } else {
            holder.mInitialDeliveryTv.setText("起送￥" + merchant.orderAccount);
        }

        //配送费
        if (TextUtils.isEmpty(merchant.shippingAccount)) {
            holder.mDeliveryFeeTv.setText("配送费￥0");
        } else {
            holder.mDeliveryFeeTv.setText("配送费￥" + merchant.shippingAccount);
        }

        //距离
        if (TextUtils.isEmpty(merchant.distance)) {
            holder.mDistanceTv.setText("0 km");
        } else {
            holder.mDistanceTv.setText(merchant.distance + " km");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClicked(view,position);
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_merchant_logo_iv)
        ImageView mMerchantLogoIv;
        @BindView(R.id.m_merchant_name_tv)
        TextView mMerchantNameTv;
        @BindView(R.id.m_monthly_sale_tv)
        TextView mMonthlySaleTv;
        @BindView(R.id.m_initial_delivery_tv)
        TextView mInitialDeliveryTv;
        @BindView(R.id.m_delivery_fee_tv)
        TextView mDeliveryFeeTv;
        @BindView(R.id.m_distance_tv)
        TextView mDistanceTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
