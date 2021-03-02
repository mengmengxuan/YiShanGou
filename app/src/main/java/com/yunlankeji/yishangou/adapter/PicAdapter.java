package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    private Context context;
    private List<Data> items = new ArrayList<>();

    public PicAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_pic, parent, false);
        return new PicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PicAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(items.get(position).productLogo)
                .into(holder.mImageIv);
        holder.mNameTv.setText(items.get(position).productName);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_image_iv)
        ImageView mImageIv;//商品图片
        @BindView(R.id.m_name_tv)
        TextView mNameTv;//商品名称

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
