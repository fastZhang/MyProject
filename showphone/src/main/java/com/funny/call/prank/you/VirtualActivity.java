package com.funny.call.prank.you;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class VirtualActivity extends BaseActivity {
    public static void startActivity(Activity owner) {
        Intent intent = new Intent(owner, VirtualActivity.class);
        owner.startActivity(intent);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_virtual;
    }

    @Override
    protected void init() {

    }
}
