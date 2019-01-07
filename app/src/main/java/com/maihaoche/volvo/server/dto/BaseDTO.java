package com.maihaoche.volvo.server.dto;

/**
 * 作者：yang
 * 时间：17/6/17
 * 邮箱：yangyang@maihaoche.com
 */
public abstract class BaseDTO<PO> {

    /**
     * 转化为本地存储对象
     */
    public abstract PO toPO();

    public BaseDTO(PO po) {
    }

    public BaseDTO() {
    }

}
