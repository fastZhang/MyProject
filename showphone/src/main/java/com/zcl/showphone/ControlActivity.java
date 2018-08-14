package com.zcl.showphone;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zcl.showphone.IFace.IEventListener;
import com.zcl.showphone.utils.PreferencesUtil;
import com.zcl.showphone.view.CallModeDialogView;
import com.zcl.showphone.view.CallTimeDialogView;
import com.zcl.showphone.view.PicTipDialogView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;

import static com.zcl.showphone.utils.Constant.ALARM_ID;
import static com.zcl.showphone.utils.Constant.SYSTEM_CONTACTS_REQ;
import static com.zcl.showphone.utils.Constant.SYSTEM_RING_REQ;
import static com.zcl.showphone.utils.Constant.SYSTEM_VOICE_REQ;

public class ControlActivity extends BaseActivity implements IEventListener {

    @BindView(R.id.ll_root)
    LinearLayout ll_root;

    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.et_num)
    EditText et_num;
    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_ring)
    TextView tv_ring;
    @BindView(R.id.tv_voice)
    TextView tv_voice;
    @BindView(R.id.tv_mode)
    TextView tv_mode;

    private PicTipDialogView mPicTipDialog;
    private CallTimeDialogView mCallTimeDialogView;
    private CallModeDialogView mCallModeDialogView;


    public static void startActivity(Activity owner) {
        Intent intent = new Intent(owner, ControlActivity.class);
        owner.startActivity(intent);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_control;
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.tv_start, R.id.fl_setting,
            R.id.iv_add, R.id.iv_contacts,
            R.id.cv_calltime, R.id.cv_callring,
            R.id.cv_callvolice, R.id.cv_callmode})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start:
                AndPermission.with(this).runtime().
                        permission(new String[]{Permission.READ_CALL_LOG, Permission.WRITE_CALL_LOG, Manifest.permission.WAKE_LOCK}).
                        onGranted(permission -> {

                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
                                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                getApplicationContext().startActivity(intent);
                            } else {
                                onClickSchedule();

                            }

                        }).
                        onDenied(permission -> {
                            AndPermission.with(this).runtime().setting().start();
                            Toast.makeText(this, this.getString(R.string.text_permission_call_log), Toast.LENGTH_LONG).show();
                        }).
                        start();
                break;
            case R.id.fl_setting:
                SettingActivity.startActivity(this);
                break;
            case R.id.iv_add:
                onToAddpic();

                break;

            case R.id.iv_contacts:
                onToContacts();

                break;
            case R.id.cv_calltime:
                if (null == mCallTimeDialogView) {
                    mCallTimeDialogView = new CallTimeDialogView(this);
                }
                mCallTimeDialogView.show();

                break;
            case R.id.cv_callring:

                toChooseRing();

                break;
            case R.id.cv_callvolice:

                toChooseVolice();

                break;
            case R.id.cv_callmode:
                if (null == mCallModeDialogView) {
                    mCallModeDialogView = new CallModeDialogView(this);
                }
                mCallModeDialogView.show();

                break;

        }
    }

    private void toChooseVolice() {


//        Intent intentAlbum = new Intent(Intent.ACTION_PICK);
//        intentAlbum.setType("image/*");
//
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");


//        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, SYSTEM_VOICE_REQ);

    }

    //选择铃声
    public void toChooseRing() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Setting Ring");
        Uri selUri = (Uri) tv_ring.getTag();
        if (null != selUri) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selUri);
        }
        startActivityForResult(intent, SYSTEM_RING_REQ);
    }


    private void onToAddpic() {
        AndPermission
                .with(this)
                .runtime()
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA)
                .onGranted(permissions -> {

                    if (null == mPicTipDialog) {
                        mPicTipDialog = new PicTipDialogView(this);
                    }
                    mPicTipDialog.show();

                })
                .onDenied(permissions -> {
                    AndPermission.with(this).runtime().setting().start();
                    Toast.makeText(this, this.getString(R.string.text_permission), Toast.LENGTH_LONG).show();
                }).start();
    }

    private void onToContacts() {
        AndPermission
                .with(this)
                .runtime()
                .permission(Permission.Group.CONTACTS)
                .onGranted(permissions -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, SYSTEM_CONTACTS_REQ);


                })
                .onDenied(permissions -> {
                    AndPermission.with(this).runtime().setting().start();
                    Toast.makeText(this, this.getString(R.string.text_permission_contacts), Toast.LENGTH_LONG).show();
                }).start();

    }

    @Override
    public void setIvAddImage(Uri uri) {
        iv_add.setImageURI(uri);
        iv_add.setTag(uri);
    }

    @Override
    public void setTimeTextView(String time, String timeNum) {
        if (!TextUtils.isEmpty(time)) {

            tv_time.setText(time);
            tv_time.setTag(timeNum);
        }

    }

    @Override
    public void setModeTextView(String text, String mode) {
        if (!TextUtils.isEmpty(text)) {
            tv_mode.setText(text);
            tv_mode.setTag(mode);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SYSTEM_CONTACTS_REQ) {
            parseContactsData(data);
        } else if (resultCode == RESULT_OK && requestCode == SYSTEM_RING_REQ) {
            parseRingData(data);

        } else if (resultCode == RESULT_OK && requestCode == SYSTEM_VOICE_REQ) {
            parseVoiceData(data);

        } else if (null != mPicTipDialog) {
            mPicTipDialog.onActivityResult(requestCode, resultCode, data);

        }


    }

    private void parseVoiceData(Intent data) {
        try {
            Uri pickedUri = data.getData();
            if (null == pickedUri)
                return;

            Cursor cursor = managedQuery(pickedUri, null, null, null, null);

            if (cursor.moveToFirst()) {
//                for (int i = 0; i < 6; i++)
                String name = cursor.getString(2);

                if (null != name && (name.contains(".mp3") || name.contains(".amr"))) {
                    Log.d("cursor.getString(i)", "parseringData: " + name);
                    String[] names = name.split("\\.");

                    tv_voice.setText(names[0]);
                    tv_voice.setTag(pickedUri);

                }
//                Log.d("cursor.getString(i)", "parseringData: " + );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseRingData(Intent data) {

        try {
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (null == pickedUri)
                return;

            Cursor cursor = managedQuery(pickedUri, null, null, null, null);

            if (cursor.moveToFirst()) {
//                for (int i = 0; i < 10; i++)
                tv_ring.setText(cursor.getString(8));
                tv_ring.setTag(pickedUri);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseContactsData(Intent data) {
        String name = "", phoneNumber = "";
        if (data == null) {
            return;
        }
        Uri contactData = data.getData();
        if (contactData == null) {
            return;
        }

//        这个问题其实很简单  估计大家在查询数据库的时候用的是这个函数
//        viedoCursor = context.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns,null, null, null);
//        只要换成这样就可以了：
//        ContentResolver cr = context.getContentResolver();
//        viedoCursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns,null, null, null);
//        我也是无意间发现有着两种写法的 然后试了一下问题就解决了

        Cursor cursor = managedQuery(contactData, null, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }

            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }

//            cursor.close();//managedQuery时不要close
        }
//        Log.i("info", "联系人：" + name + "--" + phoneNumber);
        et_num.setText(phoneNumber);
        et_name.setText(name);

    }

    private static final int HANA_UP_AFTER = 120;
    private static final int DURATION = 120;


    public void onClickSchedule() {

        String headPicUri = (iv_add.getTag() == null) ? null : iv_add.getTag().toString();
        String number = (et_num.getText() == null) ? null : et_num.getText().toString();
        String name = (et_name.getText() == null) ? getResources().getString(R.string.unknown) : et_name.getText().toString();

        String time = (tv_time.getTag() == null) ? "3" : tv_time.getTag().toString();
        String voiceUri = (tv_voice.getTag() == null) ? null : tv_voice.getTag().toString();
        String ringUri = (tv_ring.getTag() == null) ? null : tv_ring.getTag().toString();
        String mode = (tv_mode.getTag() == null) ? getString(R.string.call_mode_type) : tv_mode.getTag().toString();


        String hangUpAfter = Integer.toString(HANA_UP_AFTER);
        String duration = Integer.toString(DURATION);


        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Number can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, FakeRingerActivity.class);

        intent.putExtra("contactImage", headPicUri);
        intent.putExtra("number", number);
        intent.putExtra("name", name);

        intent.putExtra("voice", voiceUri);
        intent.putExtra("ringUri", ringUri);
        intent.putExtra("mode", mode);

        intent.putExtra("modeTimes", 1);


        intent.putExtra("duration", Integer.parseInt(duration));
        intent.putExtra("hangUpAfter", Integer.parseInt(hangUpAfter));


        PendingIntent pendingIntent = PendingIntent.getActivity(this, ALARM_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Integer.parseInt(time) * 1000, pendingIntent);
        Toast.makeText(this, "Fake call scheduled", Toast.LENGTH_SHORT).show();

//        finish();

    }

}