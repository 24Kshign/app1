package com.maihaoche.volvo.ui.vo;

import com.maihaoche.volvo.dao.po.CarPO;
import com.maihaoche.volvo.server.dto.CarExtraInfoDTO;

import java.io.Serializable;

/**
 * 车辆包含的所有信息。包括简要信息和额外信息
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class Car implements Serializable {

    public CarPO mCarPo = new CarPO();

    public CarExtraInfoDTO mCarExtraInfoDto = null;

}
