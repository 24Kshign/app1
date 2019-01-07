package com.maihaoche.volvo.view.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.PopwindowInStorageBinding;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2017/12/29
 * Email is gujian@maihaoche.com
 */

public class SelectPopWindow extends PopupWindow {

    private PopwindowInStorageBinding binding;
    private ClickFindCarListener findCarListener;
    private ClickSendListener sendListener;
    private ClickBindingListener bindingListener;

    public SelectPopWindow(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater,R.layout.popwindow_in_storage,null,false);
        setContentView(binding.getRoot());
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));

    }

    public View getContentView(){
        return binding.getRoot();
    }

    public void setArrowRight(){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.arrow.getLayoutParams();
        params.setMargins(0,0, SizeUtil.dip2px(-10),0);
        binding.arrow.setLayoutParams(params);
    }

    public void setFindCarListener(ClickFindCarListener findCarListener,String text) {
        binding.text1.setVisibility(View.VISIBLE);
        this.findCarListener = findCarListener;
        binding.text1.setText(text);
        binding.text1.setOnClickListener(v->{
            if(this.findCarListener!=null){
                this.findCarListener.click();
                dismiss();
            }
        });
    }

    public void setSendListener(ClickSendListener sendListener,String text) {
        binding.text2.setVisibility(View.VISIBLE);
        this.sendListener = sendListener;
        binding.text2.setText(text);
        binding.text2.setOnClickListener(v->{
            if(this.sendListener!=null){
                this.sendListener.click();
                dismiss();
            }
        });
    }

    public void setBindingListener(ClickBindingListener bindingListener,String text){
        binding.text3.setVisibility(View.VISIBLE);
        this.bindingListener = bindingListener;
        binding.text3.setText(text);
        binding.text3.setOnClickListener(v->{
            if(this.bindingListener!=null){
                this.bindingListener.click();
                dismiss();
            }
        });
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView   window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = SizeUtil.getScreenHeight(anchorView.getContext());
        final int screenWidth = SizeUtil.getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = anchorLoc[1] > screenHeight*(2*1.0/3);
        if (isNeedShowUp) {
            windowPos[0] = anchorLoc[0];
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = anchorLoc[0];
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    public interface ClickFindCarListener{
        void click();
    }

    public interface ClickSendListener{
        void click();
    }

    public interface ClickBindingListener{
        void click();
    }
}
