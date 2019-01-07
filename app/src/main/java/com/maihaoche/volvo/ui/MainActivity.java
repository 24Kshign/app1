package com.maihaoche.volvo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.SharePreferenceHandler;
import com.maihaoche.scanlib.ScanApplication;
import com.maihaoche.scanlib.rfid.ExceptionForToast;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.Enums;
import com.maihaoche.volvo.dao.po.GaragePO;
import com.maihaoche.volvo.dao.po.WarehouseVO;
import com.maihaoche.volvo.databinding.ActivityMainBinding;
import com.maihaoche.volvo.ui.avchat.AVChatCache;
import com.maihaoche.volvo.ui.car.activity.CheckCarActivity;
import com.maihaoche.volvo.ui.car.activity.CheckSeeCarActivity;
import com.maihaoche.volvo.ui.car.activity.OutStorageListActivity;
import com.maihaoche.volvo.ui.common.activity.InitDataActivity;
import com.maihaoche.volvo.ui.common.activity.StatisticActivity;
import com.maihaoche.volvo.ui.instorage.activity.GarageListActivity;
import com.maihaoche.volvo.ui.instorage.activity.InstorageListActivity;
import com.maihaoche.volvo.ui.inwarehouse.cars.ActivityInWarehouseCar;
import com.maihaoche.volvo.ui.inwarehouse.cars.SeatTableActivity;
import com.maihaoche.volvo.ui.inwarehouse.record.ActivityStocktake;
import com.maihaoche.volvo.ui.login.LoginActivity;
import com.maihaoche.volvo.ui.login.LoginBiz;
import com.maihaoche.volvo.ui.setting.HelpActivity;
import com.maihaoche.volvo.ui.setting.SettingActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;

import io.reactivex.functions.Action;

import static com.maihaoche.scanlib.ScanApplication.uhfInit;


/**
 * 类简介：
 * 作者：  yang
 * 时间：  2017/8/3
 * 邮箱：  yangyang@maihaoche.com
 */

public class MainActivity extends HeaderProviderActivity<ActivityMainBinding> {


    private static final String LAST_CHOOSEN_WAREHOUDE_ID = "last_choosen_warehoude_id";

    private static final int REQUEST_CHOOSE_GARAGE = 0;//切换库


    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private int count;

    @Override
    public int getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        load();
        HintUtil.getInstance().init();
        if (DeviceUtil.isSENTER()) {
            try {
                uhfInit();
            } catch (ExceptionForToast exceptionForToast) {
                AlertToast.show("找车功能初始化失败，请重启设备");
            }
        }

        //注册云信是否被踢
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
            @Override
            public void onEvent(StatusCode statusCode) {

                if (statusCode.getValue() == 7) {
                    AlertToast.show("您的账号在其他设备登录");
                    LoginBiz.logout(MainActivity.this);
                }

            }
        }, true);


    }

    private void init() {
        hiddenLeftArrow();
        setRightText("切换库", v -> toGarageChoose());

        ActivityMainBinding binding = getContentBinding();

        binding.username.setText(AppApplication.getRealName() + ",欢迎回来");

        binding.instockView.setOnClickListener(v -> {
            startActivity(new Intent(this, InstorageListActivity.class));
        });
        binding.checkinView.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckCarActivity.class));
        });
        binding.carInstockView.setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityInWarehouseCar.class));
        });
        binding.seatTableView.setOnClickListener(v -> {
            if (AppApplication.getGaragePO().getHasStroage() == 1) {
                startActivity(new Intent(this, SeatTableActivity.class));
            } else {
                AlertToast.show("当前仓库未开启库位，无法展示平面图");
            }

        });
        //盘点
        binding.stocktakeView.setOnClickListener(v -> {
            if (AppApplication.getGaragePO().getUsePda() == Enums.YesOrNoEnum.YES) {
                startActivity(ActivityStocktake.createIntent(this));
            } else {
                AlertToast.show("仓库为开通PDA盘点，请联系总部运营开通或使用钉钉盘点");
            }
        });
        binding.checkOutView.setOnClickListener(v -> {
            startActivity(new Intent(this, OutStorageListActivity.class));
        });
        binding.dataStatisticsView.setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticActivity.class));
        });
        binding.seeCar.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckSeeCarActivity.class));
        });
        binding.dataInitView.setOnClickListener(v -> {
            startActivity(new Intent(this, InitDataActivity.class));
        });
        binding.helpView.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
        });
        binding.configView.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingActivity.class));
        });

    }


    private void load() {
        try {
            Action noLastChoose = () -> {
                //没有找到这个用户对应的仓库。从仓库列表中选择第一个仓库显示
                AppApplication.getDaoApi().getDefaultWmsGarage(AppApplication.getUserName())
                        .setOnResultGet(garagePO -> onGarageGet(garagePO))
                        .call(MainActivity.this);
            };
            Long lastWarehouseId = SharePreferenceHandler.getPrefLongValue(LAST_CHOOSEN_WAREHOUDE_ID + AppApplication.getUserName(), 0);
            if (lastWarehouseId >= 0) {
                AppApplication.getDaoApi().getWmsGarage(lastWarehouseId)
                        .setOnResultGet(garagePO -> onGarageGet(garagePO))
                        .setOnDataError(emsg -> noLastChoose.run())
                        .call(MainActivity.this);
            } else {
                noLastChoose.run();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVChatCache.setMainTaskLaunching(false);
        if (AppApplication.getUserPO() == null || TextUtils.isEmpty(AppApplication.getUserPO().getUserId())) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHOOSE_GARAGE:
                if (resultCode == RESULT_OK && data != null) {
                    WarehouseVO warehouseVO = (WarehouseVO) data.getSerializableExtra(GarageListActivity.CHOSEN_WAREHOUSE);
                    if (warehouseVO != null) {
                        onGarageGet(warehouseVO.toPO());
                        SharePreferenceHandler.commitLongPref(LAST_CHOOSEN_WAREHOUDE_ID + AppApplication.getUserName(), warehouseVO.warehouseId);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void toGarageChoose() {
        startActivityForResult(GarageListActivity.createIntent(this, AppApplication.getGaragePO().getWmsGarageId(), AppApplication.getGaragePO().getGarageName()), REQUEST_CHOOSE_GARAGE);
    }

    private void onGarageGet(GaragePO garagePO) {
        initHeader(garagePO.getGarageName());
        AppApplication.setGaragePO(garagePO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HintUtil.getInstance().release();
        ScanApplication.uhfUninit();
    }
}
