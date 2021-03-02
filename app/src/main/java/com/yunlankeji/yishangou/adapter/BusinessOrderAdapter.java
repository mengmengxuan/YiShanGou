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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessOrderAdapter extends RecyclerView.Adapter<BusinessOrderAdapter.ViewHolder>{
    private Context context;
    private List<Data> items = new ArrayList<>();
    private String type;//0进行中1已完成
    private OnItemClickListener mOnItemClickListener = null;

    public BusinessOrderAdapter(Context context){
        this.context = context;
    }

    public void setItems(List<Data> items){
        this.items = items;
    }

    public void setType(String type){
        this.type = type;
    }

    @NonNull
    @Override
    public BusinessOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_business_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessOrderAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(items.get(position).logo)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .into(holder.mHeadIv);
        holder.mNameTv.setText(items.get(position).receiveName);
        holder.mPhoneTv.setText(items.get(position).receivePhone);
        holder.mAddressTv.setText(items.get(position).receiveAdress);
        if (items.get(position).orderStatus.equals("0")) {
            // 0待派单 1待接单 2待取货 3待配送 4待收货 5已完成 6已取消
            holder.mStatusTv.setText("待派单");
            holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
            holder.mLookLogisticsTv.setVisibility(View.VISIBLE);
            holder.mLookLogisticsTv.setText("立即派单");
            holder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.white));
            holder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_look_logistics));
        } else if (items.get(position).orderStatus.equals("1")) {
            holder.mStatusTv.setText("待接单");
            holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
            holder.mLookLogisticsTv.setVisibility(View.GONE);
        } else if (items.get(position).orderStatus.equals("5")) {
            holder.mStatusTv.setText("已完成");
            holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
            holder.mLookLogisticsTv.setVisibility(View.GONE);
        } else if (items.get(position).orderStatus.equals("6")) {
            holder.mStatusTv.setText("已取消");
            holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
            holder.mLookLogisticsTv.setVisibility(View.GONE);
        } else {
            holder.mStatusTv.setText("配送中");
            holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
            holder.mLookLogisticsTv.setVisibility(View.VISIBLE);
            holder.mLookLogisticsTv.setText("查看物流");
            holder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.white));
            holder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_look_logistics));
        }
        //商品信息
        List<Data> detailList = items.get(position).detailList;
        if (detailList != null && detailList.size() > 0) {
            if (detailList.size() == 1) {
                holder.mFoodLl.setVisibility(View.VISIBLE);
                holder.mPictureRv.setVisibility(View.GONE);
                Glide.with(context)
                        .load(detailList.get(0).productLogo)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                        .into(holder.mImageIv);
                holder.mGoodNameTv.setText(detailList.get(0).productName);
            } else {
                holder.mFoodLl.setVisibility(View.GONE);
                holder.mPictureRv.setVisibility(View.VISIBLE);
                PicAdapter picAdapter = new PicAdapter(context);
                picAdapter.setItems(detailList);
                holder.mPictureRv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                holder.mPictureRv.setAdapter(picAdapter);
            }

            //价格
            if (TextUtils.isEmpty(items.get(position).orderAccount)) {
                holder.mPriceTv.setText("￥0.0");
            } else {
                holder.mPriceTv.setText("￥" + items.get(position).orderAccount);
            }

            //总数量
            if (TextUtils.isEmpty(items.get(position).num)){
                holder.mCountTv.setText("共0件");
            }else{
                holder.mCountTv.setText("共"+items.get(position).num+"件");
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(v, position);
                }
            }
        });
        holder.mNameRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onTitleClicked(v, position);
                }
            }
        });
        holder.mLookLogisticsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onLookLogisticsClicked(v, position);
                }
            }
        });


    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
        void onTitleClicked(View view, int position);
        void onLookLogisticsClicked(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_head_iv)
        ImageView mHeadIv;//头像
        @BindView(R.id.m_name_tv)
        TextView mNameTv;//名字
        @BindView(R.id.m_phone_tv)
        TextView mPhoneTv;//电话
        @BindView(R.id.m_address_tv)
        TextView mAddressTv;//地址
        @BindView(R.id.m_status_tv)
        TextView mStatusTv;//订单状态
        @BindView(R.id.m_price_tv)
        TextView mPriceTv;//总价格
        @BindView(R.id.m_count_tv)
        TextView mCountTv;//商品总数
        @BindView(R.id.m_look_logistics_tv)
        TextView mLookLogisticsTv;//查看物流按钮
        @BindView(R.id.m_name_rl)
        RelativeLayout mNameRl;//标题中头像名字电话地址整体
        @BindView(R.id.m_picture_rv)
        RecyclerView mPictureRv;//商品列表
        @BindView(R.id.m_image_iv)
        ImageView mImageIv;//商品image(1个商品时)
        @BindView(R.id.m_good_name_tv)
        TextView mGoodNameTv;//商品名称(1个商品时)
        @BindView(R.id.m_food_ll)
        LinearLayout mFoodLl;//1个商品的图片和名称整体

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
