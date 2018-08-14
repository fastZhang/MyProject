package com.zcl.showphone.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zcl.showphone.IFace.IEventListener;
import com.zcl.showphone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CallModeDialogView extends Dialog {

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


    public CallModeDialogView(@NonNull final Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.view_callmode_tip);
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
        String text = "";
        String mode = mContext.getString(R.string.call_mode_type);
        switch (rp_sel.getCheckedRadioButtonId()) {
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

//
}
