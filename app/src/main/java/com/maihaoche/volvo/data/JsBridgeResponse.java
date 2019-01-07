package com.maihaoche.volvo.data;

/**
 * Created by manji
 * Date：2018/8/22 下午3:21
 * Desc：
 */
public class JsBridgeResponse<T>{

    private boolean success;

    private String error_msg;

    private T data;

    public JsBridgeResponse(boolean success, String error_msg, T data) {
        this.success = success;
        this.error_msg = error_msg;
        this.data = data;
    }
}