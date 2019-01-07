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

public class MigrateV1ToV2 extends MigrationImpl {
    @Nullable
    @Override
    public Migration getPreviousMigration() {
        return null;
    }

    @Override
    public int getTargetVersion() {
        return 1;
    }

    @Override
    public int getMigratedVersion() {
        return 2;
    }

    @Override
    public void onMigrate(@NonNull Database db) {
        //garage表中添加一个列
        String sql = String.format("ALTER TABLE %s ADD COLUMN '%s' TINYINT NOT NULL DEFAULT 1;"
                ,GaragePODao.TABLENAME
                ,GaragePODao.Properties.UsePda.columnName);
        db.execSQL(sql);  //添加列

    }
}
