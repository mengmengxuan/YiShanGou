package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.MyWalletActivity;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/25
 * Describe:提现记录
 */
public class WithdrawalsRecordAdapter extends RecyclerView.Adapter<WithdrawalsRecordAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();

    public WithdrawalsRecordAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_withdrawals_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mAccountTv.setText(items.get(position).cashAccount);
        if ("1".equals(items.get(position).cashStatus)) {
            holder.mStatusTv.setText("成功");
        } else if ("2".equals(items.get(position).cashStatus)) {
            holder.mStatusTv.setVisibility(View.GONE);
            holder.mFailLl.setVisibility(View.VISIBLE);
            holder.mDefeatTv.setText("失败");
            holder.mReasonTv.setText(items.get(position).failReason);
        } else {
            holder.mStatusTv.setText("审核中");
//            holder.mStatusTv.setTextColor(context.getResources().getColor(R.color.color_FF9000));
        }
        if (!TextUtils.isEmpty(items.get(position).createDt)) {
            holder.mTimeTv.setText(ConstantUtil.convertDate(Long.parseLong(items.get(position).createDt) / 1000 + "",
                    "yyyy.MM" +
                            ".dd HH:mm:ss"));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_account_tv)
        TextView mAccountTv;
        @BindView(R.id.m_status_tv)
        TextView mStatusTv;
        @BindView(R.id.m_time_tv)
        TextView mTimeTv;
        @BindView(R.id.m_fail_ll)
        LinearLayout mFailLl;
        @BindView(R.id.m_defeat_tv)
        TextView mDefeatTv;
        @BindView(R.id.m_reason_tv)
        TextView mReasonTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
