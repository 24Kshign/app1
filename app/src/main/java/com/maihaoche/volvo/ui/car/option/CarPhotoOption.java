package com.maihaoche.volvo.ui.car.option;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.volvo.server.dto.CarDTO;
import com.maihaoche.volvo.server.dto.CarExtraInfoDTO;
import com.maihaoche.volvo.util.QiNiuUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by gujian
 * Time is 2017/7/3
 * Email is gujian@maihaoche.com
 */

public class CarPhotoOption {

    private BaseActivity activity;
    //在对象的传输过程中，是否有失败
    private boolean isFailed;

    private List<CarDTO> carDTO;
    //每辆车有几张照片
    private int imageCount;
    //当前上传到地几辆车
    private int index;
    //总共多少张照片
    private int totleCount;
    //当前上传到第几张
    private int currentIndex;
    //全部车辆上传完成回调
    private CarPhotoOption.UpLoadListener upLoadListener;
    //每个对象的图片item
    private List<CarPhotoOption.UploadItem> uploadItems;

    private QiNiuUtil.CancleUpload cancel = QiNiuUtil.getInstence();

    //获取每个对象的图片总数
    private int getImageCount(CarExtraInfoDTO carExtraInfoDTO) {
        imageCount = 5;
        if (carExtraInfoDTO.carAbnormals != null) {
            imageCount += carExtraInfoDTO.carAbnormals.size();
        }
        return imageCount;
    }

    public CarPhotoOption(BaseActivity activity, List<CarDTO> carDTO, CarPhotoOption.UpLoadListener upLoadListener){
        this.carDTO = carDTO;
        this.upLoadListener = upLoadListener;
        this.activity = activity;
        imageCount = 0;
        index = 0;
        isFailed = false;
        totleCount = getTotleCount();
        currentIndex = 0;
        uploadItems = new ArrayList<>();


    }

    public void start(){
        QiNiuUtil.getToken(activity,token->{
            upLoadObject();
        },msg->{
            if(upLoadListener!=null){
                upLoadListener.falied(msg);
            }
        });

    }

    private int getTotleCount(){
        int totle = carDTO.size()*5;
        for(int i=0;i<carDTO.size();i++){
            if (carDTO.get(i).carAbnormals != null) {
                totle += carDTO.get(i).carAbnormals.size();
            }
        }
        return totle;
    }

    private void upLoadObject() {
        uploadItems.clear();
        CarDTO carDTO = this.carDTO.get(index);
        imageCount = getImageCount(carDTO);
        uploadItems.add(new CarPhotoOption.UploadItem(carDTO.photoFront45Degree,""));
        uploadItems.add(new CarPhotoOption.UploadItem(carDTO.photoBack45Degree,""));
        uploadItems.add(new CarPhotoOption.UploadItem(carDTO.photoCard,""));
        uploadItems.add(new CarPhotoOption.UploadItem(carDTO.photoDistance,""));
        uploadItems.add(new CarPhotoOption.UploadItem(carDTO.photoInner,""));

        QiNiuUtil.uploadImg(activity,carDTO.photoFront45Degree,new MyListener(0),cancel);
        QiNiuUtil.uploadImg(activity,carDTO.photoBack45Degree,new MyListener(1),cancel);
        QiNiuUtil.uploadImg(activity,carDTO.photoCard,new MyListener(2),cancel);
        QiNiuUtil.uploadImg(activity,carDTO.photoDistance,new MyListener(3),cancel);
        QiNiuUtil.uploadImg(activity,carDTO.photoInner,new MyListener(4),cancel);
        for (int i = 0; i < carDTO.carAbnormals.size(); i++) {
            final int index = 5+i;
            CarExtraInfoDTO.CarPhotoAbnormal carPhotoAbnormal = carDTO.carAbnormals.get(i);
            uploadItems.add(new CarPhotoOption.UploadItem(carPhotoAbnormal.picPath,""));
            QiNiuUtil.uploadImg(activity,carPhotoAbnormal.picPath,new MyListener(index),cancel);
        }

    }


    private void fillUrl() {
        CarDTO carDTO = this.carDTO.get(index);
        carDTO.photoFront45Degree = uploadItems.get(0).serverUrl;
        carDTO.photoBack45Degree = uploadItems.get(1).serverUrl;
        carDTO.photoCard = uploadItems.get(2).serverUrl;
        carDTO.photoDistance = uploadItems.get(3).serverUrl;
        carDTO.photoInner = uploadItems.get(4).serverUrl;
        for(int i=0;i<carDTO.carAbnormals.size();i++){
            carDTO.carAbnormals.get(i).picPath = uploadItems.get(5+i).serverUrl;
        }
    }

    public interface UpLoadListener {
        void success(List<CarDTO> carDTOs);
        void falied(String desc);
        void progress(String pro);
    }

    //上传图片对象
    static class UploadItem{
        public String localUrl;
        public String serverUrl;
        public int count;

        public UploadItem(String localUrl, String serverUrl) {
            this.localUrl = localUrl;
            this.serverUrl = serverUrl;
            this.count = 0;
        }
    }

    class MyListener implements QiNiuUtil.Listener{

        private int count;

        public MyListener(int index) {
            this.count = index;
        }

        @Override
        public void success(String url) {
            currentIndex++;
            imageCount--;
            LogUtil.e("upload success one");
            uploadItems.get(count).serverUrl = url;
            if(upLoadListener!=null){
                upLoadListener.progress(currentIndex+"/"+totleCount+"");
            }
            if (imageCount == 0) {
                LogUtil.e("upload success object");
                fillUrl();
                index++;
                if(isFailed){//整个对象传输过程有失败
                    if(upLoadListener!=null){
                        upLoadListener.falied("上传图片失败："+url);
                    }

                }else if (index < carDTO.size()) {
                    upLoadObject();
                } else {
                    if (upLoadListener != null) {
                        LogUtil.e("upload success all");
                        upLoadListener.success(carDTO);
                    }
                }

            }
        }

        @Override
        public void failed(String url) {
            for(int i=0;i<uploadItems.size();i++){
                CarPhotoOption.UploadItem uploadItem = uploadItems.get(i);
                if(url.equals(uploadItem.localUrl) && uploadItem.count <3){
                    uploadItem.count++;
                    QiNiuUtil.uploadImg(activity,url,this,cancel);
                }else{
                    isFailed = true;
                }
            }
        }
    }
}
