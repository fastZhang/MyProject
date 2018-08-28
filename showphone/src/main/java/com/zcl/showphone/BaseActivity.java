package com.zcl.showphone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    protected Handler mHandler = new Handler();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = getLayout();
        setContentView(id);
        ButterKnife.bind(this);
        init();

    }

    @IdRes
    protected abstract int getLayout();


    protected abstract void init();
}
