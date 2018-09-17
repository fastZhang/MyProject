package com.funny.call.prank.you.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.call.prank.you.IFace.IEventListener;
import com.funny.call.prank.you.R;
import com.funny.call.prank.you.ad.LoadIBannerAd;
import com.funny.call.prank.you.utils.AssetsPathUtils;
import com.funny.call.prank.you.utils.CallSettingUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.funny.call.prank.you.utils.Constant.SYSTEM_VOICE_REQ;


public class ThemeDialogView extends Dialog {

    private Activity mContext;


    @BindView(R.id.theme_android)
    TextView theme_android;
    @BindView(R.id.theme_mi)
    TextView theme_mi;
    @BindView(R.id.theme_samsung)
    TextView theme_samsung;
    @BindView(R.id.theme_blur)
    TextView theme_blur;

    public CallSettingUtil.CallTheme getCallTheme() {
        return callTheme;
    }

    public void setCallTheme(CallSettingUtil.CallTheme callTheme) {
        this.callTheme = callTheme;
    }

    private CallSettingUtil.CallTheme callTheme = null;


    public ThemeDialogView(@NonNull final Activity context) {
        super(context);
        mContext = context;
        View rootView = View.inflate(context, R.layout.activity_theme, null);

        ((ViewGroup) getWindow().getDecorView()).addView(rootView);


        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ButterKnife.bind(this);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        theme_android.setTextColor(CallSettingUtil.isThisCalltheme(mContext, CallSettingUtil.CallTheme.ANDROID5) ? mContext.getResources().getColor(R.color.colorMain) : mContext.getResources().getColor(R.color.colorMenuTitle));
        theme_mi.setTextColor(CallSettingUtil.isThisCalltheme(mContext, CallSettingUtil.CallTheme.MI) ? mContext.getResources().getColor(R.color.colorMain) : mContext.getResources().getColor(R.color.colorMenuTitle));
        theme_blur.setTextColor(CallSettingUtil.isThisCalltheme(mContext, CallSettingUtil.CallTheme.BLUR) ? mContext.getResources().getColor(R.color.colorMain) : mContext.getResources().getColor(R.color.colorMenuTitle));
        theme_samsung.setTextColor(CallSettingUtil.isThisCalltheme(mContext, CallSettingUtil.CallTheme.SAMSUNG) ? mContext.getResources().getColor(R.color.colorMain) : mContext.getResources().getColor(R.color.colorMenuTitle));
        findViewById(R.id.iv_finish).setOnClickListener(v -> {
            dismiss();
        });

        LoadIBannerAd.getInstance(mContext).loadBanner().showBanner(findViewById(R.id.fl_banner));

    }


    @OnClick({R.id.try1, R.id.try2, R.id.try_blur, R.id.try_samsung,
            R.id.fl_theme_mi, R.id.fl_theme_android, R.id.fl_theme_blur, R.id.fl_theme_samsung})
    void onClick(View view) {

        switch (view.getId()) {
            case R.id.fl_theme_mi:
                callTheme = CallSettingUtil.CallTheme.MI;

                themeSelect((TextView) theme_mi, CallSettingUtil.CallTheme.MI);

                break;
            case R.id.fl_theme_samsung:
                callTheme = CallSettingUtil.CallTheme.SAMSUNG;

                themeSelect((TextView) theme_samsung, CallSettingUtil.CallTheme.SAMSUNG);

                break;
            case R.id.fl_theme_android:
                callTheme = CallSettingUtil.CallTheme.ANDROID5;

                themeSelect((TextView) theme_android, CallSettingUtil.CallTheme.ANDROID5);

                break;
            case R.id.fl_theme_blur:
                callTheme = CallSettingUtil.CallTheme.BLUR;

                themeSelect((TextView) theme_blur, CallSettingUtil.CallTheme.BLUR);

                break;


            case R.id.try1:
                ((IEventListener) mContext).onStartCall();
                break;
            case R.id.try2:
                ((IEventListener) mContext).onStartCall();

                break;

            case R.id.try_blur:

                ((IEventListener) mContext).onStartCall();
                break;

            case R.id.try_samsung:
                ((IEventListener) mContext).onStartCall();

                break;
        }


    }

    private void themeSelect(TextView view, CallSettingUtil.CallTheme theme) {

        Toast.makeText(mContext, mContext.getResources().getString(R.string.text_select_theme), Toast.LENGTH_SHORT).show();

        theme_android.setTextColor(mContext.getResources().getColor(R.color.colorMenuTitle));
        theme_mi.setTextColor(mContext.getResources().getColor(R.color.colorMenuTitle));
        theme_samsung.setTextColor(mContext.getResources().getColor(R.color.colorMenuTitle));
        theme_blur.setTextColor(mContext.getResources().getColor(R.color.colorMenuTitle));

        view.setTextColor(mContext.getResources().getColor(R.color.colorMain));
        CallSettingUtil.setCallTheme(mContext, theme);
        if (callTheme != null)
            ((IEventListener) mContext).setCallThemeText(callTheme.name());
        dismiss();


    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (callTheme != null)
            ((IEventListener) mContext).setCallThemeText(callTheme.name());


        LoadIBannerAd.getInstance(mContext).removeAd();


    }
}
