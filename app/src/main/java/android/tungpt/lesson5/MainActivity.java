package android.tungpt.lesson5;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int WHAT_SEEK_BAR = 2;
    public static final int REQUEST_CODE = 1;
    public static final int NOTIFICATION_ID = 1;
    public static final int MILLIS = 1000;
    public static final int SECOND_PER_MINUTE = 60;
    public static final String TITLE_NOTIFICATION = "Music Player";
    public static final String CONTENT_NOTIFICATION = "Play a Song";
    private MusicService mMusicService;
    private Button mButtonPlay;
    private Button mButtonNext;
    private Button mButtonPrev;
    private SeekBar mPositionSeekBar;
    private TextView mTextViewRemainTime;
    private TextView mTextViewElapsTime;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_SEEK_BAR) {
                int duration = mMusicService.getDuration();
                int current = mMusicService.getCurrentPosition();
                mPositionSeekBar.setMax(duration);
                mPositionSeekBar.setProgress(current);
                mTextViewRemainTime.setText(convertTime(duration));
                mTextViewElapsTime.setText(convertTime(current));
                sendEmptyMessageDelayed(WHAT_SEEK_BAR, MILLIS);
            }
            super.handleMessage(msg);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startService();
        buildNotification();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mMusicService = binder.getService();
            mHandler.sendEmptyMessageDelayed(WHAT_SEEK_BAR, MILLIS);
            getPositionSeekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(mServiceConnection);
        }
    };

    private void startService() {
        Intent startService = new Intent(this, MusicService.class);
        bindService(startService, mServiceConnection, BIND_AUTO_CREATE);
        startService(startService);
    }

    public void initView() {
        mButtonPlay = findViewById(R.id.button_play);
        mButtonNext = findViewById(R.id.button_next);
        mButtonPrev = findViewById(R.id.button_previous);
        mPositionSeekBar = findViewById(R.id.seek_bar_position);
        mTextViewRemainTime = findViewById(R.id.text_view_remaining_time);
        mTextViewElapsTime = findViewById(R.id.text_view_elapsed_time);
        mButtonPlay.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonPrev.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play:
                if (!mMusicService.isPlaying()) {
                    mMusicService.play();
                    mButtonPlay.setBackgroundResource(R.drawable.ic_pause);
                } else {
                    mMusicService.play();
                    mButtonPlay.setBackgroundResource(R.drawable.ic_play);
                }
                break;
            case R.id.button_next:
                mMusicService.next();
                mButtonPlay.setBackgroundResource(R.drawable.ic_pause);
                break;
            case R.id.button_previous:
                mMusicService.previous();
                mButtonPlay.setBackgroundResource(R.drawable.ic_pause);
                break;
            default:
                break;
        }
    }

    public String convertTime(int value) {
        int secondUnit = value / MILLIS;
        int minute = secondUnit / SECOND_PER_MINUTE;
        int second = secondUnit - minute * SECOND_PER_MINUTE;
        if (second < 10) {
            return (minute + ":0" + second);
        }
        return (minute + ":" + second);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildNotification() {
        Intent intentNotification = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getService(getApplicationContext(), REQUEST_CODE, intentNotification, 0);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.music_player_icon);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_play)
                .setLargeIcon(logo)
                .setContentTitle(TITLE_NOTIFICATION)
                .setContentText(CONTENT_NOTIFICATION)
                .setDeleteIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void getPositionSeekBar() {
        mPositionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    mMusicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
