package com.zcl.showphone;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    public static void startActivity(Activity owner) {
        Intent intent = new Intent(owner, SettingActivity.class);

        owner.startActivity(intent);
        owner.overridePendingTransition(R.anim.activity_open, 0);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.iv_finish})
    void onClick(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.activity_close);

    }
}
