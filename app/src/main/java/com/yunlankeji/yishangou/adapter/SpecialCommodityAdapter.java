package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/23
 * Describe:
 */
public class SpecialCommodityAdapter extends RecyclerView.Adapter<SpecialCommodityAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public SpecialCommodityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_specail_commodity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(items.get(position).productLogo)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .into(holder.mProductPicIv);

        if (TextUtils.isEmpty(items.get(position).productName)) {
            holder.mProductNameTv.setText("商家名称");
        } else {
            holder.mProductNameTv.setText(items.get(position).productName);
        }
        if (items.get(position).price == null) {
            holder.mPriceTv.setText("￥0.0");
        } else {
            holder.mPriceTv.setText("￥" + items.get(position).price);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onNormalItemClicked(v, position);
                }
            }
        });
//        holder.mImmediatelyBuyTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnItemClickListener != null) {
//                    mOnItemClickListener.onBuyItemClicked(v, position);
//                }
//            }
//        });
    }

    public interface OnItemClickListener {
        void onNormalItemClicked(View view, int position);

        void onBuyItemClicked(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
//        return items != null ? items.size() : 10;
        return items.size();
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.m_product_pic_iv)
        ImageView mProductPicIv;
        @BindView(R.id.m_product_name_tv)
        TextView mProductNameTv;
        @BindView(R.id.m_price_tv)
        TextView mPriceTv;
        @BindView(R.id.m_immediately_buy_tv)
        TextView mImmediatelyBuyTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
