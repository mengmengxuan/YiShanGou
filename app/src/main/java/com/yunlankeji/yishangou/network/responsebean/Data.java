package com.yunlankeji.yishangou.network.responsebean;

import java.io.Serializable;
import java.util.List;

/**
 * Create by Snooker on 2020/8/20
 * Describe:数据解析类
 */
public class Data implements Serializable {
    //轮播图
    public String bannerCode;
    public String bannerName;
    public String bannerUrl;

    //登录
    public String agreeStatus;
    public String balanceAccount;
    public String createDt;
    public String id;
    public String detail;
    public String inviteCode;
    public String isMerchant;
    public String isRider;
    public String logo;
    public String memberCode;
    public String memberName;
    public String parentOneCode;
    public String parentSecondCode;
    public String phone;
    public String pwd;
    public String totalAccount;
    public String creatorCode;
    public String creatorName;
    public String riderCode;

    //分类
    public String merchantTypeCode;
    public String merchantTypeName;//分类名
    public String sort;

    //热销商家
    public String count;
    public int currPage;
    public int pageCount;
    public List<Data> data;
    public String pageSize;
    public String adress;
    public String areaCode;
    public String areaName;
    public String businessTime;
    public String cityCode;
    public String cityName;
    public String failReason;
    public String healthUrl;
    public String idCardFrontUrl;
    public String idCardNo;
    public String idCardReverseUrl;
    public String isBusiness;
    public String latitude;
    public String licenseUrl;
    public String longitude;
    public String merchantCode;
    public String merchantLogo;
    public String merchantName;
    public String merchantStatus;
    public String provinceCode;
    public String provinceName;
    public String realName;
    public String saleCount;//月销
    public String orderAccount;//起送
    public String shippingAccount; //配送费
    public String distance;//距离

    //特惠专区
    public String categoryCode;
    public String categoryName;
    public String isSaleOut;
    public String ishot;
    public String page;
    public String price;
    public String productCode;
    public String productLogo;
    public String productName;
    public String size;
    public String sku;
    public String stock;

    //商品分类
    public String status;
    public String num;

    //一线团队
    public String incomes;
    public String mouthnumbers;
    public String numbers;
    public String ysincomes;
    public String ysnumber;

    //获取收货地址
    public String isDefault;
    public String location;
    public String receiveName;
    public String receivePhone;

    //关于我们
    public String propertyDesc;
    public String propertyIndex;
    public String propertyKey;
    public String propertyValue;

    //购物车
    public List<Data> cartList;
    public List<Data> detailList;
    public String ids;
    public String orderNumber;
    public String orderStatus;
    public String startingFee;
    public String isBusy;// 0 非忙碌  1 忙绿

    //上传图片
    public String obj;
    //钱包
    public String cashAccount;
    public String cashStatus;

    public String orderStr;

    //订单
    public String riderName;
    public String remark;
    public String sendPhone;
    public String riderPhone;
    public String receiveAdress;
    public String receiveLatitude;
    public String receiveLongitude;
    public String sendLatitude;
    public String sendLongitude;
    public String riderLatitude;
    public String riderLongitude;

    public String riderLogo;
    public String orderType;//0 外卖  1 跑腿
    public String sendAdress;//
    public String sendName;//
    public String releaseTime;//发布时间
    public String receiveDistance;//距离你
    public String goodsType;
    public String weight;
    public String goodsCost;
    public String payStatus;
    public String sendDistance;
    public String startTime;
    public String endTime;
    public String seacrhName;
    public String riderStatus;
    public String packingMoney;

}
