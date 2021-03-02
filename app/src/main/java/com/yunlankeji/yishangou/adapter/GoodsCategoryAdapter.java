package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
public class GoodsCategoryAdapter extends RecyclerView.Adapter<GoodsCategoryAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickedListener mOnItemClickedListener = null;

    public GoodsCategoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_goods_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Data data = items.get(position);
        holder.mGoodsCategoryTv.setText(data.categoryName);
        if (data.status.equals("1")) {
            //选中
            holder.mGoodsCategoryTv.setBackgroundResource(R.color.part_line_f8f8f8);
            holder.mIndicatorView.setVisibility(View.VISIBLE);
        } else {
            //未选中
            holder.mGoodsCategoryTv.setBackgroundResource(R.color.white);
            holder.mIndicatorView.setVisibility(View.GONE);
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

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_goods_category_tv)
        TextView mGoodsCategoryTv;
        @BindView(R.id.m_indicator_view)
        View mIndicatorView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickedListener {
        void onItemClicked(View view, int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
    }

}
