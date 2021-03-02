package com.yunlankeji.yishangou.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.personal.baseutils.glide.GlideLoader;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.WechatWithdrawActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/25
 * Describe:
 */
public class UploadWechatReceivePaymentAdapter extends BaseAdapter {
    Context context;
    OnChooseListener choiceListener = null;
    private int count;
    private List<String> items = new ArrayList<>();

    public UploadWechatReceivePaymentAdapter(Context context) {
        this.context = context;
    }

    public void setChoiceListener(OnChooseListener choiceListener) {
        this.choiceListener = choiceListener;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public interface OnChooseListener {
        void onChoose(int position, boolean isCheck);
    }

    @Override
    public int getCount() {
        return items == null ? 1 : (items.size() < count ? items.size() + 1 : count);
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_item_upload_wechat_receive_payment,
                    viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (items != null && items.size() >= 0) {
            if (i == items.size() && i < count) {
                holder.mCloseIv.setVisibility(View.GONE);
                holder.mPicFl.setClickable(true);
                holder.mPicFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (choiceListener != null) {
                            choiceListener.onChoose(items.size(), false);
                        }
                    }
                });
                holder.mPicIv.setVisibility(View.GONE);
            } else {
                holder.mPicFl.setClickable(false);
                holder.mCloseIv.setVisibility(View.VISIBLE);
                holder.mCloseIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (choiceListener != null) {
                            choiceListener.onChoose(i, true);
                        }
                    }
                });
                holder.mPicFl.setClickable(false);
                holder.mPicIv.setVisibility(View.VISIBLE);
                if (items.get(i).contains("http")) {

                    Glide.with(context)
                            .load(items.get(i))
                            .into(holder.mPicIv);
                } else {
                    Glide.with(context)
                            .load(new File(items.get(i)))
                            .into(holder.mPicIv);
                }
            }
        } else {
            holder.mCloseIv.setVisibility(View.GONE);
            holder.mPicIv.setClickable(true);
            holder.mPicIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    if (choiceListener != null) {
                        choiceListener.onChoose(0, false);
                    }
                }
            });
        }

        return view;

    }

    static class ViewHolder {
        @BindView(R.id.m_close_iv)
        ImageView mCloseIv;
        @BindView(R.id.m_pic_iv)
        ImageView mPicIv;
        @BindView(R.id.m_pic_fl)
        FrameLayout mPicFl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}