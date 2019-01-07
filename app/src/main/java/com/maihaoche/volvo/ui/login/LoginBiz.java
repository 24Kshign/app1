package com.maihaoche.volvo.ui.login;

import android.content.Intent;
import android.text.TextUtils;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.commonbiz.service.utils.SharePreferenceHandler;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.dao.po.Garage2UserPO;
import com.maihaoche.volvo.dao.po.GaragePO;
import com.maihaoche.volvo.dao.po.UserPO;
import com.maihaoche.volvo.dao.po.WarehouseVO;
import com.maihaoche.volvo.server.dto.request.LoginRequestDTO;
import com.maihaoche.volvo.server.dto.response.LoginResponse;
import com.maihaoche.volvo.ui.avchat.AVChatLibManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 登录相关的业务类
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class LoginBiz {

    public static final String SP_LOGIN_USER_NAME = "sp_login_user_name";//本地缓存的用户登录的手机号。
    public static final String SP_AUTO_LOGIN = "auto_login";//本地缓存的用户是否自动登录。
    public static final String SP_IS_LOGIN = "login";//用户是否登陆过


    /**
     * 从服务端登录
     */
    public static void serverLogin(BaseActivity activity,
                                   String userName,
                                   String password,
                                   boolean isMhcStaff,
                                   boolean showLoading,
                                   Consumer<UserPO> onSucessLogin,
                                   Consumer<String> onLoginError) {
        Consumer<String> onErrorAction = s -> {
            if (showLoading) {
                activity.cancelLoading(() -> {
                    if (onLoginError != null) {
                        onLoginError.accept(s);
                    }
                });
            } else if (onLoginError != null) {
                onLoginError.accept(s);
            }
        };
        LoginRequestDTO userDTO = new LoginRequestDTO();
        userDTO.isPda = DeviceUtil.isSENTER() ? "pda" : "mobile";
        userDTO.userType = isMhcStaff ? 0 : 1;
        userDTO.userName = userName;
        userDTO.passWord = password;
        AppApplication.getServerAPI().login(userDTO)
                .setOnDataGet(
                        loginResponseBaseResponse -> {
                            LogUtil.v("登录成功，返回数据");
                            LoginResponse loginResponse = loginResponseBaseResponse.result;
                            //保存用户数据
                            UserPO userPO = new UserPO();
                            userPO.setMhcStaff(isMhcStaff);
                            userPO.setUserName(userName);
                            userPO.setRealName(loginResponse.pdaUserRealName);
                            userPO.setPassWord(password);
                            userPO.setUserId(loginResponse.userId);
                            userPO.setYunAccId(loginResponse.accId);
                            userPO.setYunToken(loginResponse.token);
                            userPO.setJSessionId(AppApplication.getUserPO().getJSessionId());
                            userPO.setMhcSessionId(AppApplication.getUserPO().getMhcSessionId());
                            AppApplication.getDaoApi().saveUser(userPO)
                                    .setOnResultGet(bolean -> {
                                        LogUtil.v("用户数据保存成功");
                                        AppApplication.setUserPO(userPO);
                                        if (loginResponse.userGarageIds == null || loginResponse.userGarageIds.size() == 0) {
                                            onSucessLogin.accept(userPO);
                                            return;
                                        }
                                        //保存仓库与用户的对应关系
                                        ArrayList<Garage2UserPO> garage2UserPOS = new ArrayList<>();
                                        for (Long garageId : loginResponse.userGarageIds) {
                                            Garage2UserPO garage2UserPO = new Garage2UserPO();
                                            garage2UserPO.setUserName(userName);
                                            garage2UserPO.setWmsGarageId(garageId);
                                            garage2UserPOS.add(garage2UserPO);
                                        }
                                        //保存数据
                                        AppApplication.getDaoApi().saveGarageToUser(garage2UserPOS)
                                                .setOnResultGet(aBoolean -> onSucessLogin.accept(userPO))
                                                .setOnDataError(emsg -> onSucessLogin.accept(userPO))
                                                .call(activity);
                                    })
                                    .setOnDataError(emsg -> onErrorAction.accept("登录出错：" + emsg))
                                    .call(activity);
                        }

                )
                .setOnDataError(emsg -> onErrorAction.accept("登录出错：" + emsg))
                .setDoOnSubscribe(disposable -> {
                    if (showLoading) {
                        activity.showLoading("登录中...", () -> {
                            onErrorAction.accept("登录超时，请再试一次");
                            if (!disposable.isDisposed()) {
                                disposable.dispose();
                            }
                        });
                    }
                })
                .call(activity);
    }

    /**
     * 退出登录
     * 只清空自动登录，不清除上次登录的用户名
     */
    public static void logout(BaseActivity activity) {
        // 退出云信登录状态
        AVChatLibManager.getInstance().logout();
        AppApplication.setUserPO(new UserPO());
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    /**
     * 退出登录
     * 只清空自动登录，不清除上次登录的用户名
     */
    public static void logout(BaseFragmentActivity activity) {
        // 退出云信登录状态
        AVChatLibManager.getInstance().logout();
        AppApplication.setUserPO(new UserPO());
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }


    /**
     * 获取仓库和车辆数据
     */
    public static void getAllData(BaseActivity activity, boolean showLoading, Action onDataSaved) {
        //再保存所有数据
        AppApplication.getServerAPI().getWarehouseList()
                .setOnDataError((String emsg) -> {
                    if (showLoading) {
                        activity.cancelLoading(() -> AlertToast.show("获取仓库数据出错:" + emsg));
                    }
                })
                .setOnDataGet(result -> {
                    if (showLoading) {
                        activity.cancelLoading();
                    }
                    if (result != null && result.result != null) {
                        ArrayList<GaragePO> garagePOS = new ArrayList<>();
                        List<WarehouseVO> warehouseVOs = result.result;
                        for (WarehouseVO warehouseVO : warehouseVOs) {
                            garagePOS.add(warehouseVO.toPO());
                        }
                        AppApplication.getDaoApi().saveGarage(garagePOS)
                                .setOnResultGet(aBoolean -> {
                                    if (onDataSaved != null) {
                                        onDataSaved.run();
                                    }
                                })
                                .call(activity);
                    } else {
                        if (onDataSaved != null) {
                            onDataSaved.run();
                        }
                    }
                })
                .setDoOnSubscribe(disposable -> {
                    if (showLoading) {
                        activity.showLoading("获取仓库数据...", () -> {
                            if (!disposable.isDisposed()) {
                                disposable.dispose();
                            }
                        });
                    }
                })
                .call(activity);


    }


    /**
     * 获取历史登录数据
     *
     * @param baseActivity
     * @param onUserPOGet
     * @throws Exception
     */
    public static void getLoginHistory(BaseActivity baseActivity, Consumer<UserPO> onUserPOGet) {
        //本地是否有登录信息。
        String loginInUserName = SharePreferenceHandler.getPrefStringValue(LoginBiz.SP_LOGIN_USER_NAME, "");
        if (TextUtils.isEmpty(loginInUserName)) {
            if (onUserPOGet != null) {
                try {
                    onUserPOGet.accept(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            AppApplication.getDaoApi().getUser(loginInUserName)
                    .setOnResultGet(userPo -> {
                        if (onUserPOGet != null) {
                            onUserPOGet.accept(userPo);
                        }
                    })
                    .setOnDataError(emsg -> {
                        if (onUserPOGet != null) {
                            onUserPOGet.accept(null);
                        }
                    })
                    .call(baseActivity);
        }
    }


}
