package com.zcl.showphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.tv_add)
    TextView tv_add;


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControlActivity.startActivity(MainActivity.this);

            }
        });

    }
//
//    @OnClick(R.id.tv_add)
//    void onClick(View view) {
//        ControlActivity.startActivity(this);
//
//    }

}
