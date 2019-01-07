package com.maihaoche.volvo.ui.setting;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.maihaoche.volvo.BR;

/**
 * Created by gujian
 * Time is 2017/6/23
 * Email is gujian@maihaoche.com
 */

public class DefauleValue extends BaseObservable{

    private boolean haveSeted;

    private boolean vinCheck;

    private boolean defaultWrite;

    //仓库位置
    private String labe;
    //钥匙数量
    private String keyNumber;


    //下列值0表示未设置，1表示没有，2表示有

    //关单、合格证
    private int certification;

    //一致性证书
    private int fitertifi;

    //商检单
    private int comnspect;

    //说明书
    private int direction;

    public DefauleValue() {

        haveSeted = true;
        vinCheck = true;
        defaultWrite = false;
    }

    public boolean isHaveSeted() {
        return haveSeted;
    }

    public void setHaveSeted(boolean haveSeted) {
        this.haveSeted = haveSeted;
    }

    @Bindable
    public boolean isVinCheck() {
        return vinCheck;
    }

    public void setVinCheck(boolean vinCheck) {
        this.vinCheck = vinCheck;
        notifyPropertyChanged(BR.vinCheck);
    }

    @Bindable
    public boolean isDefaultWrite() {
        return defaultWrite;
    }

    public void setDefaultWrite(boolean inStorage) {
        this.defaultWrite = inStorage;
        notifyPropertyChanged(BR.defaultWrite);
    }

    @Bindable
    public String getLabe() {
        return labe;
    }

    public void setLabe(String labe) {
        this.labe = labe;
        notifyPropertyChanged(BR.labe);
    }

    @Bindable
    public String getKeyNumber() {
        return keyNumber;
    }

    public void setKeyNumber(String keyNumber) {
        this.keyNumber = keyNumber;
        notifyPropertyChanged(BR.keyNumber);
    }

    @Bindable
    public int getCertification() {
        return certification;
    }

    public void setCertification(int certification) {
        this.certification = certification;
        notifyPropertyChanged(BR.certification);
    }

    @Bindable
    public int getFitertifi() {
        return fitertifi;
    }

    public void setFitertifi(int fitertifi) {
        this.fitertifi = fitertifi;
        notifyPropertyChanged(BR.fitertifi);
    }

    @Bindable
    public int getComnspect() {
        return comnspect;
    }

    public void setComnspect(int comnspect) {
        this.comnspect = comnspect;
        notifyPropertyChanged(BR.comnspect);
    }

    @Bindable
    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
        notifyPropertyChanged(BR.direction);
    }
}
