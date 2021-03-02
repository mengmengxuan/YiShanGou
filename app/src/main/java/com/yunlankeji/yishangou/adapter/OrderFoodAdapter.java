package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ListUtil;
import com.yunlankeji.yishangou.view.RadiusImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderFoodAdapter extends RecyclerView.Adapter<OrderFoodAdapter.ViewHolder> {
    private Context context;
    private List<Data> items = new ArrayList<>();

    public OrderFoodAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderFoodAdapter.ViewHolder holder, int position) {

        //头像
        Glide.with(context)
                .load(items.get(position).productLogo)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .into(holder.mImageIv);

        //商品名称
        holder.mNameTv.setText(items.get(position).productName);

        //价格
        holder.mPriceTv.setText("￥" + items.get(position).price);

        //数量
        holder.mCountTv.setText("x" + items.get(position).num);

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
        @BindView(R.id.m_price_tv)
        TextView mPriceTv;//总价格
        @BindView(R.id.m_count_tv)
        TextView mCountTv;//商品总数

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
