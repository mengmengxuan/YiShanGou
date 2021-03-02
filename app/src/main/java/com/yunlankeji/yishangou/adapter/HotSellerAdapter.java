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
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/25
 * Describe:
 */
public class HotSellerAdapter extends RecyclerView.Adapter<HotSellerAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public HotSellerAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_hot_seller, parent, false);
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
            holder.mDeliveryFeeTv.setText("配送费￥" + ConstantUtil.setFormat("0.00", merchant.shippingAccount));
        }

        //距离
        //判断是否是当前城市
        Boolean isLocationCity = (Boolean) SPUtils.get(context, "isLocationCity", false);
        if (isLocationCity) {
            //选择的城市是当前城市
            if (TextUtils.isEmpty(merchant.distance)) {
                holder.mDistanceTv.setText("0 km");
            } else {
                holder.mDistanceTv.setText(merchant.distance + " km");
            }
        } else {
            //选择的城市不是当前城市
            holder.mDistanceTv.setText("超出配送范围");
        }

        //条目点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(v, position);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.m_merchant_logo_iv)
        ImageView mMerchantLogoIv;
        @BindView(R.id.m_merchant_name_tv)
        TextView mMerchantNameTv;
        @BindView(R.id.m_monthly_sale_tv)
        TextView mMonthlySaleTv;
        @BindView(R.id.m_initial_delivery_tv)
        TextView mInitialDeliveryTv;
        @BindView(R.id.m_distance_tv)
        TextView mDistanceTv;
        @BindView(R.id.m_delivery_fee_tv)
        TextView mDeliveryFeeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
