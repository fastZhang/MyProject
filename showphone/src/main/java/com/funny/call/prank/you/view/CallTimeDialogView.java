package com.funny.call.prank.you.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.call.prank.you.ad.LoadIBannerAd;
import com.funny.call.prank.you.ad.LoadInterstitialAd;
import com.wq.photo.util.StartCamera;
import com.wq.photo.widget.PickConfig;
import com.yalantis.ucrop.UCrop;
import com.funny.call.prank.you.ControlActivity;
import com.funny.call.prank.you.IFace.IEventListener;
import com.funny.call.prank.you.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.wq.photo.MediaChoseActivity.REQUEST_CODE_CAMERA;
import static com.wq.photo.util.StartCamera.currentfile;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;


public class CallTimeDialogView extends Dialog {

    private Activity mContext;

    @BindView(R.id.rp_sel)
    RadioGroup rp_sel;

    @BindView(R.id.rb_3)
    RadioButton rb_3;
    @BindView(R.id.rb_30)
    RadioButton rb_30;
    @BindView(R.id.rb_60)
    RadioButton rb_60;
    @BindView(R.id.rb_120)
    RadioButton rb_120;


    public CallTimeDialogView(@NonNull final Activity context) {
        super(context);
        mContext = context;
        ((ViewGroup) getWindow().getDecorView()).addView(View.inflate(context, R.layout.view_calltime_tip, null));

//        setContentView(R.layout.view_calltime_tip);
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ButterKnife.bind(this);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.iv_finish).setOnClickListener(v -> {
            dismiss();
        });

        LoadIBannerAd.getInstance(mContext).loadBanner().showBanner(findViewById(R.id.fl_banner));

    }

    @OnClick({R.id.tv_active, R.id.rb_3, R.id.rb_30, R.id.rb_60, R.id.rb_120})
    void onClick(View view) {

        onSelId(view);


    }

    private void onSelId(View view) {
        String time = "";
        String timeNum = "";
        final String afterCall = " after call";
        switch (view.getId()) {
            case R.id.rb_3:
                time = (String) rb_3.getText();
                timeNum = "3";
                break;
            case R.id.rb_30:
                time = (String) rb_30.getText();
                timeNum = "30";

                break;
            case R.id.rb_60:
                time = (String) rb_60.getText();
                timeNum = "60";

                break;
            case R.id.rb_120:
                timeNum = "120";

                time = (String) rb_120.getText();
                break;
        }
        ((IEventListener) mContext).setTimeTextView(time + afterCall, timeNum);
        dismiss();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        LoadInterstitialAd.getInstance(mContext).showInterstitial(LoadInterstitialAd.getInstance(mContext).getTimeSetAd());

        LoadIBannerAd.getInstance(mContext).removeAd();


    }


    //
}
