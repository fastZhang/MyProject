package com.zcl.showphone.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;
import com.zcl.showphone.IFace.ICallEventListener;
import com.zcl.showphone.utils.ScreenInfoUtil;

public class MIUPImageView extends AppCompatImageView {

    private int l, t, r, b;
    private boolean isInitLayout;

    public MIUPImageView(Context context) {
        super(context);
        init();
    }


    public MIUPImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MIUPImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {

    }

    int lastX, lastY;

    public boolean onTouchEvent(MotionEvent event) {

        if (!isInitLayout) {


            l = getLeft();
            t = getTop();
            r = getRight();
            b = getBottom();
            isInitLayout = true;
        }
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (getTop() - t < -ScreenInfoUtil.dip2px(getContext(), 40)) {
                    Log.d("MIUPImageView", "onTouchEvent: " + ScreenInfoUtil.dip2px(getContext(), 40));
                    ((ICallEventListener) getContext()).actionAp(this);
                }

                layout(l, t, r, b);

                break;
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //计算移动的距离
                int offX = x - lastX;
                int offY = y - lastY;
                //调用layout方法来重新放置它的位置
                layout(getLeft(), getTop() + offY, getRight(), getBottom() + offY);
                break;
        }
        return true;
    }


}
