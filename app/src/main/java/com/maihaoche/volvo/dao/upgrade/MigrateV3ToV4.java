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

public class MigrateV3ToV4 extends MigrationImpl {

    @Nullable
    @Override
    public Migration getPreviousMigration() {
        return new MigrateV2ToV3();
    }

    @Override
    public int getTargetVersion() {
        return 3;
    }

    @Override
    public int getMigratedVersion() {
        return 4;
    }

    @Override
    public void onMigrate(@NonNull Database db) {
        String sql1 = String.format("ALTER TABLE %s ADD COLUMN '%s' CHAR(50) NOT NULL DEFAULT '';"
                , UserPODao.TABLENAME
                , UserPODao.Properties.YunAccId.columnName);
        String sql2 = String.format("ALTER TABLE %s ADD COLUMN '%s' CHAR(50) NOT NULL DEFAULT '';"
                , UserPODao.TABLENAME
                , UserPODao.Properties.YunToken.columnName);
        db.execSQL(sql1);  //添加列
        db.execSQL(sql2);  //添加列
    }
}
