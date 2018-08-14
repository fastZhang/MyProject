package com.zcl.showphone.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.photo.util.StartCamera;
import com.wq.photo.widget.PickConfig;
import com.yalantis.ucrop.UCrop;
import com.zcl.showphone.ControlActivity;
import com.zcl.showphone.IFace.IEventListener;
import com.zcl.showphone.R;

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

    private Context mContext;

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


    public CallTimeDialogView(@NonNull final Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.view_calltime_tip);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.tv_active})
    void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_active:
                onSelId();
                break;

        }


    }

    private void onSelId() {
        String time = "";
        String timeNum = "";
        switch (rp_sel.getCheckedRadioButtonId()) {
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
        ((IEventListener) mContext).setTimeTextView(time, timeNum);
        dismiss();

    }

//
}
