package com.yunlankeji.yishangou.adapter;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by Snooker on 2020/12/25
 * Describe:
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final Context context;
    private List<Data> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;
    private String from;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (from != null) {
            if (from.equals("home")) {
                if (position < 9) {
                    //图标
                    Glide.with(context)
                            .load(items.get(position).logo)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(holder.mCategoryLogoIv);

                    //分类名
                    holder.mCategoryNameTv.setText(items.get(position).merchantTypeName);
                } else {
                    //更多的图标
                    Glide.with(context)
                            .load(R.mipmap.icon_category_more)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(holder.mCategoryLogoIv);

                    //分类名
                    holder.mCategoryNameTv.setText("更多");
                }

                //首页的分类适配器
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 9) {
                            //点击了更多
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onLastItemClicked(v, position);
                            }

                        } else {
                            //点击了分类
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onNormalItemClicked(v, position);
                            }
                        }
                    }
                });
            } else if (from.equals("more_category")) {
                //更多分类的分类适配
                //图标
                Glide.with(context)
                        .load(items.get(position).logo)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(holder.mCategoryLogoIv);

                //分类名
                holder.mCategoryNameTv.setText(items.get(position).merchantTypeName);

                //更多分类的分类适配器
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击了更多
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClicked(v, position);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (from != null) {
            if (from.equals("more_category")) {
                return items != null ? items.size() : 0;
            }
        }
        //大于10条只取10条
        return items != null ? Math.min(items.size(), 10) : 0;
    }

    public void setItems(List<Data> items) {
        this.items = items;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.m_category_logo_iv)
        ImageView mCategoryLogoIv;
        @BindView(R.id.m_category_name_tv)
        TextView mCategoryNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);

        void onNormalItemClicked(View view, int position);

        void onLastItemClicked(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
