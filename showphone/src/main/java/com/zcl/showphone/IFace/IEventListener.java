package com.zcl.showphone.IFace;

import android.net.Uri;

public interface IEventListener {
    void setIvAddImage(Uri uri);

    void setTimeTextView(String time,  String timeNum);
    void setModeTextView(String text, String mode);
}
