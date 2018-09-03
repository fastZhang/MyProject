package com.zcl.showphone.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.wq.photo.MediaChoseActivity;
import com.wq.photo.util.StartCamera;
import com.wq.photo.widget.PickConfig;
import com.yalantis.ucrop.UCrop;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zcl.showphone.IFace.IEventListener;
import com.zcl.showphone.R;
import com.zcl.showphone.utils.PicPathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.wq.photo.MediaChoseActivity.REQUEST_CODE_CAMERA;
import static com.wq.photo.util.StartCamera.currentfile;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;
import static com.zcl.showphone.utils.Constant.SYSTEM_ACTION_PICK_REQ;


public class PicTipDialogView extends Dialog {

    @BindView(R.id.fl_picphoto)
    FrameLayout fl_picphoto;
    private Context mContext;
    private int mChosemode = PickConfig.MODE_SINGLE_PICK;

    private ArrayList<String> imagesPath;


    public PicTipDialogView(@NonNull final Context context) {
        super(context);
        mContext = context;
        ((ViewGroup) getWindow().getDecorView()).addView(View.inflate(context, R.layout.view_pic_tip, null));

//        setContentView(R.layout.view_pic_tip);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.fl_picphoto, R.id.fl_camera, R.id.fl_systemphoto})
    void onClick(View view) {
        dismiss();

        switch (view.getId()) {
            case R.id.fl_picphoto:

                try {
                    imagesPath = PicPathUtils.getHeaderPicPath(getContext().getApplicationContext());

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }


                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setCompressionQuality(60);
                new PickConfig.Builder((Activity) mContext)
                        .actionBarcolor(Color.parseColor("#f08300"))
                        .statusBarcolor(Color.parseColor("#f08300"))
                        .isneedcamera(false)
                        .isSqureCrop(false)
                        .setUropOptions(options)
                        .maxPickSize(1)
                        .spanCount(4)
                        .setImagesPathFromAssets(imagesPath)
                        .isneedcrop(false)
                        .pickMode(mChosemode).build();
                break;
            case R.id.fl_camera:
                StartCamera.sendStarCamera((Activity) mContext);
                break;
            case R.id.fl_systemphoto:

                openAlbum();
                break;
        }


    }

    //   //权限没有问题的前提下 启动相册
    private void openAlbum() {
//        Intent intentAlbum = new Intent("android.intent.action.GET_CONTENT");
        Intent intentAlbum = new Intent(Intent.ACTION_PICK);
        intentAlbum.setType("image/*");
        ((Activity) mContext).startActivityForResult(intentAlbum, SYSTEM_ACTION_PICK_REQ);
    }

    public int getmChosemode() {
        return mChosemode;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            ArrayList<String> img = new ArrayList<>();
            String crop_path = resultUri.getPath();
//            isCropOver = true;
            if (crop_path != null && new File(crop_path) != null) {
                img.add(crop_path);

                onPicSel(crop_path);
            } else {
                Toast.makeText(mContext, "img file of ucrop", Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA && (getmChosemode() == PickConfig.MODE_SINGLE_PICK)) {
            if (currentfile != null && currentfile.exists() && currentfile.length() > 10) {
                StartCamera.sendStarCrop((Activity) mContext, currentfile.getAbsolutePath());

            } else {
                Toast.makeText(mContext, "img file of camera", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == PickConfig.PICK_REQUEST_CODE) {
            //在data中返回 选择的图片列表
            ArrayList<String> paths = data.getStringArrayListExtra("data");
            onPicSel(paths.get(0));

//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, paths);
//            listview.setAdapter(adapter);
        } else if (resultCode == RESULT_OK && requestCode == SYSTEM_ACTION_PICK_REQ) {
            if (data != null) {
                /**
                 * 判断手机版本，因为在4.4版本都手机处理图片返回的方法就不一样了
                 * 4.4以后返回的不是真实的uti而是一个封装过后的uri 所以要对封装过后的uri进行解析
                 */

                if (Build.VERSION.SDK_INT >= 19) {
                    //4.4系统一上用该方法解析返回图片
                    handleImageOnKitKat(data);
                } else {
                    //4.4一下用该方法解析图片的获取
                    handleImageBeforeKitKat(data);
                }

            }
        }
    }

    /**
     * api 19以后
     * 4.4版本后 调用系统相机返回的不在是真实的uri 而是经过封装过后的uri，
     * 所以要对其记性数据解析，然后在调用displayImage方法尽心显示
     *
     * @param data
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(mContext, uri)) {
            //如果是document类型的uri 则通过id进行解析处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("" +
                        "content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equals(uri.getScheme())) {
            //如果不是document类型的uri，则使用普通的方式处理
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        StartCamera.sendStarCrop((Activity) mContext, imagePath);

    }

    /**
     * 4.4版本一下 直接获取uri进行图片处理
     *
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    /**
     * 通过 uri seletion选择来获取图片的真实uri
     *
     * @param uri
     * @param seletion
     * @return
     */
    private String getImagePath(Uri uri, String seletion) {
        String path = null;
        Cursor cursor = mContext.getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void onPicSel(String crop_path) {
        ((IEventListener) mContext).setIvAddImage(crop_path);

    }
}
