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
public class SecondLineTeamAdapter extends RecyclerView.Adapter<SecondLineTeamAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();

    public SecondLineTeamAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_second_line_team, parent, false);
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

        //电话
        holder.mPhoneTv.setText(data.phone);

        //时间
        if (data.createDt != null) {
            //时分秒
            holder.mHourTimeTv.setText(ConstantUtil.convertDate(Long.parseLong(data.createDt) / 1000 + "",
                    "HH:mm:ss"));

            //年月日
            holder.mYearTimeTv.setText(ConstantUtil.convertDate(Long.parseLong(data.createDt) / 1000 + "",
                    "yyyy.MM.dd"));
        } else {
            holder.mHourTimeTv.setText("");
            holder.mYearTimeTv.setText("");
        }
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
        @BindView(R.id.m_phone_tv)
        TextView mPhoneTv;
        @BindView(R.id.m_hour_time_tv)
        TextView mHourTimeTv;
        @BindView(R.id.m_year_time_tv)
        TextView mYearTimeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
