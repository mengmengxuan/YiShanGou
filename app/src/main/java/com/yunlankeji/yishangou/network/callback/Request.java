package com.yunlankeji.yishangou.network.callback;

import com.yunlankeji.yishangou.bean.WeChatPayBean;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by Snooker on on 2020/8/20
 * 封装请求的接口
 */
public interface Request {

    //获取验证码
    @POST("phoneVerificationCode/sendVerificationCode")
    Call<ResponseBean> sendVerificationCode(@Body ParamInfo paramInfo);

    //登录
    @POST("member/login")
    Call<ResponseBean<Data>> requestLogin(@Body ParamInfo paramInfo);

    //注册
    @POST("member/register")
    Call<ResponseBean> requestRegister(@Body ParamInfo paramInfo);

    //轮播图
    @POST("banner/queryList")
    Call<ResponseBean<List<Data>>> requestBanner(@Body ParamInfo paramInfo);

    //轮播图详情
    @POST("banner/queryOne")
    Call<ResponseBean<Data>> requestBannerDetail(@Body ParamInfo paramInfo);

    //获取首页分类
    @POST("merchantType/queryList")
    Call<ResponseBean<List<Data>>> requestHomeCategory(@Body ParamInfo paramInfo);

    //获取热销商家
    @POST("merchant/queryMerchantPage")
    Call<ResponseBean<Data>> requestHotMerchant(@Body ParamInfo paramInfo);

    //商家信息
    @POST("merchant/queryMerchant")
    Call<ResponseBean<Data>> requestMerchant(@Body ParamInfo paramInfo);

    //收货地址
    @POST("")
    Call<ResponseBean<List<Data>>> requestReceiverAddress(@Body ParamInfo paramInfo);

    //添加收货地址
    @POST("memberAdress/addMemberAdress")
    Call<ResponseBean> requestAddReceiverAddress(@Body ParamInfo paramInfo);

    //上传头像
    @POST("upload/saveImage")
    Call<ResponseBean<Data>> requestUploadImage(@Body ParamInfo paramInfo);

    //特惠专区
    @POST("merchantProduct/hotProduct")
    Call<ResponseBean<Data>> requestHotProduct(@Body ParamInfo paramInfo);

    //用户信息
    @POST("member/queryMember")
    Call<ResponseBean<Data>> requestMemberInfo(@Body ParamInfo paramInfo);

    //一线团队、二线团队
    @POST("memberInviteRecord/queryInviteList")
    Call<ResponseBean<Data>> requestInviteList(@Body ParamInfo paramInfo);

    //商品分类
    @POST("merchantCategory/getMerchantCategory")
    Call<ResponseBean<List<Data>>> requestMerchantCategory(@Body ParamInfo paramInfo);

    //根据分类获取商品
    @POST("merchantProduct/getMerchantProduct")
    Call<ResponseBean<List<Data>>> requestMerchantProduct(@Body ParamInfo paramInfo);

    //购物车
    @POST("/memberShopCart/updateShopCart")
    Call<ResponseBean<List<Data>>> requestShopCart(@Body ParamInfo paramInfo);

    //下级的下级
    @POST("memberInviteRecord/querySubordinate")
    Call<ResponseBean<Data>> requestSubordinate(@Body ParamInfo paramInfo);

    //获取系统参数
    @POST("systemConfig/updateSystemVersion")
    Call<ResponseBean<Data>> requestUpdateSystemVersion(@Body ParamInfo paramInfo);

    //订单列表(普通列表)
    @POST("memberOrder/queryPageList")
    Call<ResponseBean<Data>> requestQueryPageList(@Body ParamInfo paramInfo);

    //骑手订单详情
    @POST("memberOrder/queryOne")
    Call<ResponseBean<Data>> requestQueryRiderOrderDetail(@Body ParamInfo paramInfo);

    //骑手抢单
    @POST("memberOrder/grabbingOrder")
    Call<ResponseBean> requestGrabbingOrder(@Body ParamInfo paramInfo);

    //获取收货地址
    @POST("memberAdress/getMemberAdress")
    Call<ResponseBean<List<Data>>> requestMemberAddress(@Body ParamInfo paramInfo);

    //删除地址
    @POST("memberAdress/deleteMemberAdress")
    Call<ResponseBean> requestDeleteMemberAddress(@Body ParamInfo paramInfo);

    //编辑地址
    @POST("memberAdress/updateMemberAdress")
    Call<ResponseBean> requestUpdateMemberAddress(@Body ParamInfo paramInfo);

    //上传骑手坐标
    @POST("rider/updateRiderLngLat")
    Call<ResponseBean> requestUpdateRiderLngLat(@Body ParamInfo paramInfo);

    //关于我们
    @POST("systemConfig/updateSystemVersion")
    Call<ResponseBean<Data>> requestAboutUs(@Body ParamInfo paramInfo);

    //添加购物车
    @POST("memberShopCart/updateShopCart")
    Call<ResponseBean> requestUpdateShopCart(@Body ParamInfo paramInfo);

    //购物车
    @POST("memberShopCart/queryShopCartData")
    Call<ResponseBean<Data>> requestShopCartData(@Body ParamInfo paramInfo);

    //商家入驻
    @POST("merchant/addMerchant")
    Call<ResponseBean> requestAddMerchant(@Body ParamInfo paramInfo);

    //打烊开关
    @POST("merchant/updateMerchant")
    Call<ResponseBean> requestUpdateMerchant(@Body ParamInfo paramInfo);

    //提现查询
    @POST("memberCash/getMemberCash")
    Call<ResponseBean<List<Data>>> requestGetMemberCash(@Body ParamInfo paramInfo);

    //提现
    @POST("memberCash/addMemberCash")
    Call<ResponseBean> requestAddMemberCash(@Body ParamInfo paramInfo);

    //配送费
    @POST("systemConfig/queryShippingAccount")
    Call<ResponseBean> requestShippingAccount(@Body ParamInfo paramInfo);

    //生成订单
    @POST("memberOrder/createMemberOrder")
    Call<ResponseBean> requestCreateMemberOrder(@Body ParamInfo paramInfo);

    //支付宝支付
    @POST("aliPay/payOrder")
    Call<ResponseBean<Data>> requestAliPay(@Body ParamInfo paramInfo);

    //微信支付
    @POST("WXPay/payOrder")
    Call<ResponseBean<WeChatPayBean>> requestWeChatPay(@Body ParamInfo paramInfo);

    //提交订单页面，获取默认地址
    @POST("memberAdress/getIsDefault")
    Call<ResponseBean<Data>> requestDefaultAddress(@Body ParamInfo paramInfo);

    //订单详情
    @POST("memberOrder/queryOrderDetail")
    Call<ResponseBean<Data>> requestQueryOrderDetail(@Body ParamInfo paramInfo);

    //商家入驻回显
    @POST("merchant/queryMyMerchant")
    Call<ResponseBean<Data>> requestQueryMyMerchant(@Body ParamInfo paramInfo);

    //新增分类
    @POST("merchantCategory/addMerchantCategory")
    Call<ResponseBean> requestAddMerchantCategory(@Body ParamInfo paramInfo);

    //获取商铺分类
    @POST("merchantCategory/queryList")
    Call<ResponseBean<List<Data>>> requestMerchantCategoryList(@Body ParamInfo paramInfo);

    //新增商品
    @POST("merchantProduct/addMerchantProduct")
    Call<ResponseBean> requestAddMerchantProduct(@Body ParamInfo paramInfo);

    //修改商品
    @POST("merchantProduct/updateMerchantProduct")
    Call<ResponseBean> requestUpdateMerchantProduct(@Body ParamInfo paramInfo);

    //商品列表
    @POST("merchantProduct/queryList")
    Call<ResponseBean<List<Data>>> requestMyMerchantProductList(@Body ParamInfo paramInfo);

    //派单
    @POST("memberOrder/updateMemberOrderStatus")
    Call<ResponseBean> requestUpdateMemberOrderStatus(@Body ParamInfo paramInfo);

    //删除
    @POST("merchantProduct/deleteMerchantProduct")
    Call<ResponseBean> requestDeleteMerchantProduct(@Body ParamInfo paramInfo);

    //骑手入驻
    @POST("rider/addRider")
    Call<ResponseBean> requestAddRider(@Body ParamInfo paramInfo);

    //骑手信息
    @POST("rider/queryOne")
    Call<ResponseBean<Data>> requestQueryRider(@Body ParamInfo paramInfo);

    //跑腿运费计算
    @POST("memberOrder/getDistributionCash")
    Call<ResponseBean<Data>> requestGetDistributionCash(@Body ParamInfo paramInfo);

    //跑腿发单
    @POST("memberOrder/addRunningErrands")
    Call<ResponseBean> requestAddRunningErrands(@Body ParamInfo paramInfo);

    //获取跑腿订单状态
    @POST("memberOrder/getRunningErrands")
    Call<ResponseBean<List<Data>>> requestGetRunningErrands(@Body ParamInfo paramInfo);

    //删除分类
    @POST("merchantCategory/deleteMerchantMategory")
    Call<ResponseBean> requestDeleteMerchantMategory(@Body ParamInfo paramInfo);

    //修改易闪付协议状态
    @POST("member/updateAgreeStatus")
    Call<ResponseBean> requestUpdateAgreeStatus(@Body ParamInfo paramInfo);

    //修改资料
    @POST("member/updateMemberInfo")
    Call<ResponseBean> requestUpdateMemberInfo(@Body ParamInfo paramInfo);

    //新增忙碌时间
    @POST("merchantBusyTime/addMerchantBusyTime")
    Call<ResponseBean> requestAddMerchantBusyTime(@Body ParamInfo paramInfo);

    //获取忙碌时间
    @POST("merchantBusyTime/getMerchantBusyTime")
    Call<ResponseBean<List<Data>>> requestGetMerchantBusyTime(@Body ParamInfo paramInfo);

    //修改忙碌时间
    @POST("merchantBusyTime/updateMerchantBusyTime")
    Call<ResponseBean> requestUpdateMerchantBusyTime(@Body ParamInfo paramInfo);

    //删除忙碌时间
    @POST("merchantBusyTime/deleteMerchantBusyTime")
    Call<ResponseBean> requestDeleteMerchantBusyTime(@Body ParamInfo paramInfo);

    //商品详情
    @POST("merchantProduct/queryOne")
    Call<ResponseBean<Data>> requestProductDetail(@Body ParamInfo paramInfo);

    //点击再来一单，获取购物车数据
    @POST("memberOrder/buyOrderAgain")
    Call<ResponseBean<Data>> requestBuyOrderAgain(@Body ParamInfo paramInfo);

    //获取历史搜索
    @POST("memberSearch/queryList")
    Call<ResponseBean<List<Data>>> requestHistorySearch(@Body ParamInfo paramInfo);

    //删除历史搜索
    @POST("memberSearch/deleteAll")
    Call<ResponseBean> requestDeleteAll(@Body ParamInfo paramInfo);

    //清空购物车
    @POST("memberShopCart/deleteMemberShopCartBatch")
    Call<ResponseBean> requestDeleteShopCart(@Body ParamInfo paramInfo);

    //骑手取货是加减商品重量
    @POST("memberOrder/getMoney")
    Call<ResponseBean<Data>> requestGetMoney(@Body ParamInfo paramInfo);
}
