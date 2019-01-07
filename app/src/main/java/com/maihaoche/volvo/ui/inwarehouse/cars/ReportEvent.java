package com.maihaoche.volvo.ui.inwarehouse.cars;

import com.maihaoche.volvo.server.dto.CarDetailVO;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/28
 * Email is gujian@maihaoche.com
 */

public class ReportEvent implements Serializable {

    public InWarehouseCarVO carVO;

    public ReportEvent(InWarehouseCarVO carVO) {
        this.carVO = carVO;
    }
}
