package com.maihaoche.volvo.ui.inwarehouse.cars;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.SoftKeyBoardUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivitySeatTableBinding;
import com.maihaoche.volvo.server.dto.request.AreaPositionMoveRequest;
import com.maihaoche.volvo.ui.common.daomain.GraphicQuery;
import com.maihaoche.volvo.ui.common.daomain.OccupyInfo;
import com.maihaoche.volvo.ui.common.daomain.SearchRequest;
import com.maihaoche.volvo.ui.common.daomain.SearchResultInfo;
import com.maihaoche.volvo.util.TextWatcherUtil;
import com.maihaoche.volvo.view.dialog.CommonDialog;
import com.maihaoche.volvo.view.seattable.SeatTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class SeatTableActivity extends HeaderProviderActivity<ActivitySeatTableBinding> {

    private static final String PATTERN = "^[A-Za-z0-9]+$";
    private GraphicQuery.AreaPositionDetail[][] datas;
    private ActivitySeatTableBinding binding;
    List<SearchResultInfo> searchResult = new ArrayList<>();
    private boolean isFirst = true;//搜索初始化时默认会发送一个空字符串，过滤掉
    private Point oldP;//要移位的车辆位置
    private int selectRow = -1;
    private int selectCol = -1;

    @Override
    public int getContentResId() {
        return R.layout.activity_seat_table;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        binding = getContentBinding();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
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
                if (!isPositionValid(row, column)){
                    return false;
                }

                if(datas[row][column] == null){
                    return false;
                }
                return datas[row][column].hasCar;
            }

            @Override
            public void checked(int row, int column) {
                if (!isPositionValid(row, column)){
                    return;
                }

                if(binding.seatView.getMoveMode()){
                    String ori = binding.currentSeat.getText().toString();
                    String curr = getString(row,column);
                    setSeletc(row,column);
                    new CommonDialog(SeatTableActivity.this,"车辆移位","是否将车辆从"+ori+"移至"+curr+"库位？",()->{
                        move(datas[row][column].areaPositionId);
                        binding.seatView.setMoveDialogMode(false);
                    },()->{
                        setSeletc(-1,-1);
                        binding.seatView.cleanSelects();
                        binding.seatView.invalidate();
                    }).show();
                    return;
                }

                showOccupy();
                setSeletc(row,column);
            }

            @Override
            public void unCheck(int row, int column) {
                if (!isPositionValid(row, column)){
                    return;
                }
                if(!binding.seatView.getMoveMode()){
                    hiddAll();
                }

                setSeletc(-1,-1);
            }

            @Override
            public void onCarClick(int row, int column) {
                if (!isPositionValid(row, column)){
                    return;
                }
                if(binding.seatView.getMoveMode()){
                    AlertToast.show("该库位已停放车辆");
                    return;
                }
                showInfo(row,column);
                setSeletc(row,column);
            }

            @Override
            public void occupy(int row, int colum) {
                //点击已被占用的位置

                if(binding.seatView.getMoveMode()){
                    AlertToast.show("该库位已被占用");
                    return;
                }

                showUnOccupy();
                setSeletc(row,colum);
            }

            @Override
            public String checkedSeatArea(int row, int column) {
                if (!isPositionValid(row, column) || datas[row][column]==null){
                    return new String();
                }

                return datas[row][column].areaName;
            }

            @Override
            public String[] checkedSeatTxt(int row, int column) {
                if (!isPositionValid(row, column)){
                    return new String[]{};
                }
                if(datas[row][column] == null){
                    return new String[]{};
                }
                return new String[]{datas[row][column].rowName,datas[row][column].locationName};
            }

        });
    }

    private void setSeletc(int row, int column) {
        selectRow = row;
        selectCol = column;
    }

    private void initView() {
        initHeader("仓库平面图");
        initSearch();
        binding.searchUnEnable.searchArea.setOnClickListener(v->{
            binding.searchUnEnable.searchArea.setVisibility(View.GONE);
            binding.searchEnable.searchArea.setVisibility(View.VISIBLE);
            binding.searchEnable.searchText.requestFocus();
            SoftKeyBoardUtil.showKeyBoardDely(binding.searchEnable.searchText);
        });
        binding.searchEnable.searchText.setOnKeyListener((view,keyCode,event)->{
            if(keyCode ==  KeyEvent.KEYCODE_ENTER){
                String text = binding.searchEnable.searchText.getText().toString().trim();
                if(Pattern.matches(PATTERN,text)&&text.length()<4){
                    AlertToast.show("车架号请输入后四位");

                }else{
                    getSearchValue(text);
                }

                return true;
            }
            return false;
        });
        binding.searchEnable.searchText.addTextChangedListener(new TextWatcherUtil(0, null, binding.searchEnable.clear));
        binding.searchEnable.clear.setOnClickListener(v->{
            binding.searchEnable.searchText.setText("");
        });
        binding.occupy.setOnClickListener(v->{
            if(selectRow == -1 || selectCol == -1){
                AlertToast.show("请先选择一个有效库位");
                return;
            }
            new CommonDialog(this,"占用车位","是否要占用当前库位",()->{
                datas[selectRow][selectCol].occupied = true;
                occupy(datas[selectRow][selectCol].areaPositionId,true);
            }).show();

        });
        binding.unOccupy.setOnClickListener(v->{
            if(selectRow == -1 || selectCol == -1){
                AlertToast.show("请先选择一个有效库位");
                return;
            }
            new CommonDialog(this,"解除占用","是否解除当前已占用库位",()->{
                datas[selectRow][selectCol].occupied = false;
                occupy(datas[selectRow][selectCol].areaPositionId,false);
            }).show();

        });
        binding.moveSeat.setOnClickListener(v->{
            binding.seatView.setMoveMode(true);
            binding.seatView.invalidate();
            binding.moveSeat.setVisibility(View.GONE);
            binding.unmoveSeat.setVisibility(View.VISIBLE);
            oldP = new Point(selectRow,selectCol);
            AlertToast.show("请选择要移动到的位置");
            binding.seatView.setMovePoint(new Point(selectRow,selectCol));
        });
        binding.unmoveSeat.setOnClickListener(v->{
            binding.moveSeat.setVisibility(View.VISIBLE);
            binding.unmoveSeat.setVisibility(View.GONE);
            binding.seatView.setMoveMode(false);
            binding.seatView.invalidate();
            binding.seatView.setMoveDialogMode(false);
            //取消移位的时候，让移位的车辆变为已选
            binding.seatView.addChooseSeat(oldP.x,oldP.y);
            oldP = null;
        });




    }

    private void initData() {
        getSeatTable();
    }

    protected void initSearch() {
        RxTextView.textChanges(binding.searchEnable.searchText)
                // 表示延时多少秒后执行，当你敲完字之后停下来的半秒就会执行下面语句
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // 数据转换 flatMap: 当同时多个数据请求访问的时候，前面的网络数据会覆盖后面的网络数据
                // 数据转换 switchMap: 当同时多个网络请求访问的时候，会以最后一个发送请求为准，前面网络数据会被最后一个覆盖
                .filter(charSequence-> {
                    if(isFirst){
                        isFirst = !isFirst;
                        return true;
                    }
                    getSearchValue(charSequence.toString());
                    return true;
                })
                .subscribe();
    }

    private void getSearchValue(String s) {
        if(StringUtil.isEmpty(s)){
            binding.seatView.setSearchMode(false);
            if(!binding.seatView.hasSelect()){
                hiddAll();
            }

        }else{//如果搜索的是车架号，4位后开始搜索
            if(Pattern.matches(PATTERN,s)){
                if(s.length()>=4){
                    search(s);
                }

            }else{
                search(s);
            }


        }
    }
    private void hiddAll(){
        binding.info.setVisibility(View.GONE);
        binding.infoOccupy.setVisibility(View.GONE);
        binding.infoUnoccupy.setVisibility(View.GONE);
        binding.scroll.invalidate();
    }
    private void showOccupy(){
        binding.info.setVisibility(View.GONE);
        binding.infoOccupy.setVisibility(View.VISIBLE);
        binding.infoUnoccupy.setVisibility(View.GONE);
    }

    private void showInfo(int row,int col){
        binding.frameCode.setText(datas[row][col].frameCode);
        binding.carAttribute.setText(datas[row][col].carAttribute);
        binding.currentSeat.setText(getString(row,col));
        binding.info.setVisibility(View.VISIBLE);
        binding.infoOccupy.setVisibility(View.GONE);
        binding.infoUnoccupy.setVisibility(View.GONE);
    }

    private void showUnOccupy(){
        binding.info.setVisibility(View.GONE);
        binding.infoOccupy.setVisibility(View.GONE);
        binding.infoUnoccupy.setVisibility(View.VISIBLE);
    }

    private String getString(int i,int j){
        String area = datas[i][j].areaName;
        String rowN = datas[i][j].rowName;
        String colN = datas[i][j].locationName;
        return area+"-"+rowN+"-"+colN;
    }

    private void getSeatTable(){

        AppApplication.getServerAPI().getGraphic(AppApplication.getGaragePO().getWmsGarageId())
                .setOnDataGet(response->{

                    if(response.result == null || response.result.areaPositions == null){
                        showEmpty();
                        setEmptyText("当前仓库尚未添加库位，无法展示平面图");
                        return;
                    }

                    showContent();

                    datas = response.result.areaPositions;
                    binding.seatView.setDoorX(response.result.doorX);
                    binding.seatView.clearSelect();
                    hiddAll();
                    binding.seatView.setData(datas.length,datas[0].length);

                })
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setDoOnSubscribe(disposable ->showProgress())
                .call(this);
    }

    private void occupy(long id,boolean flag){
        OccupyInfo request = new OccupyInfo();
        request.areaPositionId = id;
        request.occupied = flag;

        AppApplication.getServerAPI().occupy(request)
                .setOnDataGet(response->{
                    showContent();
                    if(flag){
                        AlertToast.show("占用成功");
                        showUnOccupy();
                    }else{
                        AlertToast.show("取消占用成功");
                        showOccupy();
                    }
                    datas[selectRow][selectCol].occupied = flag;
                    binding.seatView.invalidate();
                })
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setDoOnSubscribe(disposable ->showProgress())
                .call(this);
    }

    private void search(String param){
        SearchRequest request = new SearchRequest();
        request.warehouseId = AppApplication.getGaragePO().getWmsGarageId();
        request.searchParam = param;
        AppApplication.getServerAPI().searchCar(request)
                .setOnDataGet(response->{
                    if(response.result == null || response.result.size()<=0){
                        AlertToast.show("未搜索到数据");
                        return;
                    }
                    showContent();
                    searchResult = response.result;
                    binding.seatView.setSearchSeat(searchResult);
                    binding.seatView.setSearchMode(true);
                    showInfo(searchResult.get(0).row,searchResult.get(0).col);
                })
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .call(this);
    }

    private void move(long areaPositionId){
        AreaPositionMoveRequest request = new AreaPositionMoveRequest();
        request.carId = datas[oldP.x][oldP.y].carId;
        request.carStoreType = datas[oldP.x][oldP.y].carStoreType;
        request.areaPositionId = areaPositionId;
        AppApplication.getServerAPI().areaPosition(request)
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(baseResponse -> {
                    AlertToast.show("移位成功");
                    showContent();
                    if(oldP!=null){
                        switchInfo(datas[oldP.x][oldP.y],datas[selectRow][selectCol]);
                        resetSearch(oldP.x,oldP.y,selectRow,selectCol);
                    }
                    binding.seatView.setMoveMode(false);
                    binding.currentSeat.setText(getString(selectRow,selectCol));
                    binding.moveSeat.setVisibility(View.VISIBLE);
                    binding.unmoveSeat.setVisibility(View.GONE);
                    binding.seatView.invalidate();
                })
                .setDoOnSubscribe(disposable ->showProgress())
                .call(this);


    }

    private void resetSearch(int x, int y, int selectRow, int selectCol) {

        for(int i=0;i<searchResult.size();i++){

            if(searchResult.get(i).row == x && searchResult.get(i).col == y){
                searchResult.remove(i);
                searchResult.add(i,new SearchResultInfo(selectRow,selectCol));
                return;
            }
        }
    }


    private void switchInfo(GraphicQuery.AreaPositionDetail oriData,GraphicQuery.AreaPositionDetail newData){
        newData.carId = oriData.carId;
        newData.hasCar = true;
        newData.frameCode = oriData.frameCode;
        newData.carAttribute = oriData.carAttribute;
        newData.carStoreType = oriData.carStoreType;

        oriData.carId = 0L;
        oriData.carAttribute = "";
        oriData.hasCar = false;
        oriData.frameCode = "";
    }
}
