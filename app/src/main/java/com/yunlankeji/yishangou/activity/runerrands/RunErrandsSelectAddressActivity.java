package com.yunlankeji.yishangou.activity.runerrands;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.SearchResultAdapter;
import com.yunlankeji.yishangou.dialog.LoadingDialog;
import com.yunlankeji.yishangou.globle.ResultCode;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RunErrandsSelectAddressActivity extends AppCompatActivity implements LocationSource, PoiSearch.OnPoiSearchListener,
        AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {

    private static final String TAG = "RunAddressActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root

    @BindView(R.id.m_search_et)
    EditText mSearchEt;
    @BindView(R.id.m_search_iv)
    ImageView mSearchIv;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.map)
    MapView mapView;

    protected LoadingDialog mLoadingDialog;
    private AMap aMap;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private String[] items = {"住宅区", "学校", "楼宇", "商场"};

    private Marker locationMarker;

    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;

    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据

    private String searchType = items[0];
    //    private String searchKey = "c29a2f46f24fdf2fc20e8d7256e3e019", city;
    private String searchKey = "";
    private LatLonPoint searchLatlonPoint;
    private List<PoiItem> resultData;

    private SearchResultAdapter searchResultAdapter;

    private boolean isItemClickAction;
    //  private List<Tip> autoTips;
    private boolean isfirstinput = true;
    private PoiItem firstItem;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_run_errands_select_address);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        mLoadingDialog = new LoadingDialog(this);
        ConstantUtil.setStatusBarFullTransparent(this);
        //设置状态栏文字颜色及图标为深色
        getWindow().getDecorView().setSystemUiVisibility(View
                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initView();
    }

    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("选择地址");

        resultData = new ArrayList<>();

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                mLatitude = cameraPosition.target.latitude;
                mLongitude = cameraPosition.target.longitude;

                Log.d(TAG, "onCameraChangeFinish  mLatitude: " + mLatitude);
                Log.d(TAG, "onCameraChangeFinish  mLongitude: " + mLongitude);

                searchLatlonPoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                Log.d(TAG, "onCameraChangeFinish+++++++++: " + cameraPosition.target.latitude + "----------" + cameraPosition.target.longitude);
                if (!isItemClickAction) {
                    geoAddress();
                    startJumpAnimation();
                }
                isItemClickAction = false;
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                addMarkerInScreenCenter(null);
            }
        });

        searchResultAdapter = new SearchResultAdapter(this);
        listview.setAdapter(searchResultAdapter);
        listview.setOnItemClickListener(onItemClickListener);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

        searchResultAdapter = new SearchResultAdapter(this);
        listview.setAdapter(searchResultAdapter);
        listview.setOnItemClickListener(onItemClickListener);

        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchKey = mSearchEt.getText().toString();
                doSearchQuery();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
    }

    @OnClick({R.id.m_back_iv, R.id.m_search_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_search_iv:
                mLoadingDialog.showDialog();
                doSearchQuery();
                mLoadingDialog.hideDialog();
                break;
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
//        Log.i("MY", "onLocationChanged");
        if (mListener != null && amapLocation != null) {
          /*  if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {*/
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);
//                LatLng curLatlng = new LatLng(Double.parseDouble(Api.latitude), Double.parseDouble(Api.longitude));

                LatLng curLatlng = new LatLng(searchLatlonPoint.getLatitude(), searchLatlonPoint.getLongitude());

                String city = amapLocation.getCity();
                Log.d(TAG, "onLocationChanged: " + city);

//
//                searchLatlonPoint = new LatLonPoint(curLatlng.latitude, curLatlng.longitude);
//                city = amapLocation.getCity();
//                mCityTv.setText(city);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16f));
                doSearchQuery();
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setOnceLocation(true);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 响应逆地理编码
     */
    public void geoAddress() {
//        Log.i("MY", "geoAddress"+ searchLatlonPoint.toString());
        showDialog();
        if (searchLatlonPoint != null) {
            RegeocodeQuery query = new RegeocodeQuery(searchLatlonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            geocoderSearch.getFromLocationAsyn(query);
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
//        LatLonPoint latLonPoint = new LatLonPoint();// 116.472995,39.993743
        searchKey = mSearchEt.getText().toString();
//        Log.i("MY", "doSearchQuery");
        currentPage = 0;
//        query = new PoiSearch.Query(searchKey, "", Api.city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        //解决在地图上滑动时，滑出该区域无搜索结果的问题
        query = new PoiSearch.Query(searchKey, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setCityLimit(true);
        query.setPageSize(20);
        query.setPageNum(currentPage);

        if (searchLatlonPoint != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(searchLatlonPoint, 1000, true));//
            poiSearch.searchPOIAsyn();
        }
    }

    /**
     * POI搜索结果回调
     *
     * @param poiResult  搜索结果
     * @param resultCode 错误码
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {
                if (poiResult.getQuery().equals(query)) {
                    poiItems = poiResult.getPois();
                    if (poiItems == null || poiItems.size() <= 0) {
                        Toast.makeText(this, "无搜索结果", Toast.LENGTH_SHORT).show();
                    }
                    updateListview(poiItems);
                }
            } else {
                Toast.makeText(this, "无搜索结果", Toast.LENGTH_SHORT).show();
                updateListview(poiItems);
            }
        }
    }

    @Override
    public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {

    }

    /**
     * 更新列表中的item
     *
     * @param poiItems
     */
    private void updateListview(List<PoiItem> poiItems) {
        resultData.clear();
        searchResultAdapter.setSelectedPosition(-1);
        //    resultData.add(firstItem);
        resultData.addAll(poiItems);

        searchResultAdapter.setData(resultData);
        searchResultAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onPoiItemSearched(PoiItem poiItem, int i) {
//
//    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position != searchResultAdapter.getSelectedPosition()) {
                PoiItem poiItem = (PoiItem) searchResultAdapter.getItem(position);
                LatLng curLatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());

                isItemClickAction = true;
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16f));
                searchResultAdapter.setSelectedPosition(position);
                searchResultAdapter.notifyDataSetChanged();

                Intent intent = new Intent();
                intent.putExtra("latitude", poiItem.getLatLonPoint().getLatitude() + "");
                intent.putExtra("longitude", poiItem.getLatLonPoint().getLongitude() + "");
                intent.putExtra("area", poiItem.getTitle());
                intent.putExtra("address", poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet());
                setResult(ResultCode.RESULT_CODE_ADDRESS_FROM_MAP, intent);
                finish();
            }
        }
    };

    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在加载...");
        progDialog.show();
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void addMarkerInScreenCenter(LatLng locationLatLng) {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        locationMarker.setZIndex(1);

    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {
        if (locationMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = locationMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this, 125);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            locationMarker.setAnimation(animation);
            //开始动画
            locationMarker.startAnimation();

        } else {
            Log.e("ama", "screenMarker is null");
        }
    }

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String address = result.getRegeocodeAddress().getProvince() + result.getRegeocodeAddress().getCity() + result.getRegeocodeAddress().getDistrict() + result.getRegeocodeAddress().getTownship();
                firstItem = new PoiItem("regeo", searchLatlonPoint, address, address);
                doSearchQuery();
            }
        } else {
            Toast.makeText(this, "error code is " + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}