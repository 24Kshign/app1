package com.maihaoche.commonbiz.service.utils;

import android.app.Application;

import com.maihaoche.base.application.BaseApplication;

/**
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class ApplicationProvider {
    protected static Application getApplication() {
        return BaseApplication.getApplication();
    }
}
