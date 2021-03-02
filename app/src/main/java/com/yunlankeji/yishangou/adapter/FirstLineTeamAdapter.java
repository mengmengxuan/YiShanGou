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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/25
 * Describe:
 */
public class FirstLineTeamAdapter extends RecyclerView.Adapter<FirstLineTeamAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public FirstLineTeamAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_first_line_team, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Data data = items.get(position);

        //头像
        Glide.with(context)
                .load(data.logo)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.mHeadIv);

        //姓名
        holder.mNameTv.setText(data.memberName);

        //时间
        if (data.createDt != null) {
            holder.mTimeTv.setText(ConstantUtil.convertDate(Long.parseLong(data.createDt) / 1000 + "",
                    "yyyy.MM.dd"));
        } else {
            holder.mTimeTv.setText("");
        }

        //直属人数
        if (TextUtils.isEmpty(data.numbers)) {
            holder.mDirectlyUnderTv.setText("直属0人");
        } else {
            holder.mDirectlyUnderTv.setText("直属" + data.numbers + "人");
        }

        //总佣金
        if (TextUtils.isEmpty(data.incomes)) {
            holder.mTotalCommissionTv.setText("￥0");
        } else {
            holder.mTotalCommissionTv.setText("￥" + data.incomes);
        }

        //昨日佣金
        if (TextUtils.isEmpty(data.ysincomes)) {
            holder.mYesterdayCommissionTv.setText("￥0");
        } else {
            holder.mYesterdayCommissionTv.setText("￥" + data.ysincomes);
        }

        //昨日直属
        if (TextUtils.isEmpty(data.ysnumber)) {
            holder.mYesterdayDirectlyUnderTv.setText("0");
        } else {
            holder.mYesterdayDirectlyUnderTv.setText(data.ysnumber);
        }

        //本月直属
        if (TextUtils.isEmpty(data.mouthnumbers)) {
            holder.mCurrentMonthDirectlyUnderTv.setText("0");
        } else {
            holder.mCurrentMonthDirectlyUnderTv.setText(data.mouthnumbers);
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

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.m_head_iv)
        ImageView mHeadIv;
        @BindView(R.id.m_name_tv)
        TextView mNameTv;
        @BindView(R.id.m_time_tv)
        TextView mTimeTv;
        @BindView(R.id.m_directly_under_tv)
        TextView mDirectlyUnderTv;
        @BindView(R.id.m_total_commission_tv)
        TextView mTotalCommissionTv;
        @BindView(R.id.m_yesterday_commission_tv)
        TextView mYesterdayCommissionTv;
        @BindView(R.id.m_yesterday_directly_under_tv)
        TextView mYesterdayDirectlyUnderTv;
        @BindView(R.id.m_current_month_directly_under_tv)
        TextView mCurrentMonthDirectlyUnderTv;

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
