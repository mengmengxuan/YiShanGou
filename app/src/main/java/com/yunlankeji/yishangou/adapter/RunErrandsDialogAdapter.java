package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunErrandsDialogAdapter extends RecyclerView.Adapter<RunErrandsDialogAdapter.ViewHolder> {

    private Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public RunErrandsDialogAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RunErrandsDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_run_errands_dialog, parent, false);
        return new RunErrandsDialogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunErrandsDialogAdapter.ViewHolder holder, int position) {
        if (items.get(position).status.equals("1")) {
            //选中
            holder.mNameTv.setBackgroundResource(R.color.color_F36C17);
            holder.mNameTv.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            //未选中
            holder.mNameTv.setBackgroundResource(R.color.color_EEEEEE);
            holder.mNameTv.setTextColor(context.getResources().getColor(R.color.color_666666));
        }
        holder.mNameTv.setText(items.get(position).goodsType);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClicked(view,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_name_tv)
        TextView mNameTv;//商品名称


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
