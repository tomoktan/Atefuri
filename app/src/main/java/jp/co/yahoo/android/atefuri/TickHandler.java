package jp.co.yahoo.android.atefuri;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

/**
 * Copyright (c) 2015 Yahoo! JAPAN Corporation. All rights reserved.
 */
public class TickHandler extends Handler {
    private boolean playFlag;
    private MediaPlayer mp;

    private TextView textView;

    public TickHandler(MediaPlayer mp, TextView textView) {
        super();
        this.mp = mp;
        this.textView = textView;
    }

    @Override
    public void handleMessage(Message msg) {
        Log.i("handleMessage", Boolean.toString(playFlag));
        if (mp != null) {
            if (mp.isPlaying()) {
                int duration = mp.getDuration();
                int currentPosition = mp.getCurrentPosition();

                String durationTime = timeText(duration);
                String currentTime = timeText(currentPosition);
                textView.setText(currentTime + "/" + durationTime);
            } else {
                textView.setText("00:00/00:00");
            }
            if (this != null) this.sleep(100);
        }

    }

    public void sleep(long delayMills) {
        removeMessages(0);
        sendMessageDelayed(obtainMessage(0), delayMills);
    }

    private String timeText(int time) {
        int m = (int)(time / 60000);
        int s = (int)(time % 60000 / 1000);

        String str = "00:00";
        if (m > 10 && s > 10) str = "" + m + ":" + s;
        else if (m < 10 && s >= 10) str = "0" + m + ":" + s;
        else if (m >= 10 && s < 10) str = "" + m + ":" + "0" + s;
        else if (m < 10 && s < 10) str = "0" + m + ":" + "0" + s;

        return str;
    }

    public void stop() {
        mp = null;
    }
}
