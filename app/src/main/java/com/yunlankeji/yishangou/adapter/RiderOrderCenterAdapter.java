package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2021/1/2
 * Describe:订单大厅
 */
public class RiderOrderCenterAdapter extends RecyclerView.Adapter<RiderOrderCenterAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickedListener mOnItemClickedListener = null;

    public RiderOrderCenterAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_rider_order_center, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Data data = items.get(position);
        //跑腿还是外卖
        if (data.orderType.equals("0")) {
            //外卖
            Glide.with(context)
                    .load(R.mipmap.icon_rider_takeaway)
                    .into(holder.mOrderTypeIv);

            //配送价格字体颜色
            holder.mDeliveryPriceTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
            //距离你字体颜色
            holder.mDistanceTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
            //发布时间字体颜色
            holder.mPublishTimeTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
            //抢单按钮背景颜色
            holder.mGrabOrderTv.setBackgroundResource(R.drawable.shape_orange_rect_20);

        } else if (data.orderType.equals("1")) {
            //跑腿
            Glide.with(context)
                    .load(R.mipmap.icon_rider_run_errands)
                    .into(holder.mOrderTypeIv);

            //配送价格字体颜色
            holder.mDeliveryPriceTv.setTextColor(context.getResources().getColor(R.color.color_03A4FA));
            //距离你字体颜色
            holder.mDistanceTv.setTextColor(context.getResources().getColor(R.color.color_03A4FA));
            //发布时间字体颜色
            holder.mPublishTimeTv.setTextColor(context.getResources().getColor(R.color.color_03A4FA));
            //抢单按钮背景颜色
            holder.mGrabOrderTv.setBackgroundResource(R.drawable.shape_blue_rect_20);
        }

        //配送价格
        if (TextUtils.isEmpty(data.shippingAccount)) {
            holder.mDeliveryPriceTv.setText("￥0");
        } else {
            holder.mDeliveryPriceTv.setText("￥" + data.shippingAccount);
        }

        //距离你
        if (TextUtils.isEmpty(data.receiveDistance)) {
            holder.mDistanceTv.setText("0km");
        } else {
            holder.mDistanceTv.setText(ConstantUtil.setFormat("0.00", (Double.parseDouble(data.receiveDistance) / 1000) + "") +
                    "km");
        }

        //发布时间
        if (!TextUtils.isEmpty(data.releaseTime)) {
            if (Double.parseDouble(data.releaseTime) < 1) {
                holder.mPublishTimeTv.setText("刚刚");
            } else if (Double.parseDouble(data.releaseTime) < 60) {
                holder.mPublishTimeTv.setText(data.releaseTime + "分钟前");
            } else {
                //小时
                double hour = Double.parseDouble(data.releaseTime) / 60;
                holder.mPublishTimeTv.setText(ConstantUtil.setFormat("0.00", String.valueOf(hour)) + "小时前");
            }
        }

        //发货地址
        holder.mDeliveryPlaceTv.setText(data.sendAdress);

        //收货地址
        holder.mReceivePlaceTv.setText(data.receiveAdress);

        //全程
        if (!TextUtils.isEmpty(data.distance)) {
            holder.mWholeJourneyTv.setText(ConstantUtil.setFormat("0.00", (Double.parseDouble(data.distance) / 1000) + "") +
                    "km");
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

    public interface OnItemClickedListener {
        void onItemClicked(View view, int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.m_order_type_iv)
        ImageView mOrderTypeIv;
        @BindView(R.id.m_delivery_price_tv)
        TextView mDeliveryPriceTv;
        @BindView(R.id.m_distance_tv)
        TextView mDistanceTv;
        @BindView(R.id.m_publish_time_tv)
        TextView mPublishTimeTv;
        @BindView(R.id.m_delivery_place_tv)
        TextView mDeliveryPlaceTv;
        @BindView(R.id.m_receive_place_tv)
        TextView mReceivePlaceTv;
        @BindView(R.id.m_whole_journey_tv)
        TextView mWholeJourneyTv;
        @BindView(R.id.m_grab_order_tv)
        TextView mGrabOrderTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
