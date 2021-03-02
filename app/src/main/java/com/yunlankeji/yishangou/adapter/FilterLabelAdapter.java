package com.yunlankeji.yishangou.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.personal.baseutils.glide.GlideLoader;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.bean.StatusBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/26
 * Describe:
 */
public class FilterLabelAdapter extends BaseAdapter {
    Context context;
    OnChooseListener choiceListener = null;
    private int count;
    private List<StatusBean> items = new ArrayList<>();

    public FilterLabelAdapter(Context context) {
        this.context = context;
    }

    public void setChoiceListener(OnChooseListener choiceListener) {
        this.choiceListener = choiceListener;
    }

    public void setItems(List<StatusBean> items) {
        this.items = items;
    }

    public interface OnChooseListener {
        void onChoose(int position, boolean isCheck);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_item_filter_label, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mFilterLabelTv.setText(items.get(i).title);

        if (items.get(i).status.equals("1")) {
            //如果是选中状态
            holder.mFilterLabelTv.setTextColor(context.getResources().getColor(R.color.color_F36C17));
        } else {
            //未选中
            holder.mFilterLabelTv.setTextColor(context.getResources().getColor(R.color.color_666666));
        }

        return view;
    }

    static class ViewHolder {

        @BindView(R.id.m_filter_label_tv)
        TextView mFilterLabelTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
