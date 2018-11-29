package android.tungpt.lesson5;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
    public static int sSongIndex = 0;
    public static final int[] SONG_LISTS = {R.raw.yeu_cover_chau_duong,
            R.raw.pho_dinhmanhninh,
            R.raw.mua_xa_nhau_emily,
            R.raw.mash_up_lo_duyen_rum,
            R.raw.gui_hoaminzy};
    public static final String[] SONG_NAME = {"Yêu - Châu Dương",
            "Phố - Đinh Mạnh Ninh",
            "Mùa Xa Nhau - Emily",
            "Mashup Lỡ Duyên - Rum ft NIT",
            "Gửi - Hòa Minzy"};
    private MediaPlayer mMediaPlayer;
    private IBinder mIBinder = new MusicBinder();
    private int mCurrentPosition;
    private int mDuration;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaPlayer = MediaPlayer.create(this, SONG_LISTS[startId]);
        mMediaPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.stop();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void playSong(int id) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
        mMediaPlayer = MediaPlayer.create(this, SONG_LISTS[id]);
        mMediaPlayer.start();
    }

    public void play() {
        if (mMediaPlayer.isPlaying()) {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
            }
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
        }
    }

    public void next() {
        if (sSongIndex < (SONG_LISTS.length - 1)) {
            playSong(sSongIndex + 1);
            sSongIndex++;
        } else {
            playSong(0);
            sSongIndex = 0;
        }
    }

    public void previous() {
        if (sSongIndex > 0) {
            playSong(sSongIndex - 1);
            sSongIndex--;
        } else {
            //play last song
            playSong(SONG_LISTS.length - 1);
            sSongIndex = SONG_LISTS.length - 1;
        }
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
        }
        return mCurrentPosition;
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            mDuration = mMediaPlayer.getDuration();
        }
        return mDuration;
    }

    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }
}
