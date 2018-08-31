package com.zcl.showphone.IFace;

import android.net.Uri;

public interface IEventListener {
    void setIvAddImage(String path);

    void setTimeTextView(String time,  String timeNum);
    void setModeTextView(String text, String mode);
    void setVoiceTextView(String name, Uri uri);
}
