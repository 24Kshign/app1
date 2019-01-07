package com.maihaoche.volvo.dao;

import com.maihaoche.volvo.dao.po.CarPO;
import com.maihaoche.volvo.dao.po.Garage2UserPO;
import com.maihaoche.volvo.dao.po.GaragePO;
import com.maihaoche.volvo.dao.po.LablePO;
import com.maihaoche.volvo.dao.po.RecordItemPO;
import com.maihaoche.volvo.dao.po.RecordPO;
import com.maihaoche.volvo.dao.po.UserPO;
import com.maihaoche.volvo.ui.vo.Car;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * 数据库查询的API
 * 所有数据库操作返回的对象为{@link Single}.该对象类似于{@link io.reactivex.Observable}.
 * 区别在于它只需要注册onSuccess 和 onError回调。既：要么成功拿到结果，要么出错。
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */

public interface AppDaoAPI {

    /**
     * 从本地数据库获取用户的数据，用于网络传输
     */
    DaoGet<UserPO> getUser(String userName);

    /**
     * 保存用户数据
     */
    DaoGet<Boolean> saveUser(UserPO userPO);


    /**==================================仓库相关的接口=========================*/

    /**
     * 根据wms仓库id获取仓库信息
     */
    DaoGet<GaragePO> getWmsGarage(Long wmsGarageId);


    /**
     * 获取用户的授权的仓库列表中，第一条记录
     */
    DaoGet<GaragePO> getDefaultWmsGarage(String userName);


    /**
     * 保存用户与仓库的关联信息
     */
    DaoGet<Boolean> saveGarageToUser(List<Garage2UserPO> relatedInfoPOS);

    /**
     * 保存仓库信息
     */
    DaoGet<Boolean> saveGarage(List<GaragePO> garagePOList);

    /**
     * ==================================车辆相关的接口=========================
     */


    /**
     * 修改car po
     */
    DaoGet<Boolean> carPOModify(Car car);

    /**
     * 出库
     *
     * @param carId
     * @return
     */
    DaoGet<Boolean> carOut(String carId);

    /**
     * 删除车辆信息
     *
     * @param carId
     * @return
     */
    DaoGet<Boolean> deleteCar(String carId);


    /**
     * 获取某辆车的详细数据
     *
     * @param carId
     * @return
     */
    DaoGet<Car> getCar(String carId);


    /**
     * 获取车辆列表
     *
     * @param wmsGarageId
     * @return
     */
    DaoGet<List<CarPO>> getCarList(Long wmsGarageId);


    /**
     * 获取在库车辆列表
     *
     * @param wmsGarageId
     * @return
     */
    DaoGet<List<CarPO>> getCarListIn(Long wmsGarageId);


    /**==================================盘库相关的接口=========================*/


    /**
     * 获取某一个盘库记录的item
     * 不在库的记录在前面
     *
     * @param recordId
     * @return
     */
    DaoGet<List<RecordItemPO>> getRecordItems(Long recordId);


    /**
     * 获取某一条盘库记录
     */
    DaoGet<RecordPO> getRecord(Long recordId);

    /**
     * 保存一条盘库记录
     */
    DaoGet<RecordPO> saveRecord(RecordPO recordPO);

    /**
     * 保存盘库记录的列表数据
     */
    DaoGet<Boolean> saveRecordItems(ArrayList<RecordItemPO> recordItemPOS);

    /**==================================标签相关的接口=========================*/

    /**
     * 保存一条标签
     */
    DaoGet<Boolean> saveLable(LablePO lablePO);

    /**
     * 删除一条标签
     */
    DaoGet<Boolean> deleteLable(Long key);

    /**
     * 获取仓库下的所有标签
     */
    DaoGet<List<LablePO>> getLable(Long garageId);
}
