package com.funny.call.prank.you.IFace;

import android.net.Uri;

public interface IEventListener {
    void setIvAddImage(String name, String path);

    void setTimeTextView(String time, String timeNum);

    void setModeTextView(String text, String mode);

    void setVoiceTextView(String name, Uri uri);

    void onStartCall();

    void setCallThemeText(String s);
}
