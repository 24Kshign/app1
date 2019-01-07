package com.maihaoche.volvo.dao.upgrade;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.maihaoche.volvo.dao.GaragePODao;

import org.greenrobot.greendao.database.Database;

/**
 * 类简介：数据库升级的一个例子。
 * 与当前版本无关。
 *
 * 作者：  yang
 * 时间：  17/6/23
 * 邮箱：  yangyang@maihaoche.com
 */

public class MigrateV2ToV3 extends MigrationImpl {
    @Nullable
    @Override
    public Migration getPreviousMigration() {
        return new MigrateV1ToV2();
    }

    @Override
    public int getTargetVersion() {
        return 2;
    }

    @Override
    public int getMigratedVersion() {
        return 3;
    }

    @Override
    public void onMigrate(@NonNull Database db) {
        //garage表中添加一个列
        String sql = String.format("ALTER TABLE %s ADD COLUMN '%s' TINYINT NOT NULL DEFAULT true;"
                ,GaragePODao.TABLENAME
                ,GaragePODao.Properties.HasStroage.columnName);
        db.execSQL(sql);  //添加列

    }
}
