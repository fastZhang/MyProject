package com.funny.call.prank.you.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.funny.call.prank.you.IFace.IEventListener;
import com.funny.call.prank.you.R;
import com.funny.call.prank.you.ad.LoadIBannerAd;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CallModeDialogView extends Dialog {

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


    public CallModeDialogView(@NonNull final Activity context) {
        super(context);
        mContext = context;
        ((ViewGroup) getWindow().getDecorView()).addView(View.inflate(context, R.layout.view_callmode_tip, null));
//        setContentView(R.layout.view_callmode_tip);
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
        String text = "";
        String mode = mContext.getString(R.string.call_mode_type);
        switch (view.getId()) {
            case R.id.rb_3:
//                不重复
                text = (String) rb_3.getText();
                mode = mContext.getString(R.string.call_mode_type);
                break;
            case R.id.rb_30:
//                每次挂断后10秒之后再次响铃，持续3回
                text = (String) rb_30.getText();
                mode = "10";

                break;
            case R.id.rb_60:
//                每次挂断后30秒之后再次响铃，持续3回
                text = (String) rb_60.getText();
                mode = "30";

                break;
            case R.id.rb_120:
//                每次挂断后60秒之后再次响铃，持续3回
                mode = "60";

                text = (String) rb_120.getText();
                break;
        }

        ((IEventListener) mContext).setModeTextView(text, mode);
        dismiss();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        LoadIBannerAd.getInstance(mContext).removeAd();

    }

    //
}
