package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.view.RadiusImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {
    private Context context;
    private List<Data> items = new ArrayList<>();

    public BusinessAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BusinessAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessAdapter.ViewHolder holder, int position) {
       /* Glide.with(context)
                .load(items.get(position).imgUrl)
                .into(holder.mImageIv);
        holder.mNameTv.setText(items.get(position).name);
        holder.mHadSoldTv.setText(items.get(position).count);
        holder.mStartFeeTv.setText(items.get(position).count);
        holder.mDeliveryFeeTv.setText(items.get(position).count);
        holder.mDistanceTv.setText(items.get(position).count);
*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_image_iv)
        RadiusImageView mImageIv;//商家icon
        @BindView(R.id.m_name_tv)
        TextView mNameTv;//商家名称
        @BindView(R.id.m_had_sold_tv)
        TextView mHadSoldTv;//月售
        @BindView(R.id.m_start_fee_tv)
        TextView mStartFeeTv;//起送
        @BindView(R.id.m_delivery_fee_tv)
        TextView mDeliveryFeeTv;//配送费
        @BindView(R.id.m_distance_tv)
        TextView mDistanceTv;//距离

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
