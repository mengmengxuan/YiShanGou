package com.yunlankeji.yishangou.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.personal.baseutils.utils.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.MainActivity;
import com.yunlankeji.yishangou.activity.business.BusinessHostActivity;
import com.yunlankeji.yishangou.activity.business.VerifyResultActivity;
import com.yunlankeji.yishangou.activity.home.BannerDetailActivity;
import com.yunlankeji.yishangou.activity.home.CategorizeActivity;
import com.yunlankeji.yishangou.activity.home.ChooseCityActivity;
import com.yunlankeji.yishangou.activity.home.FoodDetailActivity;
import com.yunlankeji.yishangou.activity.home.MoreCategoryActivity;
import com.yunlankeji.yishangou.activity.home.SearchActivity;
import com.yunlankeji.yishangou.activity.home.ShareRegisterActivity;
import com.yunlankeji.yishangou.activity.home.SpecialZoneActivity;
import com.yunlankeji.yishangou.activity.home.StoreDetailActivity;
import com.yunlankeji.yishangou.activity.mine.ChooseAreaActivity;
import com.yunlankeji.yishangou.activity.mine.RiderSettleActivity;
import com.yunlankeji.yishangou.adapter.CategoryAdapter;
import com.yunlankeji.yishangou.adapter.HotSellerAdapter;
import com.yunlankeji.yishangou.adapter.SpecialCommodityAdapter;
import com.yunlankeji.yishangou.dialog.ChooseCityDialog;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.GetHttpConnectionData;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/21
 * Describe:首页
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";
    @BindView(R.id.banner)
    ConvenientBanner banner;
    @BindView(R.id.m_root_ll)
    LinearLayout mRootLl;
    @BindView(R.id.m_area_tv)
    TextView mAreaTv;
    @BindView(R.id.m_search_ll)
    LinearLayout mSearchLl;
    @BindView(R.id.m_category_rv)
    RecyclerView mCategoryRv;
    @BindView(R.id.m_snap_up_fl)
    FrameLayout mSnapUpFl;
    @BindView(R.id.m_rider_settle_iv)
    ImageView mRiderSettleIv;
    @BindView(R.id.m_merchant_settle_iv)
    ImageView mMerchantSettleIv;
    @BindView(R.id.m_more_tv)
    TextView mMoreTv;
    @BindView(R.id.m_special_zone_iv)
    ImageView mSpecialZoneIv;
    @BindView(R.id.m_special_commodity_rv)
    RecyclerView mSpecialCommodityRv;
    @BindView(R.id.m_hot_seller_rv)
    RecyclerView mHotSellerRv;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private SpecialCommodityAdapter mSpecialCommodityAdapter;
    private List<Data> categoryItems = new ArrayList<>();//分类数据源
    private List<Data> specialCommodityItems = new ArrayList<>();//特惠专区数据源
    private List<Data> hotSellerItems = new ArrayList<>();//热销商家数据源
    private HotSellerAdapter mHotSellerAdapter;
    private CategoryAdapter mCategoryAdapter;
    private List<Data> bannerItems = new ArrayList<>();//轮播图集合
    private int currPage = 1;
    private LocationManager locationManager;

    private List<HotCity> hotCities = new ArrayList<>();
    private String mCityName;//选择的城市名

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(getActivity(), mRootLl);

        //获取经纬度
        location();

        //抢购图片
        mSnapUpFl.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity()).asDrawable().load(R.mipmap.icon_snap_up).into(new SimpleTarget<Drawable>(mSnapUpFl.getWidth() / 2, mSnapUpFl.getHeight()) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mSnapUpFl.setBackground(resource);
                    }
                });
            }
        });

        //骑手入驻图片
        mRiderSettleIv.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity()).asBitmap().load(R.mipmap.icon_rider_settle).into(new SimpleTarget<Bitmap>(mRiderSettleIv.getWidth(), mRiderSettleIv.getHeight()) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mRiderSettleIv.setImageBitmap(resource);
                    }
                });
            }
        });

        //商家入驻图片
        mMerchantSettleIv.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity()).asBitmap().load(R.mipmap.icon_merchant_settle).into(new SimpleTarget<Bitmap>(mMerchantSettleIv.getWidth(), mMerchantSettleIv.getHeight()) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mMerchantSettleIv.setImageBitmap(resource);
                    }
                });
            }
        });

        //特惠专区的图片
        mSpecialZoneIv.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity()).asBitmap().load(R.mipmap.icon_home_special_zone).into(new SimpleTarget<Bitmap>(mSpecialZoneIv.getWidth() / 2, mSpecialZoneIv.getHeight()) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mSpecialZoneIv.setImageBitmap(resource);
                    }
                });
            }
        });

        //分类列表
        mCategoryAdapter = new CategoryAdapter(getActivity());
        mCategoryAdapter.setItems(categoryItems);
        mCategoryAdapter.setFrom("home");
        mCategoryRv.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mCategoryRv.setAdapter(mCategoryAdapter);
        //点击分类列表
        mCategoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {

            }

            @Override
            public void onNormalItemClicked(View view, int position) {
                //点击跳转到店铺列表页面
                Intent intent = new Intent();
                intent.setClass(getActivity(), CategorizeActivity.class);
                intent.putExtra("merchantTypeCode", categoryItems.get(position).merchantTypeCode);
                intent.putExtra("title", categoryItems.get(position).merchantTypeName);
                startActivity(intent);
            }

            @Override
            public void onLastItemClicked(View view, int position) {
                //点击“更多”，跳转到更多分类页面
                String cityName = mAreaTv.getText().toString();
                Intent intent = new Intent();
                intent.setClass(getActivity(), MoreCategoryActivity.class);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
            }
        });

        //特惠专区的列表
        mSpecialCommodityAdapter = new SpecialCommodityAdapter(getActivity());
        mSpecialCommodityAdapter.setItems(specialCommodityItems);
        mSpecialCommodityRv.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        mSpecialCommodityRv.setAdapter(mSpecialCommodityAdapter);
        mSpecialCommodityAdapter.setOnItemClickListener(new SpecialCommodityAdapter.OnItemClickListener() {
            @Override
            public void onNormalItemClicked(View view, int position) {
                doActivity(FoodDetailActivity.class, specialCommodityItems.get(position).id, "id", "home", "from");
            }

            @Override
            public void onBuyItemClicked(View view, int position) {
                doActivity(FoodDetailActivity.class);
            }
        });

        //热销商家列表
        mHotSellerAdapter = new HotSellerAdapter(getActivity());
        mHotSellerAdapter.setItems(hotSellerItems);
        mHotSellerRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHotSellerRv.setAdapter(mHotSellerAdapter);
        mHotSellerAdapter.setOnItemClickListener(new HotSellerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), StoreDetailActivity.class);
                intent.putExtra("merchantCode", hotSellerItems.get(position).merchantCode);
                startActivity(intent);
            }
        });

        //下来刷新和上拉加载
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currPage++;
                requestHotSeller();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currPage = 1;
                if (hotSellerItems != null) {
                    hotSellerItems.clear();
                }
                requestHotSeller();
            }
        });
    }

    /**
     * 获取当前定位城市
     */
    private void location() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            setLocation(location);
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);

//        saveLocation(location);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            setLocation(location);
        }
    };

    /**
     * 设置位置
     *
     * @param location
     */
    private void setLocation(Location location) {
        if (location != null) {
            String latitude = location.getLatitude() + "";
            String longitude = location.getLongitude() + "";

            SPUtils.put(getActivity(), "latitude", latitude);
            SPUtils.put(getActivity(), "longitude", longitude);

            String url = "http://api.map.baidu.com/geocoder/v2/?ak=pPGNKs75nVZPloDFuppTLFO3WXebPgXg&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0";
            new MyAsyncTask(url).execute();
        }

    }

    class MyAsyncTask extends AsyncTask {
        String url = null;//要请求的网址
        String str = null;//服务器返回的数据
        String address = null;

        public MyAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            str = GetHttpConnectionData.getData(url);
            return null;
        }

        /**
         * 根据首页获取的定位城市获取首页数据
         *
         * @param o
         */
        @Override
        protected void onPostExecute(Object o) {
            try {
                if (str != null) {
                    str = str.replace("renderReverse&&renderReverse", "");
                    str = str.replace("(", "");
                    str = str.replace(")", "");
                    JSONObject jsonObject = new JSONObject(str);
                    JSONObject address = jsonObject.getJSONObject("result");
                    String city = address.getString("formatted_address");
                    String district = address.getString("sematic_description");
                    try {
                        if (!TextUtils.isEmpty(city)) {
                            if (city.contains("省")) {
                                String[] sStr = city.split("省");
                                if (!TextUtils.isEmpty(sStr[1])) {
                                    String[] cityStr = sStr[1].split("市");
                                    if (!TextUtils.isEmpty(cityStr[0])) {
                                        if (TextUtils.isEmpty(cityStr[0])) {
                                            //SPUtil.get("city", "合肥").toString()
                                            mAreaTv.setText(SPUtils.get(getActivity(), "city", "合肥").toString());
                                        } else {
                                            mAreaTv.setText(cityStr[0]);
                                            SPUtils.put(getActivity(), "city", cityStr[0]);
                                            SPUtils.put(getActivity(), "isLocationCity", true);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(o);
        }
    }

    private Criteria getCriteria() {
        // TODO Auto-generated method stub
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setSpeedRequired(false);
        c.setCostAllowed(false);
        c.setBearingRequired(false);
        c.setAltitudeRequired(false);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return c;
    }

   /* private void saveLocation(Location location) {
        if (location != null) {
            String latitude = location.getLatitude() + "";
            String longitude = location.getLongitude() + "";
            SPUtils.put(getActivity(), "latitude", latitude);
            SPUtils.put(getActivity(), "longitude", longitude);
        }
    }*/

    @Override
    protected void initData() {
        //轮播图
        requestBanner();
        //特惠专区
        requestHotProduct();
        //获取分类
        requestHomeCategory();
        //热销商家
        requestHotSeller();
    }

    /**
     * 热销商家
     */
    private void requestHotSeller() {
        showLoading();

        String latitude = (String) SPUtils.get(getActivity(), "latitude", "");
        String longitude = (String) SPUtils.get(getActivity(), "longitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.page = currPage + "";
        paramInfo.size = "10";
        paramInfo.latitude = latitude;
        paramInfo.longitude = longitude;
        paramInfo.orderByType = "saleCount";
        paramInfo.cityName = mCityName;

        LogUtil.d(TAG, "latitude --> " + latitude);
        LogUtil.d(TAG, "longitude --> " + longitude);
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestHotMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "热销商家：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;

                if (data != null) {
                    hotSellerItems.addAll(data.data);
                    mHotSellerAdapter.notifyDataSetChanged();

                    if (data.currPage >= data.pageCount) {
                        //不能加载更多了
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    } else {
                        //加载更多
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }
                    }
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });

    }

    /**
     * 获取分类
     */
    private void requestHomeCategory() {
        ParamInfo paramInfo = new ParamInfo();
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestHomeCategory(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "首页分类：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (data != null && data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        if (i < 10) {
                            categoryItems.add(data.get(i));
                        }
                    }
                }
                mCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    /**
     * 获取轮播图
     */
    private void requestBanner() {
        ParamInfo paramInfo = new ParamInfo();
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestBanner(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "轮播图：" + JSON.toJSONString(response.data));
                bannerItems = (List<Data>) response.data;
                banner.setPageIndicator(new int[]{R.mipmap.icon_indicator_normal,
                        R.mipmap.icon_indicator_checked});
                banner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

                banner.setPages(new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, bannerItems).setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (!Utils.isFastDoubleClick()) {
                            Intent intent = new Intent();
                            if (position == 0) {
                                intent.setClass(getActivity(), ShareRegisterActivity.class);
                            } else {
                                intent.setClass(getActivity(), BannerDetailActivity.class);
                                intent.putExtra("id", bannerItems.get(position).id);
                            }
                            startActivity(intent);
                        }
                    }
                }).startTurning(3000);
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, msg);
            }
        });
    }

    /**
     * 特惠专区
     */
    private void requestHotProduct() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.page = "1";
        paramInfo.size = "10";
        paramInfo.cityName = mCityName;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestHotProduct(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "特惠专区：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    specialCommodityItems.addAll(data.data);
                    mSpecialCommodityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, msg);
            }
        });
    }

    //为了方便改写，来实现复杂布局的切换
    private static class LocalImageHolderView implements Holder<Data> {
        private ImageView imageView;

        @Override
        public View createView(Context context) { //你可以通过layout文件来创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, Data data) {
            Glide.with(context)
                    .load(data.bannerUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(imageView);
        }
    }

    @OnClick({R.id.m_search_ll, R.id.m_rider_settle_iv, R.id.m_merchant_settle_iv, R.id.m_area_tv, R.id.m_more_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_area_tv:
                String city = (String) SPUtils.get(getActivity(), "city", "");
                /*intent.setClass(getActivity(), ChooseCityActivity.class);
                startActivity(intent);*/
                CityPicker.from(getActivity())
                        .setLocatedCity(null)
                        .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                mAreaTv.setText(String.format("%s", data.getName()));
                                //将选择的城市存进sp
                                SPUtils.put(getActivity(), "chooseCity", data.getName());

                                //选择的城市名
                                mCityName = data.getName();
                                //判断选择的城市和是否为当前定位的城市
                                //当前城市
                                String locationCity = (String) SPUtils.get(getActivity(), "city", "");

                                if (mCityName.equals(locationCity)) {
                                    //选择的城市是当前定位的城市，则存true
                                    SPUtils.put(getActivity(), "isLocationCity", true);
                                } else {
                                    //选择的城市不是当前定位的城市，则存false
                                    SPUtils.put(getActivity(), "isLocationCity", false);
                                }
                                //调接口，获取特惠专区和热销商家
                                if (specialCommodityItems != null) {
                                    specialCommodityItems.clear();
                                }
                                requestHotProduct();

                                if (hotSellerItems != null) {
                                    hotSellerItems.clear();
                                }
                                requestHotSeller();
                            }

                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onLocate() {
                                //开始定位，这里模拟一下定位
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.from(getActivity()).locateComplete(new LocatedCity(city, "未知",
                                                "0"), LocateState.SUCCESS);
                                    }
                                }, 3000);
                            }
                        })
                        .show();

                break;
            case R.id.m_search_ll://点击搜索框
                intent.setClass(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.m_rider_settle_iv://骑手入驻
                if ("0".equals(Global.isRider)) {
                    //不是骑手，跳转到骑手入驻页面
                    intent.setClass(getActivity(), RiderSettleActivity.class);
                    startActivity(intent);
                } else if ("1".equals(Global.isRider)) {
                    //是骑手，获取入驻状态，
                    //判断是否需要跳转至审核成功页面
                /*    Boolean jumpToStore = (Boolean) SPUtils.get(getActivity(), "jumpToRider", false);
                    if (jumpToStore) {
                        //直接进入骑手订单大厅
                        intent.setClass(getActivity(), RiderOrderCenterActivity.class);
                    } else {*/
                    //审核成功
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "1");
                    intent.putExtra("page", "home_rider");
//                    }
                    startActivity(intent);

                } else if ("2".equals(Global.isRider)) {
                    //审核中
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "2");
                    intent.putExtra("page", "home_rider");
                    startActivity(intent);

                } else if ("3".equals(Global.isRider)) {
                    //审核失败
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "3");
                    intent.putExtra("page", "home_rider");
                    startActivity(intent);
                }

                break;
            case R.id.m_merchant_settle_iv://商家入驻

                if ("0".equals(Global.isMerchant)) {
                    //不是商家，跳转到商家入驻页面
                    intent.setClass(getActivity(), BusinessHostActivity.class);
                    startActivity(intent);
                } else if ("1".equals(Global.isMerchant)) {
                    //是商家，获取入驻状态，
                    //判断是否需要跳转至审核成功页面
                   /* Boolean jumpToStore = (Boolean) SPUtils.get(getActivity(), "jumpToStore", false);
                    if (jumpToStore) {
                        //直接进入店铺
                        intent.setClass(getActivity(), BusinessOrderCenterActivity.class);
                    } else {*/
                    //审核成功
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "1");
                    intent.putExtra("page", "home_business");
//                    }
                    startActivity(intent);

                } else if ("2".equals(Global.isMerchant)) {
                    //审核中
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "2");
                    intent.putExtra("page", "home_business");
                    startActivity(intent);

                } else if ("3".equals(Global.isMerchant)) {
                    //审核失败
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "3");
                    intent.putExtra("page", "home_business");
                    startActivity(intent);
                }
                break;
            case R.id.m_more_tv://特惠专区，查看更多
                intent.setClass(getActivity(), SpecialZoneActivity.class);
                startActivity(intent);
                break;

        }
    }

}

