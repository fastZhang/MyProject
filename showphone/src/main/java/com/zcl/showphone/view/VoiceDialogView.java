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

import com.wq.photo.util.StartCamera;
import com.wq.photo.widget.PickConfig;
import com.yalantis.ucrop.UCrop;
import com.zcl.showphone.IFace.IEventListener;
import com.zcl.showphone.R;
import com.zcl.showphone.utils.AssetsPathUtils;
import com.zcl.showphone.utils.PicPathUtils;

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
import static com.zcl.showphone.utils.Constant.SYSTEM_ACTION_PICK_REQ;
import static com.zcl.showphone.utils.Constant.SYSTEM_VOICE_REQ;


public class VoiceDialogView extends Dialog {

    private Activity mContext;
    @BindView(R.id.ry_voice)
    RecyclerView ry_voice;

    String[] path = null;

    public VoiceDialogView(@NonNull final Activity context) {
        super(context);
        mContext = context;
        ((ViewGroup) getWindow().getDecorView()).addView(View.inflate(context, R.layout.view_voice_tip, null));
//        setContentView(R.layout.view_voice_tip);
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

        }


        @Override
        public int getItemCount() {
            int count = path == null ? 0 : path.length;
            return count;
        }

        class ListHolder extends RecyclerView.ViewHolder {
            public TextView name;

            public ListHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);

            }
        }
    }


}
