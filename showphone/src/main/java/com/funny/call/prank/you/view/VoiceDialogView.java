package com.funny.call.prank.you.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.call.prank.you.utils.ScreenInfoUtil;
import com.wq.photo.util.StartCamera;
import com.wq.photo.widget.PickConfig;
import com.yalantis.ucrop.UCrop;
import com.funny.call.prank.you.IFace.IEventListener;
import com.funny.call.prank.you.R;
import com.funny.call.prank.you.utils.AssetsPathUtils;
import com.funny.call.prank.you.utils.PicPathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.wq.photo.MediaChoseActivity.REQUEST_CODE_CAMERA;
import static com.wq.photo.util.StartCamera.currentfile;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;
import static com.funny.call.prank.you.utils.Constant.SYSTEM_ACTION_PICK_REQ;
import static com.funny.call.prank.you.utils.Constant.SYSTEM_VOICE_REQ;


public class VoiceDialogView extends Dialog {

    private Activity mContext;
    @BindView(R.id.ry_voice)
    RecyclerView ry_voice;

    String[] path = null;
    MediaPlayer voicePlayer;

    public VoiceDialogView(@NonNull final Activity context) {
        super(context);
        mContext = context;
        View rootView = View.inflate(context, R.layout.view_voice_tip, null);

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ry_voice.setLayoutManager(linearLayoutManager);


        try {
            path = AssetsPathUtils.getPathList(mContext, "voice");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (path != null) {

            ry_voice.setAdapter(new VoiceListAdapter());
        }

    }

    @OnClick({R.id.fl_sys_voice})
    void onClick(View view) {
        dismiss();

        switch (view.getId()) {
            case R.id.fl_sys_voice:

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                mContext.startActivityForResult(intent, SYSTEM_VOICE_REQ);

                break;
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Uri pickedUri = data.getData();
            if (null == pickedUri)
                return;

            Cursor cursor = mContext.managedQuery(pickedUri, null, null, null, null);

            if (cursor.moveToFirst()) {
//                for (int i = 0; i < 6; i++)
                String name = "";
                try {
                    name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                } catch (Exception e) {
                    name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                }

                if (!TextUtils.isEmpty(name)) {
                    String[] names = name.split("\\.");

                    ((IEventListener) mContext).setVoiceTextView(names[0], pickedUri);

                }
//                Log.d("cursor.getString(i)", "parseringData: " + );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class VoiceListAdapter extends RecyclerView.Adapter<VoiceListAdapter.ListHolder> {

        @NonNull
        @Override
        public VoiceListAdapter.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = View.inflate(mContext, R.layout.item_voice_tip, null);
            return new ListHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ListHolder holder, int position) {
            if (path == null) return;
            holder.name.setText(path[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name = path[position];
                    if (!TextUtils.isEmpty(name)) {
                        String[] names = name.split("\\.");
                        String path = "file:///android_asset/" + "voice/" + name;


                        ((IEventListener) mContext).setVoiceTextView(names[0], Uri.parse(path));

                    }

                    dismiss();

                }
            });
            holder.iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = path[position];
                    if (!TextUtils.isEmpty(name)) {
                        String[] names = name.split("\\.");
                        String path = "file:///android_asset/" + "voice/" + name;
                        stopVoice();
                        playVoice(path);

                    }
                }
            });

        }


        @Override
        public int getItemCount() {
            int count = path == null ? 0 : path.length;
            return count;
        }

        class ListHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public View iv_play;

            public ListHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                iv_play = itemView.findViewById(R.id.iv_play);

            }
        }
    }


    public void playVoice(String voice) {

        if (!voice.equals("")) {


            Uri voiceURI = Uri.parse(voice);

            voicePlayer = new MediaPlayer();

            try {

                if (voice.contains("android_asset")) {

                    String[] strings = voice.split("/");
                    AssetFileDescriptor fd = mContext.getAssets().openFd(strings[strings.length - 2] + "/" + strings[strings.length - 1]);
                    voicePlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());

                } else
                    voicePlayer.setDataSource(mContext, voiceURI);
            } catch (Exception e) {
                e.printStackTrace();
            }

            voicePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            voicePlayer.prepareAsync();

            voicePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            // 监听音频播放完的代码，实现音频的自动循环播放
            voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    voicePlayer.start();
//                    voicePlayer.setLooping(true);
                }
            });

        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        stopVoice();
    }

    private void stopVoice() {

        if (voicePlayer != null && voicePlayer.isPlaying()) {
            voicePlayer.stop();
        }

    }


}
