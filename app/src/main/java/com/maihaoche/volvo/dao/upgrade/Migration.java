package com.maihaoche.volvo.dao.upgrade;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.greendao.database.Database;

/**
 * 类简介：数据库升级的接口
 * 作者：  yang
 * 时间：  17/6/23
 * 邮箱：  yangyang@maihaoche.com
 */

public interface Migration {

    /**
     * 应用升级
     *
     * @param db
     * @param oldVersion 最原始的版本。
     * @return the version 返回当前migration使用后的最新版本
     */
    int applyMigration(@NonNull Database db, int oldVersion);

    /**
     * @return 返回前一个migration
     */
    @Nullable
    Migration getPreviousMigration();

    /**
     * @return 当前的migration适用的版本
     */
    int getTargetVersion();

    /**
     * @return migration应用后的数据库版本
     * applied.
     */
    int getMigratedVersion();
}