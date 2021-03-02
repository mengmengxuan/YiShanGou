package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/11/9
 * Describe:地址管理适配器
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickedListener mOnItemClickedListener = null;

    public AddressAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //姓名
        holder.mNameTv.setText(items.get(position).receiveName);

        //电话
        holder.mPhoneTv.setText(items.get(position).receivePhone);

        //是否默认
        if (items.get(position).isDefault.equals("1")) {
            holder.mDefaultAddressTv.setVisibility(View.VISIBLE);
        } else {
            holder.mDefaultAddressTv.setVisibility(View.GONE);
        }

        //地址
        holder.mAddressTv.setText(items.get(position).location + items.get(position).adress);

        //总条目点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onItemClicked(v, position);
                }
            }
        });
        //修改地址
        holder.mEditTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onEditViewClicked(v, position);
                }
            }
        });
        //删除地址
        holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onDeleteViewClicked(v, position);
                }
            }
        });

        if (position == items.size() - 1) {
            holder.partLine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public interface OnItemClickedListener {
        void onItemClicked(View view, int position);

        void onEditViewClicked(View view, int position);

        void onDeleteViewClicked(View view, int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.m_name_tv)
        TextView mNameTv;
        @BindView(R.id.m_phone_tv)
        TextView mPhoneTv;
        @BindView(R.id.m_default_address_tv)
        TextView mDefaultAddressTv;
        @BindView(R.id.m_address_tv)
        TextView mAddressTv;
        @BindView(R.id.m_edit_tv)
        TextView mEditTv;
        @BindView(R.id.m_delete_tv)
        TextView mDeleteTv;
        @BindView(R.id.part_line)
        View partLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
