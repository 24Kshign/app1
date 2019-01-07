package com.maihaoche.volvo.view.seattable;

/**
 * Created by brantyu on 17/8/8.
 * 库位状态
 */

public enum CarportStatus {
    /**
     * 空位可用
     */
    EMPTY,
    /**
     * 已经被使用，有车停放着
     */
    USED,
    /**
     * 已经被使用且被选中
     */
    USED_SELECT,
    /**
     * 已经被占用
     */
    OCCUPY,
    /**
     * 已经被占用且被选中
     */
    OCCUPY_SELECT,
    /**
     * 被选中
     */
    SELECTED,
    /**
     * 不可用
     */
    UNAVAILABLE
}
