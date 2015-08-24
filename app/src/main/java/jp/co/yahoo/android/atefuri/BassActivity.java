package jp.co.yahoo.android.atefuri;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BassActivity extends Activity implements SensorEventListener {

    private final static String TAG = "tomoktan";

    private Button playButton;
    private TextView currentPositionTextView;
    private TextView startTimeTextView;
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private AudioManager audioManager;
    private static TickHandler monitorHandler;
    private ScheduledExecutorService scheduledExecutorService;

    private SoundPool soundPool;
    private int soundId;

    private SoundPool tuneSoundPool;
    private int tuneSoundId;
    private long tuneMillis = 0;

    private int maxVolume;
    private int currentVolume = 0;

    private float x;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bass);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        this.playButton = (Button)findViewById(R.id.play);
        this.currentPositionTextView = (TextView)findViewById(R.id.current_position);
        this.startTimeTextView = (TextView)findViewById(R.id.start_time);

        this.startTimeTextView.setText("5秒から開始！！");

        this.mediaPlayer = MediaPlayer.create(this, R.raw.guitar_and_base);
        try{
            this.mediaPlayer.prepare();
            this.mediaPlayer.setLooping(true);
        }catch( Exception e ){}
        try{
            this.mediaPlayer.prepare();
            this.mediaPlayer.setLooping(true);
        }catch( Exception e ){}

        this.monitorHandler = new TickHandler(this.mediaPlayer, this.currentPositionTextView);

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        if(!isFinishing() && mediaPlayer.isPlaying()) {
                            monitorHandler.sendMessage(monitorHandler.obtainMessage());
                        }
                    }},
                200, //initialDelay
                200, //delay
                TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0) {
            Sensor sensor = sensorList.get(0);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }

        // 予め音声データを読み込む
        this.soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        this.soundId = this.soundPool.load(getApplicationContext(), R.raw.climax, 0);

        this.tuneSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        this.tuneSoundId = this.tuneSoundPool.load(getApplicationContext(), R.raw.bass_tune, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.monitorHandler.stop();
        this.monitorHandler=null;
        this.scheduledExecutorService = null;
        sensorManager.unregisterListener(this);
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    public void play(View view) {

        if (this.mediaPlayer.isPlaying()) {
            // this.mediaPlayer.pause();
            // this.playButton.setText("Play");
        } else {
            this.mediaPlayer.start();
            this.playButton.setText("Stop");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.x = event.values[0];
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if(this.x > 15 || this.x < -15) {

                if(!this.mediaPlayer.isPlaying()) {
                    if (System.currentTimeMillis() - this.tuneMillis > 4000) {
                        this.tuneSoundPool.play(this.tuneSoundId, 1.0F, 1.0F, 0, 0, 1.0F);
                        this.tuneMillis = System.currentTimeMillis();
                    }
                }

                count = 0; // 停止カウントをリセット
                if(this.currentVolume == 0) {
                    Log.i(TAG, "Play");
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (this.maxVolume), 0);
                    this.currentVolume = this.maxVolume;
                }
            } else if (this.x < 10 && this.x > -10) {
                count++;
                if (count >= 5 && this.currentVolume == this.maxVolume) { // 再生カウントが溜まったらミュート
                    Log.i(TAG, "Stop");
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    this.currentVolume = 0;
                    count = 0;
                }
            }
            Log.v(TAG, Float.toString(this.x));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
