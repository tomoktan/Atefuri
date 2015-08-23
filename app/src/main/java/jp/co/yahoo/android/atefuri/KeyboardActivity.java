package jp.co.yahoo.android.atefuri;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.MotionEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class KeyboardActivity extends Activity {

    private final static String TAG = "tomoktan";

    private Button playButton;
    private TextView currentPositionTextView;
    private TextView startTimeTextView;
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private AudioManager audioManager;
    private TickHandler monitorHandler;
    private ScheduledExecutorService scheduledExecutorService;

    private int maxVolume;
    private int currentVolume = 0;
    private long touchUpTime;
    private boolean tapFlag = false;

    private float x;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        this.playButton = (Button)findViewById(R.id.play);
        this.currentPositionTextView = (TextView)findViewById(R.id.current_position);
        this.startTimeTextView = (TextView)findViewById(R.id.start_time);

        this.startTimeTextView.setText("16秒から開始！！");

        this.mediaPlayer = MediaPlayer.create(this, R.raw.keyboard);
        try{
            this.mediaPlayer.prepare();
            this.mediaPlayer.setLooping(true);
        }catch( Exception e ){

        }
        try{
            this.mediaPlayer.prepare();
            this.mediaPlayer.setLooping(true);
        }catch( Exception e ){}

        this.monitorHandler = new TickHandler(this.mediaPlayer, this.currentPositionTextView);

        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        if(!isFinishing() && mediaPlayer.isPlaying()) {
                            long time = System.currentTimeMillis();

                            if (tapFlag == true && (time - touchUpTime) > 1000) {
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                tapFlag = false;
                            }
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.monitorHandler.stop();
        this.monitorHandler=null;
        this.scheduledExecutorService.shutdown();
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    public void play(View view) {

        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.playButton.setText("Play");
        } else {
            this.mediaPlayer.start();
            this.playButton.setText("Stop");
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("pressure", "hoge=" + event.getPressure());

        if (mediaPlayer.isPlaying()) {
            if (tapFlag == false) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (this.maxVolume), 0);
                tapFlag = true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {

                Log.d("TouchEventAction", "ac:" + event.getAction());
                Log.d("TouchDownTime", "dt:" + event.getDownTime());
                Log.d("TouchEventTime", "et" + event.getEventTime());

                touchUpTime = System.currentTimeMillis();
            }
        }
        return true;
    }
}
