package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.home.StoreDetailActivity;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/28
 * Describe:
 */
public class SelectedGoodsAdapter extends RecyclerView.Adapter<SelectedGoodsAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickedListener mOnItemClickedListener = null;

    public SelectedGoodsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_selected_goods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Data data = items.get(position);
        //头像
        Glide.with(context)
                .load(data.productLogo)
                .into(holder.mSelectedGoodsLogoIv);
        //名称
        holder.mSelectedGoodsNameTv.setText(data.productName);
        //规格
        holder.mSelectedWeightTv.setText(data.sku);
        //价格
        holder.mSelectedGoodsPriceTv.setText("￥" + data.price);
        //数量
        holder.mDialogCommodityCountTv.setText(data.num);

        //加号点击事件
        holder.mDialogPlusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onPlusItemClicked(v, position);
                }
            }
        });

        //减号点击事件
        holder.mDialogMinusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onLessItemClicked(v, position);
                }
            }
        });

    }

    public interface OnItemClickedListener {

        /**
         * 点击加号
         *
         * @param view
         * @param position
         */
        void onPlusItemClicked(View view, int position);

        /**
         * 点击减号
         *
         * @param view
         * @param position
         */
        void onLessItemClicked(View view, int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.m_selected_goods_logo_iv)
        ImageView mSelectedGoodsLogoIv;
        @BindView(R.id.m_dialog_minus_iv)
        ImageView mDialogMinusIv;
        @BindView(R.id.m_dialog_plus_iv)
        ImageView mDialogPlusIv;
        @BindView(R.id.m_selected_goods_name_tv)
        TextView mSelectedGoodsNameTv;
        @BindView(R.id.m_selected_weight_tv)
        TextView mSelectedWeightTv;
        @BindView(R.id.m_selected_goods_price_tv)
        TextView mSelectedGoodsPriceTv;
        @BindView(R.id.m_dialog_commodity_count_tv)
        TextView mDialogCommodityCountTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
