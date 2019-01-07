package com.maihaoche.volvo.dao.upgrade;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.maihaoche.volvo.dao.UserPODao;

import org.greenrobot.greendao.database.Database;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  2017/12/29
 * 邮箱：  yangyang@maihaoche.com
 */

public class MigrateV4ToV5 extends MigrationImpl {

    @Nullable
    @Override
    public Migration getPreviousMigration() {
        return new MigrateV3ToV4();
    }

    @Override
    public int getTargetVersion() {
        return 4;
    }

    @Override
    public int getMigratedVersion() {
        return 5;
    }

    @Override
    public void onMigrate(@NonNull Database db) {
        String sql1 = String.format("ALTER TABLE %s ADD COLUMN '%s' TINYINT NOT NULL DEFAULT true;"
                , UserPODao.TABLENAME
                , UserPODao.Properties.IsMhcStaff.columnName);
        db.execSQL(sql1);  //添加列
    }
}