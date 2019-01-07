package com.maihaoche.volvo.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.DialogSeatTableBinding;
import com.maihaoche.volvo.server.dto.request.AreaPositionMoveRequest;
import com.maihaoche.volvo.ui.common.daomain.AreaPositionDetail1;
import com.maihaoche.volvo.ui.common.daomain.GraphicQuery;
import com.maihaoche.volvo.ui.common.daomain.SearchResultInfo;
import com.maihaoche.volvo.view.seattable.SeatTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/9/15
 * Email is gujian@maihaoche.com
 */

public class SeatTableDialog extends Dialog {

    private DialogSeatTableBinding binding;
    private GraphicQuery.AreaPositionDetail[][] datas;
    private long seatId;
    private String seat;
    private SelectSeatListener listener;
    private String currentSeat;
    private Disposable disposable;
    private Long carId;

    public SeatTableDialog(@NonNull Context context,Long carId,String currentSeat,SelectSeatListener listener) {
        super(context);
        this.listener = listener;
        this.currentSeat = currentSeat;
        this.carId = carId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_seat_table,null,false);
        setContentView(binding.getRoot());

        loadData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void initView() {
        binding.seatView.setMoveMode(true);
        binding.seatView.setSeatChecker(new SeatTable.CarportChecker() {

            private boolean isPositionValid(int row, int column) {
                if (row >= datas.length || column >= datas[row].length) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public boolean isValid(int row, int column) {
                if (!isPositionValid(row, column)) {
                    return false;
                }
                return datas[row][column] != null;
            }

            @Override
            public boolean isOccupy(int row, int column) {
                if (!isPositionValid(row, column) || datas[row][column] == null) {
                    return false;
                }
                return datas[row][column].occupied;
            }

            @Override
            public boolean isUsed(int row, int column) {
                if (!isPositionValid(row, column)) {
                    return false;
                }

                if(datas[row][column] == null) {
                    return false;
                }
                return datas[row][column].hasCar;
            }

            @Override
            public void checked(int row, int column) {
                if (!isPositionValid(row, column)) {
                    return;
                }

                binding.seat.setText(getString(row,column));
                seatId = datas[row][column].areaPositionId;
                seat = getString(row,column);

            }

            @Override
            public void unCheck(int row, int column) {
                if (!isPositionValid(row, column)) {
                    return;
                }
                seatId = 0;
                seat = null;
            }

            @Override
            public void onCarClick(int row, int column) {
                if (!isPositionValid(row, column)) {
                    return;
                }
                if(binding.seatView.getMoveMode()){
                    AlertToast.show("该库位已停放车辆");
                    return;
                }
            }

            @Override
            public void occupy(int row, int colum) {
                if(binding.seatView.getMoveMode()){
                    AlertToast.show("该库位已被占用");
                    return;
                }
            }

            @Override
            public String checkedSeatArea(int row, int column) {
                if (!isPositionValid(row, column) || datas[row][column]==null) {
                    return new String();
                }

                return datas[row][column].areaName;
            }

            @Override
            public String[] checkedSeatTxt(int row, int column) {
                if (!isPositionValid(row, column)) {
                    return new String[]{};
                }
                if(datas[row][column] == null) {
                    return new String[]{};
                }
                return new String[]{datas[row][column].rowName,datas[row][column].locationName};
            }


        });
        binding.seat.setText("--");
        if(StringUtil.isEmpty(currentSeat)){
            binding.currentStorage.setText("当前库位：--");
        }else{
            binding.currentStorage.setText("当前库位："+currentSeat);
        }

        binding.cancel.setOnClickListener(v->dismiss());
        binding.sure.setOnClickListener(v->{
            if(StringUtil.isEmpty(currentSeat)){
                if(listener!=null){
                    listener.getSeat(seat,seatId);
                    dismiss();
                }
            }else{
                String select = binding.seat.getText().toString();
                new CommonDialog(getContext(),"车辆移位","是否将车辆从"+currentSeat+"移至"+select+"库位？",()->{
                    if(listener!=null){
                        listener.getSeat(seat,seatId);
                        dismiss();
                    }
                }).show();
            }


        });
    }

    private void loadData() {
        getSeatTable();
    }

    private String getString(int i,int j){
        String area = datas[i][j].areaName;
        String rowN = datas[i][j].rowName;
        String colN = datas[i][j].locationName;
        return area+"-"+rowN+"-"+colN;
    }

    public interface SelectSeatListener{
        void getSeat(String seat,long id);
    }

    private void getSeatTable(){

        disposable = AppApplication.getServerAPI().getGraphic(AppApplication.getGaragePO().getWmsGarageId())
                .setOnDataGet(response->{

                    if(response.result == null || response.result.areaPositions == null){
                        showEmpty();
                        return;
                    }

                    showContent();
                    datas = response.result.areaPositions;
                    binding.seatView.setDoorX(response.result.doorX);
                    binding.seatView.setData(datas.length,datas[0].length);
                    if(carId!=null){
                        getSelectCar();
                    }

                })
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setDoOnSubscribe(disposable1 ->showProgress())
                .call();
    }

    private void getSelectCar() {
        for(int i=0;i<datas.length;i++){
            for(int j=0;j<datas[i].length;j++){
                if(datas[i][j]!=null&&carId.equals(datas[i][j].carId)){
                    binding.seatView.setMovePoint(new Point(i,j));

                    break;
                }
            }
        }
    }

    private void showProgress(){
        binding.seatView.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);
        binding.empty.setVisibility(View.GONE);
    }

    private void showContent(){
        binding.seatView.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.GONE);
        binding.empty.setVisibility(View.GONE);
    }

    private void showEmpty(){
        binding.seatView.setVisibility(View.GONE);
        binding.progress.setVisibility(View.GONE);
        binding.empty.setVisibility(View.VISIBLE);
    }


}
