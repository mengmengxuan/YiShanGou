package com.yunlankeji.yishangou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.home.SearchActivity;
import com.yunlankeji.yishangou.bean.StatusBean;
import com.yunlankeji.yishangou.network.responsebean.Data;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2021/1/12
 * Describe:
 */
public class HistorySearchAdapter extends BaseAdapter {
    Context context;
    OnChooseListener choiceListener = null;
    private int count;
    private List<Data> items = new ArrayList<>();

    public HistorySearchAdapter(Context context) {
        this.context = context;
    }

    public void setChoiceListener(OnChooseListener choiceListener) {
        this.choiceListener = choiceListener;
    }

    public void setItems(List<Data> items) {
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_item_history_search, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mTitleTv.setText(items.get(i).seacrhName);

        return view;
    }

    static class ViewHolder {

        @BindView(R.id.m_title_tv)
        TextView mTitleTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
