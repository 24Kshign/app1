package com.maihaoche.volvo.server;

import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.commonbiz.module.dto.PagerResponse;
import com.maihaoche.volvo.dao.po.WarehouseVO;
import com.maihaoche.volvo.server.dto.CarBaseInfoOnMailVO;
import com.maihaoche.volvo.server.dto.CarDetailVO;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.server.dto.StocktakeDetailCarVO;
import com.maihaoche.volvo.server.dto.StocktakeDetailVO;
import com.maihaoche.volvo.server.dto.StocktakeRecordVO;
import com.maihaoche.volvo.server.dto.request.AreaPositionMoveRequest;
import com.maihaoche.volvo.server.dto.request.CarIdPageRequest;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.server.dto.request.CheckUpdateRequestDTO;
import com.maihaoche.volvo.server.dto.request.LoginRequestDTO;
import com.maihaoche.volvo.server.dto.request.NotifyLiveClosedRequest;
import com.maihaoche.volvo.server.dto.request.NotifyLiveConnectedRequest;
import com.maihaoche.volvo.server.dto.request.ReportAbnInfoRequest;
import com.maihaoche.volvo.server.dto.request.SendProcedureRequest;
import com.maihaoche.volvo.server.dto.request.StartCheckRequest;
import com.maihaoche.volvo.server.dto.request.StocktakeRecordIdRequest;
import com.maihaoche.volvo.server.dto.request.UploadDataRequest;
import com.maihaoche.volvo.server.dto.request.UploadRequestDTO;
import com.maihaoche.volvo.server.dto.request.UploadStocktakeDetailsRequest;
import com.maihaoche.volvo.server.dto.request.WarehouseIdPageRequest;
import com.maihaoche.volvo.server.dto.response.CheckResultVO;
import com.maihaoche.volvo.server.dto.response.CheckUpdateResponseDTO;
import com.maihaoche.volvo.server.dto.response.LiveCheckInfoVO;
import com.maihaoche.volvo.server.dto.response.LoginResponse;
import com.maihaoche.volvo.server.dto.response.ToStocktakeDataResponse;
import com.maihaoche.volvo.ui.car.domain.CheckSeeCodeRequest;
import com.maihaoche.volvo.ui.car.domain.DepartCheck;
import com.maihaoche.volvo.ui.car.domain.DepartingCarList;
import com.maihaoche.volvo.ui.car.domain.OutStorageDetailRequest;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.domain.OutStorageRequest;
import com.maihaoche.volvo.ui.car.domain.OutstorageListRequest;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetail;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetailRequest;
import com.maihaoche.volvo.ui.common.chart.StaticRequest;
import com.maihaoche.volvo.ui.common.chart.StaticticsData;
import com.maihaoche.volvo.ui.common.daomain.ApplyBatchKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.ApplyKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.BindCodeRequest;
import com.maihaoche.volvo.ui.common.daomain.BindKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.CancelApplyKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.CarAreaInfo;
import com.maihaoche.volvo.ui.common.daomain.CheckCarResponse;
import com.maihaoche.volvo.ui.common.daomain.Customer;
import com.maihaoche.volvo.ui.common.daomain.GaoDeResponse;
import com.maihaoche.volvo.ui.common.daomain.GraphicQuery;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.common.daomain.OccupyInfo;
import com.maihaoche.volvo.ui.common.daomain.OrderCarList;
import com.maihaoche.volvo.ui.common.daomain.OrderCarListRequest;
import com.maihaoche.volvo.ui.common.daomain.PayDetail;
import com.maihaoche.volvo.ui.common.daomain.PayUrlRequest;
import com.maihaoche.volvo.ui.common.daomain.PhotoConfigForm;
import com.maihaoche.volvo.ui.common.daomain.QRCodeRequest;
import com.maihaoche.volvo.ui.common.daomain.QueryOrderRequest;
import com.maihaoche.volvo.ui.common.daomain.SearchRequest;
import com.maihaoche.volvo.ui.common.daomain.SearchResultInfo;
import com.maihaoche.volvo.ui.instorage.daomain.BrandNewInfo;
import com.maihaoche.volvo.ui.instorage.daomain.BrandSeriesInfo;
import com.maihaoche.volvo.ui.instorage.daomain.CheckCarRequest;
import com.maihaoche.volvo.ui.instorage.daomain.InstorageListRequest;
import com.maihaoche.volvo.ui.instorage.daomain.InstorageRequest;
import com.maihaoche.volvo.ui.instorage.daomain.NoStandInstoRequest;
import com.maihaoche.volvo.ui.instorage.daomain.RefuseInstorageRequest;
import com.maihaoche.volvo.ui.instorage.daomain.Series;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 网络请求的接口
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public interface AppServerAPI {
    /**
     * 登录接口
     */
    @POST("pda/login.json")
    ServerGetBuilder<BaseResponse<LoginResponse>> login(@Body LoginRequestDTO userDTO);

    /**
     * 上传数据的接口
     */
    @POST("pda/data/uploadWarehouseActivityRecord.json")
    ServerGetBuilder<BaseResponse> upload(@Body UploadRequestDTO uploadDTO);


    @GET("/uploadImg/qiniuToken.json")
    ServerGetBuilder<BaseResponse<String>> getToken();//获取七牛发布token

    /**
     * 检查软件更新的接口
     */
    @POST("pda/checkVersionUpdate.json")
    ServerGetBuilder<BaseResponse<CheckUpdateResponseDTO>> checkUpdate(@Body CheckUpdateRequestDTO checkUpdateRequest);

    /**
     * 获取待入库车辆列表
     */
    @POST("/pda/pts/pendingToStoredCarList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<InstorageInfo>>> pendingToStored(@Body InstorageListRequest instorageRequest);

    /**
     * 获取今日已入库车辆列表
     */
    @POST("/pda/pts/todaysStoredCarList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<InstorageInfo>>> haveInstoraged(@Body InstorageListRequest instorageRequest);

    /**
     * 标准入库
     */
    @POST("/pda/pts/storeCarStandardly.json")
    ServerGetBuilder<BaseResponse> instorageStand(@Body InstorageRequest instorageRequest);

    /**
     * 非标入库
     */
    @POST("/pda/pts/storeCarNonStandardly.json")
    ServerGetBuilder<BaseResponse> instorageNoStand(@Body NoStandInstoRequest noStandInstoRequest);

    /**
     * 可出库车辆
     */
    @POST("/pda/dfw/canDepartCarList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<OutStorageInfo>>> canOutStorage(@Body OutstorageListRequest outstorageListRequest);

    /**
     * 已出库列表
     */
    @POST("/pda/dfw/todaysDeparedCarList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<OutStorageInfo>>> haveOutStorage(@Body OutstorageListRequest outstorageListRequest);

    /**
     * 出库详情
     */
    @POST("/pda/dfw/departingCarList.json")
    ServerGetBuilder<BaseResponse<DepartingCarList>> outStorageDetail(@Body OutStorageDetailRequest outstorageRequest);

    /**
     * 确认出库
     */
    @POST("/pda/dfw/departingCar.json")
    ServerGetBuilder<BaseResponse> outStorage(@Body OutStorageRequest outstorageRequest);

    /**
     * 获取在库车辆数据
     */
    @POST("/pda/inWarehouse/getInWarehouseCars.json")
    ServerGetBuilder<BaseResponse<PagerResponse<InWarehouseCarVO>>> getInGarageCars(@Body WarehouseIdPageRequest warehouseIdPageRequest);

    /**
     * 获取在库异常车辆数据
     */
    @POST("/pda/inWarehouse/getInWarehouseAbnCars.json")
    ServerGetBuilder<BaseResponse<PagerResponse<InWarehouseCarVO>>> getInGarageAbnCars(@Body WarehouseIdPageRequest warehouseIdPageRequest);

    /**
     * 待验车车辆列表
     */
    @POST("/pda/pts/uncheckedCarList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<InstorageInfo>>> pandCheckCar(@Body InstorageListRequest instorageRequest);

    /**
     * 今日已验车列表
     */
    @POST("/pda/pts/todaysCheckedCarList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<InstorageInfo>>> checkCared(@Body InstorageListRequest instorageRequest);

    /**
     * 验车
     */
    @POST("/pda/pts/checkCarV2.json")
    ServerGetBuilder<BaseResponse> checkCar(@Body CheckCarRequest checkCarRequest);

    /**
     * 仓库列表
     */
    @GET("/app/workbench/queryWarehouse.json")
    ServerGetBuilder<BaseResponse<List<WarehouseVO>>> getWarehouseList();


    /**
     * 获取待盘点车辆列表
     *
     * @param warehouseIdPageRequest
     * @return
     */
    @POST("/pda/inWarehouse/getToStocktakeCars.json")
    ServerGetBuilder<BaseResponse<ToStocktakeDataResponse>> getToStocktakeCars(@Body WarehouseIdPageRequest warehouseIdPageRequest);

    /**
     * 获取已盘点车辆列表
     *
     * @param warehouseIdPageRequest
     * @return
     */
    @POST("/pda/inWarehouse/getStocktakenCars.json")
    ServerGetBuilder<BaseResponse<PagerResponse<StocktakeDetailCarVO>>> getStocktakenCars(@Body WarehouseIdPageRequest warehouseIdPageRequest);


    /**
     * 上传盘到的盘库详情的id
     *
     * @param request
     * @return
     */
    @POST("/pda/inWarehouse/uploadStocktakeInfo.json")
    ServerGetBuilder<BaseResponse> uploadStocktakeDetails(@Body UploadStocktakeDetailsRequest request);


    /**
     * 获取盘库记录的详情列表
     *
     * @return
     */
    @POST("/pda/inWarehouse/getStocktakeDetails.json")
    ServerGetBuilder<BaseResponse<PagerResponse<StocktakeDetailCarVO>>> getStocktakeDetails(@Body StocktakeRecordIdRequest request);

    /**
     * 获取盘点记录列表
     */
    @POST("/pda/inWarehouse/getStocktakeRecords.json")
    ServerGetBuilder<BaseResponse<PagerResponse<StocktakeRecordVO>>> getStocktakeRecords(@Body WarehouseIdPageRequest request);


    /**
     * 邮寄手续之前获取车辆基础信息的
     */
    @POST("/pda/inWarehouse/getBaseCarInfo.json")
    ServerGetBuilder<BaseResponse<CarBaseInfoOnMailVO>> getBaseCarInfo(@Body CarIdRequest request);


    /**
     * 邮寄手续之前获取车辆基础信息的
     */
    @POST("/pda/inWarehouse/uploadMailInfo.json")
    ServerGetBuilder<BaseResponse> uploadMailInfo(@Body SendProcedureRequest request);


    /**
     * 上报异常情况
     */
    @POST("/pda/inWarehouse/reportInWarehouseCarException.json")
    ServerGetBuilder<BaseResponse> uploadAbnormalSituation(@Body ReportAbnInfoRequest request);

    /**
     * 客户查询
     *
     * @return
     */
    @GET("/crm/queryCustomer.json")
    ServerGetBuilder<BaseResponse<List<Customer>>> getCustomer(@Query("customerName") String customerName);

    /**
     * 联系人查询
     *
     * @return
     */
    @GET("/crm/queryContact.json")
    ServerGetBuilder<BaseResponse<List<Customer>>> getContact(@Query("customerId") Long customerId);

    /**
     * 未绑定条码列表
     *
     * @return
     */
    @POST("/pda/inWarehouse/getUnbindingTagCarList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<OutStorageInfo>>> getUnCodeList(@Body OutstorageListRequest request);

    /**
     * 绑定条码
     *
     * @return
     */
    @POST("/pda/inWarehouse/bindTag.json")
    ServerGetBuilder<BaseResponse> bindCode(@Body BindCodeRequest request);

    /**
     * 品牌查询
     *
     * @return
     */
    @GET("/carQuality/allBrand.json")
    ServerGetBuilder<BaseResponse<List<BrandNewInfo>>> getBrand();

    /**
     * 车系查询
     *
     * @return
     */
    @GET("/carQuality/allSeriesByBrandId.json")
    ServerGetBuilder<BaseResponse<List<Series>>> getSeries(@Query("brandId") Long brandId);

    /**
     * 订单维度查询
     *
     * @return
     */
    @POST("/pda/inWarehouse/orderCarList.json")
    ServerGetBuilder<BaseResponse<OrderCarList>> getOrderCarList(@Body OrderCarListRequest request);


    /**
     * 获取车辆详情
     */
    @POST("/pda/inWarehouse/carDetail.json")
    ServerGetBuilder<BaseResponse<CarDetailVO>> getCarDetailVO(@Body CarIdRequest request);


    /**
     * 获取某个车辆的盘点信息的接口
     */
    @POST("/pda/inWarehouse/getStocktakeDetailList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<StocktakeDetailVO>>> getStocktakeDetailList(@Body CarIdPageRequest request);

    /**
     * 数据统计
     */
    @POST("/pda/data/getDataOverview.json")
    ServerGetBuilder<BaseResponse<StaticticsData>> staticsData(@Body StaticRequest request);

    /**
     * 获取可用库位
     */
    @GET("/warehouse/areaPosition/usablePosition.json")
    ServerGetBuilder<BaseResponse<List<CarAreaInfo>>> getEnableArea(@Query("warehouseId") Long warehouseId);

    /**
     * 库位选择/移库
     */
    @POST("/warehouse/areaPosition/move.json")
    ServerGetBuilder<BaseResponse> areaPosition(@Body AreaPositionMoveRequest request);

    /**
     * 拒绝入库
     */
    @POST("/app/entryNotice/refuseCarEntry.json")
    ServerGetBuilder<BaseResponse> refuseInstorage(@Body RefuseInstorageRequest request);

    /**
     * 自动获取品牌车系
     */
    @GET("/carQuality/brandSeriesByUnique.json")
    ServerGetBuilder<BaseResponse<BrandSeriesInfo>> getBrandSeri(@Query("unique") String unique);

    /**
     * 库位信息
     */
    @GET("/pda/areaPosition/graphic/query.json")
    ServerGetBuilder<BaseResponse<GraphicQuery>> getGraphic(@Query("warehouseId") Long warehouseId);

    /**
     * 占位
     */
    @POST("/pda/areaPosition/graphic/occupy.json")
    ServerGetBuilder<BaseResponse> occupy(@Body OccupyInfo request);

    /**
     * 库位搜索
     */
    @POST("/pda/areaPosition/graphic/queryCars.json")
    ServerGetBuilder<BaseResponse<List<SearchResultInfo>>> searchCar(@Body SearchRequest request);

    /**
     * PDA接听视频通话的回调中，申请建立连接
     */
    @POST("/pda/liveCheck/startCheckV2.json")
    ServerGetBuilder<BaseResponse<LiveCheckInfoVO>> avChatStartCheck(@Body StartCheckRequest request);

    /**
     * PDA通话连接建立成功后通知服务端连接已建立
     */
    @POST("/pda/liveCheck/notifyLiveConnectedV2.json")
    ServerGetBuilder<BaseResponse> avChatNotifyLiveConnected(@Body NotifyLiveConnectedRequest request);

    /**
     * APP收到连接断开的回调
     */
    @POST("/pda/liveCheck/onLiveEnd.json")
    ServerGetBuilder<BaseResponse<CheckResultVO>> closeApply(@Body NotifyLiveClosedRequest request);

    /**
     * pda提交直播结果数据
     */
    @POST("/pda/liveCheck/uploadData.json")
    ServerGetBuilder<BaseResponse<CheckResultVO>> avChatUploadData(@Body UploadDataRequest request);

    /**
     * pda提交直播结果数据
     */
    @POST("/pda/liveCheck/notifyLiveClosed.json")
    ServerGetBuilder<BaseResponse> avChatNotifyLiveClosed(@Body NotifyLiveClosedRequest request);

    /**
     * 获取二维码
     */
    @POST("/pda/settlement/create/payBye.json")
    ServerGetBuilder<BaseResponse<String>> getQRcode(@Body QRCodeRequest request);

    /**
     * 出库结算费用
     */
    @POST("/pda/settlement/depart/get.json")
    ServerGetBuilder<BaseResponse<PayDetail>> payMoney(@Body OutStorageDetailRequest request);

    /**
     * 查询支付状态
     */
    @POST("/pda/settlement/payStatus/get.json")
    ServerGetBuilder<BaseResponse<Integer>> queryOrderStatus(@Body QueryOrderRequest request);

    /**
     * 查询车辆是否可以进行出库操作
     */
    @POST("/pda/dfw/departingCar/check.json")
    ServerGetBuilder<BaseResponse<DepartCheck>> queryIsCanOut(@Body OutStorageDetailRequest request);

    /**
     * 获取二维码链接
     */
    @POST("/pda/settlement/depart/getPayUrl.json")
    ServerGetBuilder<BaseResponse<String>> getPayUrl(@Body PayUrlRequest request);

    /**
     * 申请取钥匙
     */
    @POST("pda/ysg/applyTokenKey.json")
    ServerGetBuilder<BaseResponse> applyKey(@Body ApplyKeyRequest request);

    @POST("/pda/pts/getCheckPhotoVidoInfo.json")
    ServerGetBuilder<BaseResponse<CheckCarResponse>> getCheckCarConf(@Body PhotoConfigForm form);

    /**
     * 取消取钥匙
     */
    @POST("pda/ysg/cancelTokenKey.json")
    ServerGetBuilder<BaseResponse> cancelApplyKey(@Body CancelApplyKeyRequest request);

    /**
     * 在库车辆绑定钥匙功能
     */
    @POST("pda/ysg/bindingCarKey.json")
    ServerGetBuilder<BaseResponse> bindKey(@Body BindKeyRequest request);

    /**
     * 在库车辆换绑钥匙功能
     */
    @POST("pda/ysg/changeCarKey.json")
    ServerGetBuilder<BaseResponse> changeCarKey(@Body BindKeyRequest request);

    /**
     * 获取批量取钥匙列表
     */
    @POST("pda/ysg/batchKeyList.json")
    ServerGetBuilder<BaseResponse<PagerResponse<OutStorageInfo>>> getBatchKeyList(@Body ApplyBatchKeyRequest request);

    /**
     * 验证看车码是否有效
     */
    @POST("/pda/checkAppl/getApplId2.json")
    ServerGetBuilder<BaseResponse<Integer>> CheckSeeCode(@Body CheckSeeCodeRequest request);

    /**
     * 验证看车码是否有效2
     */
    @POST("/pda/checkAppl/getApplIdV2.json")
    ServerGetBuilder<BaseResponse<List<Long>>> CheckSeeCode2(@Body CheckSeeCodeRequest request);

    /**
     * 获取看车详情
     */
    @POST("/pda/checkAppl/getCheckApplDetail.json")
    ServerGetBuilder<BaseResponse<SeeCarDetail>> getSeeCarDetail(@Body SeeCarDetailRequest request);

    /**
     * 获取看车详情
     */
    @POST("/pda/checkAppl/getCheckApplDetailV2.json")
    ServerGetBuilder<BaseResponse<List<SeeCarDetail>>> getSeeCarDetail2(@Body SeeCarDetailRequest request);

    @FormUrlEncoded
    @POST("assistant/coordinate/convert")
    Observable<GaoDeResponse> getGaoDeLocation(@FieldMap Map<String, String> params);
}