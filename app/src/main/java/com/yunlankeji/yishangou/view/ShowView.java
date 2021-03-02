package com.yunlankeji.yishangou.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yunlankeji.yishangou.R;

/**
 * Time: 2019/4/27 0027
 * Author:LiYong
 * Description:
 */
public class ShowView extends LinearLayout implements View.OnClickListener {

    private View view;
    private LinearLayout mRootLl;
    private LinearLayout showDataLayout;//无数据
    private LinearLayout showNetworkLayout;//无网络
    //   private LinearLayout showLoginLayout;//未登录
    ImageView mDataIv;
    TextView mDataTv;

    // private TextView hintTv;//提示文字
    public static final int NO_DATA = 1;//无数据
    public static final int NO_NET = 2;//无网络
    public static final int NO_LOGIN = 3;//未登录

    private RetryListerner retryListener;//重试
    Context context;

    public ShowView(Context context) {
        super(context);
        initView(context);
    }

    public ShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_status, this);
        showDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);
        //     showNetworkLayout = (LinearLayout) view.findViewById(R.id.no_network_layout);
//        showLoginLayout = (LinearLayout) view.findViewById(R.id.no_login_layout);
//        mLoginTv = (GradientView) view.findViewById(R.id.m_login_tv);
        mRootLl = (LinearLayout) view.findViewById(R.id.m_root_ll);
        mDataIv = (ImageView) view.findViewById(R.id.m_data_iv);
        mDataTv = (TextView) view.findViewById(R.id.m_data_tv);
        view.setVisibility(GONE);
        mRootLl.setOnClickListener(this);
        //    mLoginTv.setOnClickListener(this);

    }

    public void setDataImg(int dataImg) {
        mDataIv.setImageResource(dataImg);
    }

    public void setDataTextColor(int color) {
        mDataTv.setTextColor(ContextCompat.getColor(context, color));
    }

    public void setDataTextStr(String str) {
        mDataTv.setText(str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.m_root_ll:
//                if(showNetworkLayout.getVisibility() == VISIBLE){
//                    retryListener.retry();//执行重试逻辑
//                }
                break;
//            case R.id.m_login_tv:
//                CommonUtils.goLogin((Activity) context);
//                break;
        }
    }

    /*设置重试监听*/
    public void setOnRetryListener(RetryListerner retryListener) {
        this.retryListener = retryListener;
    }

    public void show(int i) {
//        view.setVisibility(GONE);
        view.setVisibility(VISIBLE);
        switch (i) {
            case NO_DATA: /*暂无数据*/
                showDataLayout.setVisibility(VISIBLE);
                //      showNetworkLayout.setVisibility(GONE);
                //     showLoginLayout.setVisibility(GONE);
                break;
            case NO_NET: /*无网络*/
                showDataLayout.setVisibility(GONE);
                //     showNetworkLayout.setVisibility(VISIBLE);
                //      showLoginLayout.setVisibility(GONE);
                break;
            case NO_LOGIN: /*无网络*/
                showDataLayout.setVisibility(GONE);
                //       showNetworkLayout.setVisibility(GONE);
                //     showLoginLayout.setVisibility(VISIBLE);
                break;
        }
    }

    public void hide() {
        view.setVisibility(GONE);
    }

    public interface RetryListerner {
        void retry();
    }
}

