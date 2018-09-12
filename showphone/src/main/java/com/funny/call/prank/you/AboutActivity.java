package com.funny.call.prank.you;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.funny.call.prank.you.utils.AppInfoUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView tv_version;

    public static void startActivity(Activity owner) {
        Intent intent = new Intent(owner, AboutActivity.class);
        owner.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        tv_version.setText(AppInfoUtil.getVersion(this));
    }

    @OnClick({R.id.iv_finish})
    void onClick(View view) {
        finish();

    }
}

