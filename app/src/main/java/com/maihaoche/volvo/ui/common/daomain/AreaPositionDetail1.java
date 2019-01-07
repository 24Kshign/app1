package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/9/19
 * Email is gujian@maihaoche.com
 */

public class AreaPositionDetail1 implements Serializable {

    @SerializedName("areaPositionId")
    @Expose
    public Long areaPositionId;//库位ID

    @SerializedName("warehouseId")
    @Expose
    public Long warehouseId;//对应仓库ID

    @SerializedName("areaId")
    @Expose
    public Integer areaId;//库区ID

    @SerializedName("areaName")
    @Expose
    public String areaName;//库区名称

    @SerializedName("rowId")
    @Expose
    public Integer rowId;//排ID

    @SerializedName("rowName")
    @Expose
    public String rowName;//排名称

    @SerializedName("locationId")
    @Expose
    public Integer locationId;//列ID

    @SerializedName("locationName")
    @Expose
    public String locationName;//列名称

    @SerializedName("occupied")
    @Expose
    public Boolean occupied;//是否占用

    @SerializedName("hasCar")
    @Expose
    public Boolean hasCar;//是否绑定车

    @SerializedName("hasCar")
    @Expose
    public String frameCode;//车架号

    @SerializedName("hasCar")
    @Expose
    public String carAttribute;//车辆属性

//    public AreaPositionDetail1(String name,String a,String b,boolean ...value){
//        areaName = name;
//        rowName = a;
//        locationName = b;
//        if(value!=null && value.length>0){
//            occupied = value[0];
//        }
//    }
//
//    public static AreaPositionDetail1[][] getdata(){
//        AreaPositionDetail1[][] datas = new AreaPositionDetail1[][]{
//                {new AreaPositionDetail1("A","1","1"),new AreaPositionDetail1("A","1","2"),new AreaPositionDetail1("A","1","3"),new AreaPositionDetail1("A","1","4"),new AreaPositionDetail1("A","1","5"),new AreaPositionDetail1("A","1","6"),new AreaPositionDetail1("A","1","7"),null,null,null},
//                {new AreaPositionDetail1("A","2","1"),new AreaPositionDetail1("A","2","2"),new AreaPositionDetail1("A","2","3"),new AreaPositionDetail1("A","2","4"),new AreaPositionDetail1("A","2","5"),new AreaPositionDetail1("A","2","6"),new AreaPositionDetail1("A","2","7"),null,null,null},
//                {null,null,null,null,null,null,null,null,null,null},
//                {new AreaPositionDetail1("B","1","1"),new AreaPositionDetail1("B","1","2"),new AreaPositionDetail1("B","1","3"),new AreaPositionDetail1("B","1","4"),new AreaPositionDetail1("B","1","5"),new AreaPositionDetail1("B","1","6"),new AreaPositionDetail1("B","1","7"),null,null,null},
//                {new AreaPositionDetail1("B","2","1"),new AreaPositionDetail1("B","2","2"),new AreaPositionDetail1("B","2","3"),new AreaPositionDetail1("B","2","4"),new AreaPositionDetail1("B","2","5"),new AreaPositionDetail1("B","2","6"),new AreaPositionDetail1("B","2","7"),null,null,null},
//                {new AreaPositionDetail1("B","3","1"),new AreaPositionDetail1("B","3","2"),new AreaPositionDetail1("B","3","3"),new AreaPositionDetail1("B","3","4"),new AreaPositionDetail1("B","3","5"),new AreaPositionDetail1("B","3","6"),new AreaPositionDetail1("B","3","7"),null,null,null},
//                {new AreaPositionDetail1("B","4","1",true),new AreaPositionDetail1("B","4","2",true),new AreaPositionDetail1("B","4","3"),new AreaPositionDetail1("B","4","4"),new AreaPositionDetail1("B","4","5"),new AreaPositionDetail1("B","4","6"),new AreaPositionDetail1("B","4","7"),null,null,null},
//                {null,null,null,null,null,null,null,null,null,null},
//                {null,null,null,null,null,null,null,null,null,null},
//                {null,null,null,null,null,null,null,null,null,null}
//        };
//
//        return datas;
//    }
}




//res = new String[][]{
//        {"F-1-1", "F-1-2", "F-1-3", null, "A4-1-1", "A4-1-2", "A4-1-3", "A4-1-4", "A4-1-5", "A4-1-6", "A4-1-7", "A4-1-8", "A4-1-9", "A4-1-10", null, "B4-1-1", "B4-1-2", "B4-1-3", "B4-1-4", "B4-1-5", "B4-1-6", "B4-1-7", "B4-1-8", "B4-1-9", "B4-1-10", null, "C4-1-1", "C4-1-2", "C4-1-3", "C4-1-4", "C4-1-5", "C4-1-6", "C4-1-7", "C4-1-8", "C4-1-9", "C4-1-10", null, "D4-1-1", "D4-1-2", "D4-1-3", "D4-1-4", "D4-1-5", "D4-1-6", "D4-1-7", "D4-1-8", "D4-1-9", "D4-1-10", null, "E4-1-1", "E4-1-2", "E4-1-3", "E4-1-4", "E4-1-5", "E4-1-6", "E4-1-7", "E4-1-8", "E4-1-9"},
//        {"F-2-1", "F-2-2", "F-2-3", null, "A4-2-1", "A4-2-2", "A4-2-3", "A4-2-4", "A4-2-5", "A4-2-6", "A4-2-7", "A4-2-8", "A4-2-9", "A4-2-10", null, "B4-2-1", "B4-2-2", "B4-2-3", "B4-2-4", "B4-2-5", "B4-2-6", "B4-2-7", "B4-2-8", "B4-2-9", "B4-2-10", null, "C4-2-1", "C4-2-2", "C4-2-3", "C4-2-4", "C4-2-5", "C4-2-6", "C4-2-7", "C4-2-8", "C4-2-9", "C4-2-10", null, "D4-2-1", "D4-2-2", "D4-2-3", "D4-2-4", "D4-2-5", "D4-2-6", "D4-2-7", "D4-2-8", "D4-2-9", "D4-2-10", null, "E4-2-1", "E4-2-2", "E4-2-3", "E4-2-4", "E4-2-5", "E4-2-6", "E4-2-7", "E4-2-8", "E4-2-9"},
//        {"F-3-1", "F-3-2", "F-3-3", null, "A4-3-1", "A4-3-2", "A4-3-3", "A4-3-4", "A4-3-5", "A4-3-6", "A4-3-7", "A4-3-8", "A4-3-9", "A4-3-10", null, "B4-3-1", "B4-3-2", "B4-3-3", "B4-3-4", "B4-3-5", "B4-3-6", "B4-3-7", "B4-3-8", "B4-3-9", "B4-3-10", null, "C4-3-1", "C4-3-2", "C4-3-3", "C4-3-4", "C4-3-5", "C4-3-6", "C4-3-7", "C4-3-8", "C4-3-9", "C4-3-10", null, "D4-3-1", "D4-3-2", "D4-3-3", "D4-3-4", "D4-3-5", "D4-3-6", "D4-3-7", "D4-3-8", "D4-3-9", "D4-3-10", null, "E4-3-1", "E4-3-2", "E4-3-3", "E4-3-4", "E4-3-5", "E4-3-6", "E4-3-7", "E4-3-8", "E4-3-9"},
//        {"F-4-1", "F-4-2", "F-4-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {"F-5-1", "F-5-2", "F-5-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {"F-6-1", "F-6-2", "F-6-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {"F-7-1", "F-7-2", "F-7-3", null, "A3-1-1", "A3-1-2", "A3-1-3", "A3-1-4", "A3-1-5", "A3-1-6", "A3-1-7", "A3-1-8", "A3-1-9", "A3-1-10", null, "B3-1-1", "B3-1-2", "B3-1-3", "B3-1-4", "B3-1-5", "B3-1-6", "B3-1-7", "B3-1-8", "B3-1-9", "B3-1-10", null, "C3-1-1", "C3-1-2", "C3-1-3", "C3-1-4", "C3-1-5", "C3-1-6", "C3-1-7", "C3-1-8", "C3-1-9", "C3-1-10", null, "D3-1-1", "D3-1-2", "D3-1-3", "D3-1-4", "D3-1-5", "D3-1-6", "D3-1-7", "D3-1-8", "D3-1-9", "D3-1-10", null, "E3-1-1", "E3-1-2", "E3-1-3", "E3-1-4", "E3-1-5", "E3-1-6", "E3-1-7", "E3-1-8", "E3-1-9"},
//        {"F-8-1", "F-8-2", "F-8-3", null, "A3-2-1", "A3-2-2", "A3-2-3", "A3-2-4", "A3-2-5", "A3-2-6", "A3-2-7", "A3-2-8", "A3-2-9", "A3-2-10", null, "B3-2-1", "B3-2-2", "B3-2-3", "B3-2-4", "B3-2-5", "B3-2-6", "B3-2-7", "B3-2-8", "B3-2-9", "B3-2-10", null, "C3-2-1", "C3-2-2", "C3-2-3", "C3-2-4", "C3-2-5", "C3-2-6", "C3-2-7", "C3-2-8", "C3-2-9", "C3-2-10", null, "D3-2-1", "D3-2-2", "D3-2-3", "D3-2-4", "D3-2-5", "D3-2-6", "D3-2-7", "D3-2-8", "D3-2-9", "D3-2-10", null, "E3-2-1", "E3-2-2", "E3-2-3", "E3-2-4", "E3-2-5", "E3-2-6", "E3-2-7", "E3-2-8", "E3-2-9"},
//        {"F-9-1", "F-9-2", "F-9-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {"F-10-1", "F-10-2", "F-10-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {"F-11-1", "F-11-2", "F-11-3", null, "A2-1-1", "A2-1-2", "A2-1-3", "A2-1-4", "A2-1-5", "A2-1-6", "A2-1-7", "A2-1-8", "A2-1-9", "A2-1-10", null, "B2-1-1", "B2-1-2", "B2-1-3", "B2-1-4", "B2-1-5", "B2-1-6", "B2-1-7", "B2-1-8", "B2-1-9", "B2-1-10", null, "C2-1-1", "C2-1-2", "C2-1-3", "C2-1-4", "C2-1-5", "C2-1-6", "C2-1-7", "C2-1-8", "C2-1-9", "C2-1-10", null, "D2-1-1", "D2-1-2", "D2-1-3", "D2-1-4", "D2-1-5", "D2-1-6", "D2-1-7", "D2-1-8", "D2-1-9", "D2-1-10", null, "E2-1-1", "E2-1-2", "E2-1-3", "E2-1-4", "E2-1-5", "E2-1-6", "E2-1-7", "E2-1-8", "E2-1-9"},
//        {"F-12-1", "F-12-2", "F-12-3", null, "A2-2-1", "A2-2-2", "A2-2-3", "A2-2-4", "A2-2-5", "A2-2-6", "A2-2-7", "A2-2-8", "A2-2-9", "A2-1-10", null, "B2-2-1", "B2-2-2", "B2-2-3", "B2-2-4", "B2-2-5", "B2-2-6", "B2-2-7", "B2-2-8", "B2-2-9", "B2-2-10", null, "C2-2-1", "C2-2-2", "C2-2-3", "C2-2-4", "C2-2-5", "C2-2-6", "C2-2-7", "C2-2-8", "C2-2-9", "C2-2-10", null, "D2-2-1", "D2-2-2", "D2-2-3", "D2-2-4", "D2-2-5", "D2-2-6", "D2-2-7", "D2-2-8", "D2-2-9", "D2-2-10", null, "E2-2-1", "E2-2-2", "E2-2-3", "E2-2-4", "E2-2-5", "E2-2-6", "E2-2-7", "E2-2-8", "E2-2-9"},
//        {"F-13-1", "F-13-2", "F-13-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {"F-14-1", "F-14-2", "F-14-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {"F-15-1", "F-15-2", "F-15-3", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
//        {null, null, null, null, "A1-1-1", "A1-1-2", "A1-1-3", "A1-1-4", "A1-1-5", "A1-1-6", "A1-1-7", "A1-1-8", "A1-1-9", "A1-1-10", null, "B1-1-1", "B1-1-2", "B1-1-3", "B1-1-4", "B1-1-5", "B1-1-6", "B1-1-7", "B1-1-8", "B1-1-9", "B1-1-10", null, "C1-1-1", "C1-1-2", "C1-1-3", "C1-1-4", "C1-1-5", "C1-1-6", "C1-1-7", "C1-1-8", "C1-1-9", "C1-1-10", null, "D1-1-1", "D1-1-2", "D1-1-3", "D1-1-4", "D1-1-5", "D1-1-6", "D1-1-7", "D1-1-8", "D1-1-9", "D1-1-10", null, "E1-1-1", "E1-1-2", "E1-1-3", "E1-1-4", "E1-1-5", "E1-1-6", "E1-1-7", "E1-1-8", "E1-1-9"},
//        };
//
//        for (int i = 1; i < 11; i++) {
//        String position = "F-1-" + i;
//        mCarPauseMap.put(position, "保时捷 911,一骑轻尘");
//        }
//
//        points = new ArrayList<>();
//        points.add(new Point(0,2));
//        points.add(new Point(0,6));
//        points.add(new Point(0,15));
//        points.add(new Point(5,8));
//        points.add(new Point(11,19));
//        binding.seatView.setSearchSeat(points);
//        binding.seatView.setSearchMode(true);


