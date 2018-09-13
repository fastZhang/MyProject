package com.funny.call.prank.you.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.funny.call.prank.you.IFace.IEventListener;
import com.funny.call.prank.you.R;
import com.funny.call.prank.you.utils.AssetsPathUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.funny.call.prank.you.utils.Constant.SYSTEM_VOICE_REQ;


public class PicListDialogView extends Dialog {

    private Activity mContext;

    @BindView(R.id.ry_voice)
    RecyclerView ry_voice;

    private List<String> imagesPath;

    public PicListDialogView(@NonNull final Activity context, List list) {
        super(context);
        mContext = context;
        imagesPath = list;

        View rootView = View.inflate(context, R.layout.view_pic_list, null);
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


        if (imagesPath != null) {
            ry_voice.setAdapter(new VoiceListAdapter());
        }

    }


    class VoiceListAdapter extends RecyclerView.Adapter<VoiceListAdapter.ListHolder> {
        String[] names;
        String path;

        @NonNull
        @Override
        public VoiceListAdapter.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = View.inflate(mContext, R.layout.item_pic_list, null);
            return new ListHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ListHolder holder, int position) {
            if (imagesPath == null) return;

            String name = imagesPath.get(position);
            if (!TextUtils.isEmpty(name)) {
                names = name.split("\\.");
                path = "file:///android_asset/" + "img_header/" + name;


                holder.name.setText(names[0]);
                Glide.with(mContext).load(path).into(holder.iv_pic);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((IEventListener) mContext).setIvAddImage(names[0], path);


                    dismiss();

                }
            });


        }


        @Override
        public int getItemCount() {
            int count = imagesPath == null ? 0 : imagesPath.size();
            return count;
        }

        class ListHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView iv_pic;

            public ListHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                iv_pic = itemView.findViewById(R.id.iv_pic);

            }
        }
    }


}
