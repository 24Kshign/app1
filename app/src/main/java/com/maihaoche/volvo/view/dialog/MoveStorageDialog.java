package com.maihaoche.volvo.view.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.DialogMoveStorageBinding;
import com.maihaoche.volvo.ui.common.daomain.CarAreaInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/8/15
 * Email is gujian@maihaoche.com
 */

public class MoveStorageDialog extends Dialog {

    private static final String PREFIX = "已选库位：";

    private DialogMoveStorageBinding binding;
    private List<CarAreaInfo> carAreaInfos;
    private String area = "0";
    private String row = "0";
    private String line = "0";
    private int areaNum = 0;
    private int rowNum = 0;
    private int locationNum = 0;
    private SelectListener listener;
    private InstorageInfo haveListener;
    private Disposable disposable;
    private String currentSeat;
    private Long carId;

    public MoveStorageDialog(@NonNull BaseActivity activity,SelectListener listener) {
        super(activity);
        this.listener = listener;
    }

    public MoveStorageDialog(@NonNull BaseActivity activity,Long carId,SelectListener listener) {
        super(activity);
        this.listener = listener;
        this.carId = carId;
    }

    public MoveStorageDialog(@NonNull BaseActivity activity,Long carId,String currentSeat,SelectListener listener) {
        super(activity);
        this.listener = listener;
        this.currentSeat = currentSeat;
        this.carId = carId;
    }

    public void setHaveListener(InstorageInfo haveListener) {
        this.haveListener = haveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.dialog_move_storage,null,false);
        setContentView(binding.getRoot());
        initView();
        setCanceledOnTouchOutside(false);
        loadData();
    }

    private void loadData() {

        disposable = AppApplication.getServerAPI().getEnableArea(AppApplication.getGaragePO().getWmsGarageId())
                .setOnDataGet(response->{
                    if(response.result == null || response.result.size() == 0){
                        showEmpty();
                        if(haveListener!=null){
                            haveListener.haveInfo(false);
                        }
                    }else{
                        showContent();
                        binding.change.setVisibility(View.VISIBLE);
                        carAreaInfos = response.result;
                        fillData();
                        haveListener.haveInfo(true);
                    }

                })
                .setDoOnSubscribe(disposable ->showProgress())
                .call();
    }

    private void initView() {
        if(StringUtil.isEmpty(currentSeat)){
            binding.currentStorage.setText("当前库位:--");
        }else{
            binding.currentStorage.setText("当前库位:"+currentSeat);
        }

        binding.list1.setOnSelectListener(((position, data) -> {

            if(position <carAreaInfos.size()){
                area = data;
                areaNum = position;
                row = carAreaInfos.get(areaNum).usbaleRowVOs.get(0).rowName;
                line = carAreaInfos.get(areaNum).usbaleRowVOs.get(0).usbaleLocationVOs.get(0).locationName;
                binding.selectStorage.setText(PREFIX+area+"-"+row+"-"+line);
                if(carAreaInfos.get(areaNum).usbaleRowVOs!=null){
                    binding.list2.setData(getListRow(carAreaInfos.get(areaNum).usbaleRowVOs));
                }
            }


            binding.list3.setData(getListLocation(carAreaInfos.get(areaNum).usbaleRowVOs.get(0).usbaleLocationVOs));
        }));

        binding.list2.setOnSelectListener(((position, data) -> {

            if(carAreaInfos.get(areaNum).usbaleRowVOs.size() > position){
                row = data;
                rowNum = position;
                line = carAreaInfos.get(areaNum).usbaleRowVOs.get(rowNum).usbaleLocationVOs.get(0).locationName;
                binding.selectStorage.setText(PREFIX+area+"-"+row+"-"+line);
                binding.list3.setData(getListLocation(carAreaInfos.get(areaNum).usbaleRowVOs.get(rowNum).usbaleLocationVOs));

                line = carAreaInfos.get(areaNum).usbaleRowVOs.get(rowNum).usbaleLocationVOs.get(0).locationName;
                binding.selectStorage.setText(PREFIX+area+"-"+row+"-"+line);
                if(carAreaInfos.get(areaNum).usbaleRowVOs.get(rowNum).usbaleLocationVOs!=null){
                    binding.list3.setData(getListLocation(carAreaInfos.get(areaNum).usbaleRowVOs.get(rowNum).usbaleLocationVOs));
                }

            }

        }));

        binding.list3.setOnSelectListener(((position, data) -> {
            if(carAreaInfos.get(areaNum).usbaleRowVOs.get(rowNum).usbaleLocationVOs.size()>position){
                line = data;
                locationNum = position;
                binding.selectStorage.setText(PREFIX+area+"-"+row+"-"+line);
            }

        }));

        binding.cancel.setOnClickListener(v->dismiss());

        binding.sure.setOnClickListener(v->{
            if(carAreaInfos == null || carAreaInfos.size()<=0){
                dismiss();
                return;
            }
            String[] texts = binding.selectStorage.getText().toString().split("：");
            Long id = carAreaInfos.get(areaNum).usbaleRowVOs.get(rowNum).usbaleLocationVOs.get(locationNum).areaPositionId;
            if(listener!=null){
                listener.onSelect(texts[1],id);

            }
            dismiss();
        });

        binding.change.setOnClickListener(v->{
            new SeatTableDialog(getContext(),carId,currentSeat == null?"":currentSeat,(seat,seatId)->{
                if(seatId == 0){
                    dismiss();
                    return;
                }
                if(listener!=null){
                    listener.onSelect(seat,seatId);
                    dismiss();
                }
            }).show();
        });

    }

    private void fillData(){
        if(carAreaInfos!=null && carAreaInfos.size()>0){
            binding.list1.setData(getListCarArea(carAreaInfos));
            if(carAreaInfos.get(0).usbaleRowVOs!=null){
                binding.list2.setData(getListRow(carAreaInfos.get(0).usbaleRowVOs));
                if(carAreaInfos.get(0).usbaleRowVOs.get(0).usbaleLocationVOs!=null){

                    area = carAreaInfos.get(0).areaName;
                    row = carAreaInfos.get(0).usbaleRowVOs.get(0).rowName;
                    line = carAreaInfos.get(0).usbaleRowVOs.get(0).usbaleLocationVOs.get(0).locationName;

                    binding.list3.setData(getListLocation(carAreaInfos.get(0).usbaleRowVOs.get(0).usbaleLocationVOs));
                    binding.selectStorage.setText(PREFIX+area+"-"+row+"-"+line);

                }
            }
        }else{
            binding.content.setVisibility(View.GONE);
            binding.empty.setVisibility(View.VISIBLE);
        }

    }
    private List<String> getListCarArea(List<CarAreaInfo> list){
        if(list == null ||list.size() == 0) {
            return new ArrayList<>();
        }
        List<String> ll = new ArrayList<>();
        for(CarAreaInfo info:list){
            ll.add(info.areaName);
        }
        return ll;
    }

    private List<String> getListRow(List<CarAreaInfo.CarRowInfo> list){
        if(list == null ||list.size() == 0) {
            return new ArrayList<>();
        }
        List<String> ll = new ArrayList<>();
        for(CarAreaInfo.CarRowInfo info:list){
            ll.add(info.rowName);
        }
        return ll;
    }

    private List<String> getListLocation(List<CarAreaInfo.CarLocationInfo> list){
        if(list == null ||list.size() == 0) {
            return new ArrayList<>();
        }
        List<String> ll = new ArrayList<>();
        for(CarAreaInfo.CarLocationInfo info:list){
            ll.add(info.locationName);
        }
        return ll;
    }

    public interface SelectListener{
        void onSelect(String name ,Long id);
    }

    public interface InstorageInfo{
        void haveInfo(boolean have);
    }

    private void showProgress(){
        binding.content.setVisibility(View.GONE);
        binding.empty.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void showContent(){
        binding.content.setVisibility(View.VISIBLE);
        binding.empty.setVisibility(View.GONE);
        binding.progress.setVisibility(View.GONE);
    }

    private void showEmpty(){
        binding.content.setVisibility(View.GONE);
        binding.empty.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.GONE);
    }

    @Override
    public void dismiss() {
        if(disposable!=null){
            disposable.dispose();
        }
        super.dismiss();

    }
}
