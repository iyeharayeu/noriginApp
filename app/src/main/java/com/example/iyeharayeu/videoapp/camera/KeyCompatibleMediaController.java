package com.example.iyeharayeu.videoapp.camera;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;

public class KeyCompatibleMediaController extends MediaController {
    private static final int VIDEO_SCROLL_SHIFT_FORWARD = 15000;
    private static final int VIDEO_SCROLL_SHIFT_BACKWARD = 5000;
    private static final String TAG = KeyCompatibleMediaController.class.getSimpleName();

    private MediaController.MediaPlayerControl playerControl;

    public KeyCompatibleMediaController(Context context) {
        super(context);
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl playerControl) {
        super.setMediaPlayer(playerControl);
        this.playerControl = playerControl;
    }




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        Log.d(TAG, "dispatchKeyEvent() called with: " + "event = [" + event + "]");
        if (playerControl.canSeekForward() && (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                playerControl.seekTo(playerControl.getCurrentPosition() + VIDEO_SCROLL_SHIFT_FORWARD); // milliseconds
                show();
            }
            return true;
        } else if (playerControl.canSeekBackward() && (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                playerControl.seekTo(playerControl.getCurrentPosition() - VIDEO_SCROLL_SHIFT_BACKWARD); // milliseconds
                show();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
