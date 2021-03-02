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
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.home.SpecialZoneActivity;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2021/1/8
 * Describe:特惠专区商品适配器
 */
public class SpecialZoneCommodityAdapter extends RecyclerView.Adapter<SpecialZoneCommodityAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickedListener mOnItemClickedListener = null;

    public SpecialZoneCommodityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adpater_item_special_zone_commodity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //图片
        Glide.with(context)
                .load(items.get(position).productLogo)
                .into(holder.m_goods_pic_iv);
        //名称
        holder.m_goods_name_tv.setText(items.get(position).productName);

        //月销
        if (TextUtils.isEmpty(items.get(position).saleCount)) {
            holder.m_monthly_sale_tv.setText("月销 0");
        } else {
            holder.m_monthly_sale_tv.setText("月销" + items.get(position).saleCount);
        }

        //价格
        holder.m_price_tv.setText("￥" + items.get(position).price);

        //条目点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onItemClicked(v, position);
                }
            }
        });

    }

    public interface OnItemClickedListener {
        void onItemClicked(View view, int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_goods_pic_iv)
        ImageView m_goods_pic_iv;
        @BindView(R.id.m_goods_name_tv)
        TextView m_goods_name_tv;
        @BindView(R.id.m_monthly_sale_tv)
        TextView m_monthly_sale_tv;
        @BindView(R.id.m_price_tv)
        TextView m_price_tv;
        @BindView(R.id.m_immediately_buy_tv)
        TextView m_immediately_buy_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
