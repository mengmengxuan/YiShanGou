package com.yunlankeji.yishangou.dialog;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.personal.baseutils.utils.Utils;
import com.personal.baseutils.widget.wheelview.OnWheelChangedListener;
import com.personal.baseutils.widget.wheelview.OnWheelScrollListener;
import com.personal.baseutils.widget.wheelview.WheelView;
import com.personal.baseutils.widget.wheelview.adapter.AbstractWheelTextAdapter1;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.bean.LocationInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16323 on 2017/9/22.
 */

public class ChooseCityDialog extends PopupWindow implements View.OnClickListener {

    private static final String TAG = "ChooseCityDialog";
    private Context context;
    private WheelView wvProvice;
    private WheelView wvCity;
    private WheelView wvArea;
    private TextView btnSure;
    private TextView btnCancel, mTitleTv;

    List<LocationInfo> provinceList = new ArrayList<>();
    List<List<LocationInfo>> cityList = new ArrayList<>();

    private OnAddressListener onAddressListener;

    private int maxTextSize = 20;
    private int minTextSize = 14;

    String privice, priviceId, city, cityId, area, areaId;
    boolean isCity;
    int priviceIndex = 0, cityIndex = 0, areaIndex = 0;

    private boolean issetdata = false;
    LocationInfo location;

    CalendarTextAdapter proviceAdapter, cityAdapter, areaAdapter;
    boolean isShowArea = true;
    private boolean isShowProvince = true;
    private boolean isShowCity = true;

    public void setShowArea(boolean showArea) {
        isShowArea = showArea;
        if (isShowArea) {
            wvArea.setVisibility(View.VISIBLE);
        } else {
            wvArea.setVisibility(View.GONE);
        }
    }

    public void setShowProvince(boolean showProvince) {
        isShowProvince = showProvince;
        if (isShowProvince) {
            wvProvice.setVisibility(View.VISIBLE);
        } else {
            wvProvice.setVisibility(View.GONE);
        }
    }

    public void setShowCity(boolean showCity) {
        isShowCity = showCity;
        if (isShowCity) {
            wvCity.setVisibility(View.VISIBLE);
        } else {
            wvCity.setVisibility(View.GONE);
        }
    }

    public void setOnAddressListener(OnAddressListener onAddressListener) {
        this.onAddressListener = onAddressListener;
    }

    public void setIssetdata(boolean issetdata) {
        this.issetdata = issetdata;
        if (issetdata) {
            ChooseCity();
        }
    }

    public ChooseCityDialog(final Context context) {
        super(context);
        this.context = context;
        View view = View.inflate(context, R.layout.dialog_wheel_city, null);
        wvProvice = view.findViewById(R.id.first_wv);
        wvCity = view.findViewById(R.id.second_wv);
        wvArea = view.findViewById(R.id.third_wv);
        btnSure = view.findViewById(R.id.m_sure_tv);
        btnCancel = view.findViewById(R.id.m_cancel_tv);
        mTitleTv = view.findViewById(R.id.m_title_tv);

        mTitleTv.setText("选择地区");

        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//		this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        String json = readJson();
        Gson gson = new Gson();
        location = gson.fromJson(json, LocationInfo.class);//对于javabean直接给出class实例

    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    private String readJson() {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(assetManager.open("city.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void ChooseCity() {
        Log.e("!@@!@!###$@#423", "location===2222222");
        privice = location.locationList.get(priviceIndex).name;
        city = location.locationList.get(priviceIndex).citylist.get(cityIndex).name;
        area = location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist.get(areaIndex).name;

        priviceId = location.locationList.get(priviceIndex).code;
        cityId = location.locationList.get(priviceIndex).citylist.get(cityIndex).code;
        areaId = location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist.get(areaIndex).code;
        Log.d(TAG, "ChooseCity: " + priviceId + "-" + cityId + "-" + "-" + areaId);

        proviceAdapter = new CalendarTextAdapter(context, location.locationList, priviceIndex, maxTextSize, minTextSize);
        wvProvice.setVisibleItems(5);
        wvProvice.setViewAdapter(proviceAdapter);
        wvProvice.setCurrentItem(priviceIndex);

        cityAdapter = new CalendarTextAdapter(context, location.locationList.get(priviceIndex).citylist, cityIndex, maxTextSize, minTextSize);
        wvCity.setVisibleItems(5);
        wvCity.setViewAdapter(cityAdapter);
        wvCity.setCurrentItem(cityIndex);

        if (isShowArea) {
            areaAdapter = new CalendarTextAdapter(context, location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist, areaIndex, maxTextSize, minTextSize);
            wvArea.setVisibleItems(5);
            wvArea.setViewAdapter(areaAdapter);
            wvArea.setCurrentItem(areaIndex);
        }

        wvProvice.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) proviceAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, proviceAdapter);
                priviceIndex = wheel.getCurrentItem();
                privice = currentText;
                //省id
                priviceId = location.locationList.get(wheel.getCurrentItem()).code;

                cityAdapter = new CalendarTextAdapter(context, location.locationList.get(wheel.getCurrentItem()).citylist, 0, maxTextSize, minTextSize);
                wvCity.setVisibleItems(5);
                wvCity.setViewAdapter(cityAdapter);
                wvCity.setCurrentItem(0);

                if (location.locationList != null && location.locationList.size() > 0 && location.locationList.get(wheel.getCurrentItem()).citylist != null && location.locationList.get(wheel.getCurrentItem()).citylist.size() > 0) {
                    city = location.locationList.get(wheel.getCurrentItem()).citylist.get(0).name;
                    //市id
                    cityId = location.locationList.get(wheel.getCurrentItem()).citylist.get(0).code;
                }

                if (isShowArea) {

//                    if (location.locationList.get(priviceIndex).citylist != null && location.locationList.get(priviceIndex).citylist.size() > 0) {

                    if (location.locationList != null && location.locationList.size() > 0
                            && location.locationList.get(priviceIndex).citylist != null && location.locationList.get(priviceIndex).citylist.size() > 0
                            && location.locationList.get(priviceIndex).citylist.get(0).arealist != null && location.locationList.get(priviceIndex).citylist.get(0).arealist.size() > 0
                            && location.locationList.get(priviceIndex).citylist != null && location.locationList.get(priviceIndex).citylist.size() > 0) {

                        areaAdapter = new CalendarTextAdapter(context, location.locationList.get(priviceIndex).citylist.get(0).arealist, 0, maxTextSize, minTextSize);
                        wvArea.setVisibleItems(5);
                        wvArea.setViewAdapter(areaAdapter);
                        wvArea.setCurrentItem(0);

                        area = location.locationList.get(priviceIndex).citylist.get(0).arealist.get(0).name;
                        //地区id
                        areaId = location.locationList.get(priviceIndex).citylist.get(0).arealist.get(0).code;
                    }
                }
            }
        });

        wvProvice.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) proviceAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, proviceAdapter);
            }
        });

        wvCity.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                city = currentText;
                setTextviewSize(currentText, cityAdapter);
                cityIndex = wheel.getCurrentItem();
                cityId = location.locationList.get(priviceIndex).citylist.get(wheel.getCurrentItem()).code;

                if (isShowArea) {
                    areaAdapter = new CalendarTextAdapter(context, location.locationList.get(priviceIndex).citylist.get(wheel.getCurrentItem()).arealist, 0, maxTextSize, minTextSize);
                    wvArea.setVisibleItems(5);
                    wvArea.setViewAdapter(areaAdapter);
                    wvArea.setCurrentItem(0);
                    if (location.locationList.get(priviceIndex).citylist.get(wheel.getCurrentItem()).arealist.size() > 0) {
                        area = location.locationList.get(priviceIndex).citylist.get(wheel.getCurrentItem()).arealist.get(0).name;
                        areaId = location.locationList.get(priviceIndex).citylist.get(wheel.getCurrentItem()).arealist.get(0).code;
                    } else {
                        area = "";
                        areaId = "";
                    }
                }

            }
        });

        wvCity.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, cityAdapter);
            }
        });

        if (isShowArea) {
            wvArea.addChangingListener(new OnWheelChangedListener() {

                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    // TODO Auto-generated method stub
                    String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
                    area = currentText;
                    if (location.locationList != null && location.locationList.size() > 0
                            && location.locationList.get(priviceIndex).citylist != null && location.locationList.get(priviceIndex).citylist.size() > 0
                            && location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist != null && location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist.size() > 0) {
                        areaId = location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist.get(wheel.getCurrentItem()).code;
                        setTextviewSize(currentText, areaAdapter);
                    }
                }
            });

            wvArea.addScrollingListener(new OnWheelScrollListener() {

                @Override
                public void onScrollingStarted(WheelView wheel) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    // TODO Auto-generated method stub
                    String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
                    setTextviewSize(currentText, areaAdapter);
                }
            });
        }

    }

    public void setIndex(String privice, String city, String area) {
        this.privice = privice;
        this.city = city;
        priviceIndex = setPrivice(privice);
        cityIndex = setCity(city, priviceIndex);
        if (!Utils.isEmpty(area)) {
            areaIndex = setArea(area, priviceIndex, cityIndex);
        }
        ChooseCity();
    }

    private int setPrivice(String privice) {
        int priviceIndex = 0;
        for (int i = 0; i < location.locationList.size(); i++) {
            if (location.locationList.get(i).name.equals(privice)) {
                priviceIndex = i;
                break;
            }
        }
        return priviceIndex;
    }

    private int setCity(String city, int priviceIndex) {
        int cityIndex = 0;
        for (int i = 0; i < location.locationList.get(priviceIndex).citylist.size(); i++) {
            if (location.locationList.get(priviceIndex).citylist.get(i).name.equals(city)) {
                cityIndex = i;
                break;
            }
        }
        return cityIndex;
    }

    private int setArea(String area, int priviceIndex, int cityIndex) {
        int areaIndex = 0;
        for (int i = 0; i < location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist.size(); i++) {
            if (location.locationList.get(priviceIndex).citylist.get(cityIndex).arealist.get(i).name.equals(area)) {
                areaIndex = i;
                break;
            }
        }
        return areaIndex;
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter1 {
        List<LocationInfo> list;

        protected CalendarTextAdapter(Context context, List<LocationInfo> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            if (list.size() > 0) {
                return list.get(index).name + "";
            } else {
                return "";
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (area.contains("(省直辖)")) {
                area = area.replace("(省直辖)", "");
                city = area;
            } else if (Utils.isEmpty(area)) {
                area = city;
            }
            Log.e("!@@!@!###$@#423", "privice==" + privice);
            Log.e("!@@!@!###$@#423", "city==" + city);
            Log.e("!@@!@!###$@#423", "area==" + area);
            onAddressListener.onClick(privice, priviceId, city, cityId, area, areaId);
        } else {
            dismiss();
        }
        dismiss();
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }

    public interface OnAddressListener {
        public void onClick(String privice, String priviceId, String city, String cityId, String area, String areaId);
    }

}
