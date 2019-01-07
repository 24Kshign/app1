package com.maihaoche.volvo.dao;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.maihaoche.volvo.BuildConfig;
import com.maihaoche.volvo.dao.po.CarPO;
import com.maihaoche.volvo.dao.po.Garage2UserPO;
import com.maihaoche.volvo.dao.po.GaragePO;
import com.maihaoche.volvo.dao.po.LablePO;
import com.maihaoche.volvo.dao.po.RecordItemPO;
import com.maihaoche.volvo.dao.po.RecordPO;
import com.maihaoche.volvo.dao.po.UserPO;
import com.maihaoche.volvo.dao.upgrade.AppOpenHelper;
import com.maihaoche.volvo.server.dto.CarExtraInfoDTO;
import com.maihaoche.volvo.ui.vo.Car;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * dao对外输出的api
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class AppDaoHandler implements AppDaoAPI {

    private DaoMaster mDaoMaster;


    public AppDaoHandler(Application application) {
        AppOpenHelper devOpenHelper = new AppOpenHelper(application, BuildConfig.DB_NAME, null);
        SQLiteDatabase sSQLiteDatabase = devOpenHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(sSQLiteDatabase);
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    private DaoSession newSession() {
        return mDaoMaster.newSession(IdentityScopeType.Session);
    }

    @Override
    public DaoGet<UserPO> getUser(String userName) {
        return new DaoGet<UserPO>() {
            @Override
            UserPO getResult() throws Exception {
                return getUserSync(userName);
            }
        };
    }

    @Override
    public DaoGet<Boolean> saveUser(UserPO userPO) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() throws Exception {
                if (userPO != null) {
                    newSession().getUserPODao().insertOrReplace(userPO);
                }
                return true;
            }
        };
    }


    @Override
    public DaoGet<GaragePO> getWmsGarage(Long wmsGarageId) {
        return new DaoGet<GaragePO>() {
            @Override
            GaragePO getResult() throws Exception {
                DaoSession session = newSession();
                GaragePO garagePO = session.getGaragePODao().queryBuilder()
                        .where(GaragePODao.Properties.WmsGarageId.eq(wmsGarageId))
                        .unique();
                return garagePO;
            }
        };
    }

    @Override
    public DaoGet<GaragePO> getDefaultWmsGarage(String userName) {
        return new DaoGet<GaragePO>() {
            @Override
            GaragePO getResult() throws Exception {
                DaoSession session = newSession();
                List<GaragePO> garagePOList = getUserSync(session, userName).getGaragesOfThisUser();
                if (garagePOList != null && garagePOList.size() > 0) {
                    return garagePOList.get(0);
                }
                return null;
            }
        };
    }


    @Override
    public DaoGet<Boolean> saveGarageToUser(List<Garage2UserPO> garage2UserPOS) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() throws Exception {
                DaoSession session = newSession();
                session.getGarage2UserPODao().deleteAll();
                session.getGarage2UserPODao().insertOrReplaceInTx(garage2UserPOS);
                return true;
            }
        };
    }

    @Override
    public DaoGet<Boolean> saveGarage(List<GaragePO> garagePOList) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() throws Exception {
                newSession().getGaragePODao().insertOrReplaceInTx(garagePOList);
                return true;
            }
        };
    }

    @Override
    public DaoGet<Boolean> carPOModify(Car car) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() {
                if (car.mCarPo == null) {
                    return false;
                }
                DaoSession daoSession = newSession();
                long id = daoSession.getCarPODao().insertOrReplace(car.mCarPo);
                if (id > 0 && car.mCarExtraInfoDto != null) {
                    daoSession.getCarExtraInfoPODao().insertOrReplace(car.mCarExtraInfoDto.toExtraInfoPO());
                }
                return true;
            }
        };
    }

    @Override
    public DaoGet<Boolean> carOut(String carId) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() {
                CarPODao carPODao = newSession().getCarPODao();
                CarPO carPO = carPODao
                        .queryBuilder()
                        .where(CarPODao.Properties.CarId.eq(carId))
                        .uniqueOrThrow();
                carPO.setIsIn(Enums.YesOrNoEnum.NO);
                carPODao.insertOrReplace(carPO);
                return true;
            }
        };
    }

    @Override
    public DaoGet<Boolean> deleteCar(String carId) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() {
                DaoSession daoSession = newSession();
                CarPODao carPODao = daoSession.getCarPODao();
                carPODao.deleteByKey(carId);
                daoSession.getCarExtraInfoPODao().deleteByKey(carId);
                return true;
            }
        };
    }

    @Override
    public DaoGet<Car> getCar(String carId) {
        return new DaoGet<Car>() {
            @Override
            Car getResult() throws Exception {
                DaoSession daoSession = newSession();
                CarPO carPO = daoSession.getCarPODao()
                        .queryBuilder()
                        .where(CarPODao.Properties.CarId.eq(carId))
                        .unique();
                if (carPO == null) {
                    return null;
                }
                Car car = new Car();
                car.mCarPo = carPO;
                car.mCarExtraInfoDto = CarExtraInfoDTO.createFromPO(carPO.getCarExtraInfo());
                return car;
            }
        };
    }

    @Override
    public DaoGet<List<CarPO>> getCarList(Long wmsGarageId) {
        return new DaoGet<List<CarPO>>() {
            @Override
            List<CarPO> getResult() {
                return newSession().getCarPODao()
                        .queryBuilder()
                        .where(CarPODao.Properties.WmsGarageId.eq(wmsGarageId))
                        .list();
            }
        };
    }

    @Override
    public DaoGet<List<CarPO>> getCarListIn(Long wmsGarageId) {
        return new DaoGet<List<CarPO>>() {
            @Override
            List<CarPO> getResult() {
                return newSession().getCarPODao()
                        .queryBuilder()
                        .where(CarPODao.Properties.WmsGarageId.eq(wmsGarageId), CarPODao.Properties.IsIn.eq(Enums.YesOrNoEnum.YES.value()))
                        .list();
            }
        };
    }

    /**
     * 查询某个盘库记录下面的详情，既 车辆记录
     *
     * @param recordId
     * @return
     */
    @Override
    public DaoGet<List<RecordItemPO>> getRecordItems(Long recordId) {
        return new DaoGet<List<RecordItemPO>>() {
            @Override
            List<RecordItemPO> getResult() {
                return newSession().getRecordItemPODao()
                        .queryBuilder()
                        .where(RecordItemPODao.Properties.RecordId.eq(recordId))
                        .orderAsc(RecordItemPODao.Properties.IsInRecord)
                        .orderDesc(RecordItemPODao.Properties.RecordTime)
                        .list();
            }
        };
    }

    @Override
    public DaoGet<RecordPO> getRecord(Long recordId) {
        return new DaoGet<RecordPO>() {
            @Override
            RecordPO getResult() throws Exception {
                return newSession().getRecordPODao().queryBuilder()
                        .where(RecordPODao.Properties.RecordId.eq(recordId))
                        .unique();
            }
        };
    }

    @Override
    public DaoGet<RecordPO> saveRecord(RecordPO recordPO) {
        return new DaoGet<RecordPO>() {
            @Override
            RecordPO getResult() throws Exception {
                long recordId = newSession().getRecordPODao().insertOrReplace(recordPO);
                recordPO.setRecordId(recordId);
                return recordPO;
            }
        };
    }

    @Override
    public DaoGet<Boolean> saveRecordItems(ArrayList<RecordItemPO> recordItemPOS) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() throws Exception {
                newSession().getRecordItemPODao().insertOrReplaceInTx(recordItemPOS);
                return true;
            }
        };
    }

    @Override
    public DaoGet<Boolean> saveLable(LablePO lablePO) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() throws Exception {
                newSession().getLablePODao().insertOrReplaceInTx(lablePO);
                return true;
            }
        };
    }

    @Override
    public DaoGet<Boolean> deleteLable(Long key) {
        return new DaoGet<Boolean>() {
            @Override
            Boolean getResult() throws Exception {
                newSession().getLablePODao().deleteByKey(key);
                return true;
            }
        };
    }

    @Override
    public DaoGet<List<LablePO>> getLable(Long garageId) {
        return new DaoGet<List<LablePO>>() {
            @Override
            List<LablePO> getResult() {
                return newSession().getLablePODao()
                        .queryBuilder()
                        .where(LablePODao.Properties.GarageId.eq(garageId))
                        .list();
            }
        };
    }


    /**=============================同步的查询语句===============================*/
    /**
     * 同步的获得某个用户信息数据
     *
     * @param session
     * @param userName
     * @return
     * @throws Throwable
     */
    private UserPO getUserSync(DaoSession session, String userName) throws Exception {
        if (session == null) {
            session = newSession();
        }
        QueryBuilder<UserPO> queryBuilder = session.getUserPODao().queryBuilder();
        try {
            return queryBuilder
                    .where(UserPODao.Properties.UserName.eq(userName))
                    .uniqueOrThrow();
        } catch (DaoException e) {
            e.printStackTrace();
            throw new NullPointerException(DaoGet.NO_DATA);
        }
    }

    /**
     * 同步的获得某个用户信息数据
     */
    private UserPO getUserSync(String userName) throws Exception {
        return getUserSync(newSession(), userName);
    }


}
