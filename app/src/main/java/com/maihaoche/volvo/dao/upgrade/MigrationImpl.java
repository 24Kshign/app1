package com.maihaoche.volvo.dao.upgrade;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.database.Database;

/**
 * 类简介：升级接口的简单封装。包含了查找升级链之前的逻辑。
 * 作者：  yang
 * 时间：  17/6/23
 * 邮箱：  yangyang@maihaoche.com
 */

public abstract class MigrationImpl implements Migration {

    /**
     * 数据库升级之前的准备，主要进行版本判断，并调用之前的升级对象。
     */
    protected void prepareMigration(@NonNull Database db, int currentVersion) {
        if (db == null) {
            throw new NullPointerException("db不能为空");
        }
        if (currentVersion < 1) {
            throw new IllegalArgumentException(
                    "最低版本为 1, 无法从该版本升级: " + currentVersion);
        }

        if (currentVersion < getTargetVersion()) {
            Migration previousMigration = getPreviousMigration();
            if (previousMigration == null) {
                // This is the first migration
                if (currentVersion != getTargetVersion()) {
                    throw new IllegalStateException(
                            "无法从版本: " + currentVersion + " 升级。");
                }
            }
            if (previousMigration.applyMigration(db, currentVersion) != getTargetVersion()) {
                // For all other migrations ensure that after the earlier
                // migration has been applied the expected version matches
                throw new IllegalStateException(
                        "getPreviousMigration返回的对象不对，其升级后的版本不等于:" + getTargetVersion());
            }
        }
    }

    @Override
    final public int applyMigration(@NonNull Database db, int currentVersion) {
        prepareMigration(db, currentVersion);
        onMigrate(db);
        return getMigratedVersion();
    }

    abstract public void onMigrate(@NonNull Database db);

}