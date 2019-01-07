package com.maihaoche.volvo.dao.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.maihaoche.volvo.dao.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * 类简介：数据库的helper。可以冲重写onUpgrade语句
 * 作者：  yang
 * 时间：  17/6/23
 * 邮箱：  yangyang@maihaoche.com
 */

public class AppOpenHelper extends DaoMaster.OpenHelper {

    public AppOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        //数据库升级语句在这里
        switch (newVersion) {
            case 2:
                new MigrateV1ToV2().applyMigration(db, oldVersion);
                break;
            case 3:
                new MigrateV2ToV3().applyMigration(db, oldVersion);
                break;
            case 4:
                new MigrateV3ToV4().applyMigration(db, oldVersion);
            case 5:
                new MigrateV4ToV5().applyMigration(db, oldVersion);
            default:
                return;
        }
    }
}
