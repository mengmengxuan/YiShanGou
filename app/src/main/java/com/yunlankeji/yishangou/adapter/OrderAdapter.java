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
import com.yunlankeji.yishangou.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ORDER_TAKEAWAY = 0;
    private static final int ORDER_RUN_ERRANDS = 1;
    private Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public OrderAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ORDER_TAKEAWAY) {
            //外卖
            view = LayoutInflater.from(context).inflate(R.layout.adapter_item_order_takeaway, parent, false);
            return new TakeawayViewHolder(view);
        } else {
            //跑腿
            view = LayoutInflater.from(context).inflate(R.layout.adapter_item_order_run_errands, parent, false);
            return new RunErrandsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //先获取条目对象
        Data data = items.get(position);
        if (holder instanceof TakeawayViewHolder) {
            //外卖订单
            TakeawayViewHolder takeawayViewHolder = (TakeawayViewHolder) holder;
            Glide.with(context)
                    .load(items.get(position).logo)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(takeawayViewHolder.mHeadIv);
            takeawayViewHolder.mNameTv.setText(items.get(position).merchantName);

            //商品信息
            List<Data> detailList = items.get(position).detailList;
            if (detailList != null && detailList.size() > 0) {
                if (detailList.size() == 1) {
                    takeawayViewHolder.mFoodLl.setVisibility(View.VISIBLE);
                    takeawayViewHolder.mFoodPicRv.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(detailList.get(0).productLogo)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                            .into(takeawayViewHolder.mImageIv);
                    takeawayViewHolder.mGoodNameTv.setText(detailList.get(0).productName);
                } else {
                    takeawayViewHolder.mFoodLl.setVisibility(View.GONE);
                    takeawayViewHolder.mFoodPicRv.setVisibility(View.VISIBLE);
                    PicAdapter picAdapter = new PicAdapter(context);
                    picAdapter.setItems(detailList);
                    takeawayViewHolder.mFoodPicRv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                    takeawayViewHolder.mFoodPicRv.setAdapter(picAdapter);
                }

                //价格
                if (TextUtils.isEmpty(items.get(position).orderAccount)) {
                    takeawayViewHolder.mPriceTv.setText("￥0.0");
                } else {
                    takeawayViewHolder.mPriceTv.setText("￥" + items.get(position).orderAccount);
                }

                //总数量
                if (TextUtils.isEmpty(items.get(position).num)) {
                    takeawayViewHolder.mCountTv.setText("共0件");
                } else {
                    takeawayViewHolder.mCountTv.setText("共" + items.get(position).num + "件");
                }
            }
            //根据orderStatus判断该订单的状态   // 0待派单 1待接单 2待取货 3待配送 4待收货 5已完成 6已取消
            switch (data.orderStatus) {
                case "0":
                    takeawayViewHolder.mStatusTv.setText("待派单");
                    takeawayViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
                    takeawayViewHolder.mLookLogisticsTv.setText("查看详情");
                    takeawayViewHolder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.white));
                    takeawayViewHolder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_look_logistics));
                    break;
                case "1":
                    takeawayViewHolder.mStatusTv.setText("待接单");
                    takeawayViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
                    takeawayViewHolder.mLookLogisticsTv.setText("查看详情");
                    takeawayViewHolder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.white));
                    takeawayViewHolder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_look_logistics));
                    break;
                case "2":
                case "3":
                case "4":
                    takeawayViewHolder.mStatusTv.setText("进行中");
                    takeawayViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
                    takeawayViewHolder.mLookLogisticsTv.setText("查看物流");
                    takeawayViewHolder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.white));
                    takeawayViewHolder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_look_logistics));
                    break;
                case "5":
                    takeawayViewHolder.mStatusTv.setText("已完成");
                    takeawayViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
                    takeawayViewHolder.mLookLogisticsTv.setVisibility(View.VISIBLE);
                    takeawayViewHolder.mLookLogisticsTv.setText("再来一单");
                    takeawayViewHolder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.color_333333));
                    takeawayViewHolder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_one_again));
                    break;
                case "6":
                    takeawayViewHolder.mStatusTv.setText("已取消");
                    takeawayViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
                    takeawayViewHolder.mLookLogisticsTv.setVisibility(View.VISIBLE);
                    takeawayViewHolder.mLookLogisticsTv.setText("再来一单");
                    takeawayViewHolder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.color_333333));
                    takeawayViewHolder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_one_again));
                    break;
            }

            //条目点击事件
            takeawayViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClicked(v, position);
                    }
                }
            });

            //点击跳转到详情页面
            takeawayViewHolder.mLookLogisticsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onLookLogisticsClicked(v, position);
                    }
                }
            });

            //点击店铺名称
            takeawayViewHolder.mNameRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onTitleClicked(v, position);
                    }
                }
            });

        } else if (holder instanceof RunErrandsViewHolder) {
            //跑腿订单
            RunErrandsViewHolder runErrandsViewHolder = (RunErrandsViewHolder) holder;

            //发货地址
            runErrandsViewHolder.mSendAddressTv.setText(data.sendAdress);
            //收货地址
            runErrandsViewHolder.mReceiveAddressTv.setText(data.receiveAdress);
            //全程
            runErrandsViewHolder.mDistanceTv.setText(ConstantUtil.setFormat("0.00",
                    Double.parseDouble(data.distance) / 1000 + "") + "km");
            //物品
            runErrandsViewHolder.mGoodsTypeTv.setText(data.goodsType + "/" + data.weight + "kg/" + data.goodsCost);

            //价格
            if (TextUtils.isEmpty(data.orderAccount)) {
                runErrandsViewHolder.mPriceTv.setText("￥0.0");
            } else {
                runErrandsViewHolder.mPriceTv.setText("￥" + data.orderAccount);
            }

            //根据orderStatus判断该订单的状态   // 0待派单 1待接单 2待取货 3待配送 4待收货 5已完成 6已取消
            switch (data.orderStatus) {
                case "0":
                    runErrandsViewHolder.mStatusTv.setText("待支付");
                    runErrandsViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
                    runErrandsViewHolder.mLookLogisticsTv.setVisibility(View.VISIBLE);
                    runErrandsViewHolder.mLookLogisticsTv.setText("立即支付");
                    runErrandsViewHolder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.white));
                    runErrandsViewHolder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_look_logistics));
                    break;
                case "1":
                case "2":
                case "3":
                case "4":
                    runErrandsViewHolder.mStatusTv.setText("进行中");
                    runErrandsViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
                    runErrandsViewHolder.mLookLogisticsTv.setVisibility(View.VISIBLE);
                    runErrandsViewHolder.mLookLogisticsTv.setText("查看物流");
                    runErrandsViewHolder.mLookLogisticsTv.setTextColor(context.getResources().getColor(R.color.white));
                    runErrandsViewHolder.mLookLogisticsTv.setBackground(context.getResources().getDrawable(R.drawable.bg_look_logistics));
                    break;
                case "5":
                    runErrandsViewHolder.mStatusTv.setText("已完成");
                    runErrandsViewHolder.mLookLogisticsTv.setVisibility(View.GONE);
                    runErrandsViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
                    break;
                case "6":
                    runErrandsViewHolder.mStatusTv.setText("已取消");
                    runErrandsViewHolder.mLookLogisticsTv.setVisibility(View.GONE);
                    runErrandsViewHolder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_999999));
                    break;
            }

            //条目点击事件
            runErrandsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClicked(v, position);
                    }
                }
            });

            //查看物流或再来一单
            runErrandsViewHolder.mLookLogisticsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onLookLogisticsClicked(v, position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);

        void onTitleClicked(View view, int position);

        void onLookLogisticsClicked(View view, int position);

    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).orderType.equals("1")) {
            //跑腿
            return ORDER_RUN_ERRANDS;
        } else {
            //外卖
            return ORDER_TAKEAWAY;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TakeawayViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_head_iv)
        ImageView mHeadIv;//商家icon
        @BindView(R.id.m_name_tv)
        TextView mNameTv;//商家名称
        @BindView(R.id.m_status_tv)
        TextView mStatusTv;//订单状态
        @BindView(R.id.m_price_tv)
        TextView mPriceTv;//总价格
        @BindView(R.id.m_count_tv)
        TextView mCountTv;//商品总数
        @BindView(R.id.m_look_logistics_tv)
        TextView mLookLogisticsTv;//查看物流按钮
        @BindView(R.id.m_name_rl)
        RelativeLayout mNameRl;//商家图片名称整体
        @BindView(R.id.m_food_pic_rv)
        RecyclerView mFoodPicRv;//商品列表
        @BindView(R.id.m_image_iv)
        ImageView mImageIv;//商品image(1个商品时)
        @BindView(R.id.m_good_name_tv)
        TextView mGoodNameTv;//商品名称(1个商品时)
        @BindView(R.id.m_food_ll)
        LinearLayout mFoodLl;//1个商品的图片和名称整体

        public TakeawayViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RunErrandsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_status_tv)
        TextView mStatusTv;
        @BindView(R.id.m_send_address_tv)
        TextView mSendAddressTv;
        @BindView(R.id.m_price_tv)
        TextView mPriceTv;
        @BindView(R.id.m_receive_address_tv)
        TextView mReceiveAddressTv;
        @BindView(R.id.m_distance_tv)
        TextView mDistanceTv;
        @BindView(R.id.m_goods_type_tv)
        TextView mGoodsTypeTv;
        @BindView(R.id.m_look_logistics_tv)
        TextView mLookLogisticsTv;

        public RunErrandsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
