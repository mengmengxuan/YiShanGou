package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RiderOrderAdapter extends RecyclerView.Adapter<RiderOrderAdapter.ViewHolder> {
    private Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public RiderOrderAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RiderOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_rider_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiderOrderAdapter.ViewHolder holder, int position) {
        if (items.get(position).orderType.equals("0")) {
            //外卖
            holder.mTypeTv.setText("外卖");
            holder.mTypeTv.setBackground(context.getResources().getDrawable(R.drawable.shape_orange_right_10));
            holder.mPriceTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
        } else if (items.get(position).orderType.equals("1")) {
            //跑腿
            holder.mTypeTv.setText("跑腿");
            holder.mTypeTv.setBackground(context.getResources().getDrawable(R.drawable.shape_blue_right_10));
            holder.mPriceTv.setTextColor(context.getResources().getColor(R.color.color_03A4FA));
        }

        if ("5".equals(items.get(position).orderStatus)) {
            holder.mStatusTv.setText("已完成");
            holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
        } else {
            if ("4".equals(items.get(position).orderStatus)) {
                //待收货
                if ("1".equals(items.get(position).riderStatus)) {
                    //骑手已送达，用户未点击确认送达
                    holder.mStatusTv.setText("骑手已送达");
                    holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
                }
            } else {
                holder.mStatusTv.setText("进行中");
                holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
            }
        }

        //发货地址
        holder.mAddressFromTv.setText(items.get(position).sendAdress);

        //收货地址
        holder.mAddressToTv.setText(items.get(position).receiveAdress);

        //全程
        if (!TextUtils.isEmpty(items.get(position).distance)) {
            holder.mDistanceTv.setText(ConstantUtil.setFormat("0.00", (Double.parseDouble(items.get(position).distance) / 1000) + "") +
                    "km");
        }
        if (TextUtils.isEmpty(items.get(position).shippingAccount)) {
            holder.mPriceTv.setText("￥0");
        } else {
            holder.mPriceTv.setText("￥" + items.get(position).shippingAccount);
        }

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
        @BindView(R.id.m_type_tv)
        TextView mTypeTv;//订单类型
        @BindView(R.id.m_status_tv)
        TextView mStatusTv;//状态
        @BindView(R.id.m_address_from_tv)
        TextView mAddressFromTv;//发货
        @BindView(R.id.m_address_to_tv)
        TextView mAddressToTv;//收货
        @BindView(R.id.m_distance_tv)
        TextView mDistanceTv;//全程
        @BindView(R.id.m_price_tv)
        TextView mPriceTv;//价格

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
