package jp.co.yahoo.android.atefuri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final static String TAG = "tomoktan";

    private final static Class GUITAR_MODE = GuitarActivity.class;
    private final static Class KEYBOARD_MODE = KeyboardActivity.class;
    private final static Class DRUM_MODE = DrumActivity.class;
    private final static Class BASS_MODE = BassActivity.class;

    private Class mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void selectMode(View view) {
        switch (view.getId()) {
            case R.id.guitar_mode:
                this.mode = GUITAR_MODE;
                break;
            case R.id.keyboard_mode:
                this.mode = KEYBOARD_MODE;
                break;
            case R.id.drum_mode:
                this.mode = DRUM_MODE;
                break;
            case R.id.bass_mode:
                this.mode = BASS_MODE;
                break;
            default:
                break;
        }
        Intent intent = new Intent(this, this.mode);
        startActivity(intent);
    }
}