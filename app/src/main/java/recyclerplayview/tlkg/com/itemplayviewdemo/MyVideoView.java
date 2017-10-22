package recyclerplayview.tlkg.com.itemplayviewdemo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

/**
 * Created by wuxiaoqi on 2017/9/27.
 */

public class MyVideoView extends RelativeLayout {
    private View rootLayout;

    private VideoView videoView;

    private View mClickView;

    private ImageView playOrPauseCenterIv;

    private LinearLayout playControlLl;

    private ImageView playOrPauseIv;

    private SeekBar seekBar;

    private ImageView fullIv;

    private static final int SHOW_CONTROL = 0x0001;

    private static final int HIDE_CONTROL = 0x0002;

    private static final int UPDATE_POSITION = 0x0003;

    private boolean isTrackingTouch = false;

    private boolean isShowControl = false;

    private boolean isPause = false;

    private boolean isFull = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CONTROL:
                    showControLl();
                    break;
                case HIDE_CONTROL:
                    hideControLl();
                    break;
                case UPDATE_POSITION:
                    updatePosition();
                    break;
                default:
                    break;
            }
        }
    };

    private IFullScreenListener listener;

    public void setListener(IFullScreenListener listener) {
        this.listener = listener;
    }

    public MyVideoView(Context context) {
        super(context);
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        rootLayout = LayoutInflater.from(getContext()).inflate(R.layout.video_view, this, true);
        mClickView = findViewById(R.id.my_video_click);
        videoView = (VideoView) findViewById(R.id.my_videoview);
        playOrPauseCenterIv = (ImageView) findViewById(R.id.my_video_center_playpause_iv);
        playControlLl = (LinearLayout) findViewById(R.id.my_video_play_control_ll);
        playOrPauseIv = (ImageView) findViewById(R.id.my_video_play_pause_iv);
        seekBar = (SeekBar) findViewById(R.id.my_video_seekbar);
        fullIv = (ImageView) findViewById(R.id.my_video_full_iv);
        initSetting();
        initEvent();
    }

    private void initSetting() {
        playOrPauseCenterIv.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
        showControLl();
        playOrPauseIv.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
        seekBar.setProgress(0);
        fullIv.setImageResource(R.drawable.ic_fullscreen_white_24dp);
        seekBar.setEnabled(false);
    }


    private void initEvent() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //TODO
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playOrPauseIv.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                playOrPauseCenterIv.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                seekBar.setMax(mp.getDuration());
                seekBar.setProgress(mp.getCurrentPosition());
                seekBar.setEnabled(true);
                handler.sendEmptyMessage(UPDATE_POSITION);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = true;
                handler.removeMessages(HIDE_CONTROL);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = false;
                handler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000);
                int position = seekBar.getProgress();
                if (videoView.isPlaying()) {
                    videoView.seekTo(position);
                }
            }
        });
        mClickView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowControl) {
                    hideControLl();
                } else {
                    showControLl();
                }
            }
        });
        playOrPauseIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    pause();
                } else {
                    resume();
                }
            }
        });
        playOrPauseCenterIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    pause();
                } else {
                    resume();
                }
            }
        });
        fullIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFull = !isFull;
                setFullScreen(isFull);
                if (listener != null) {
                    listener.onClickFull(isFull);
                }
            }
        });
    }


    /**
     * 更新进度
     */
    private void updatePosition() {
        handler.removeMessages(UPDATE_POSITION);
        if (videoView.isPlaying()) {
            int currentPosition = videoView.getCurrentPosition();
            if (!isTrackingTouch) {
                seekBar.setProgress(currentPosition);
            }
            handler.sendEmptyMessageDelayed(UPDATE_POSITION, 500);
        }
    }

    /**
     * 隐藏控制条
     */
    public void hideControLl() {
        isShowControl = false;
        handler.removeMessages(HIDE_CONTROL);
        playOrPauseCenterIv.setVisibility(View.GONE);
        playControlLl.clearAnimation();
        playControlLl.animate().translationY(playControlLl.getHeight()).setDuration(500).start();
    }


    /**
     * 显示控制条
     */
    public void showControLl() {
        isShowControl = true;
        handler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000);
        playOrPauseCenterIv.setVisibility(View.VISIBLE);
        playControlLl.clearAnimation();
        playControlLl.animate().translationY(0).setDuration(500).start();
    }

    /**
     * 设置播放地址
     *
     * @param path
     */
    public void setVideoPath(String path) {
        videoView.setVideoPath(path);
    }

    /**
     * 开始播放
     */
    public void start() {
        isPause = false;
        videoView.start();
        showControLl();
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return videoView.isPlaying();
    }

    /**
     * 暂停
     */
    public void pause() {
        isPause = true;
        handler.removeMessages(UPDATE_POSITION);
        videoView.pause();
        playOrPauseCenterIv.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
        playOrPauseIv.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
    }

    /**
     * 是否处于暂停状态
     *
     * @return
     */
    public boolean isPause() {
        return isPause;
    }

    /**
     * 继续
     */
    public void resume() {
        isPause = false;
        handler.sendEmptyMessageDelayed(UPDATE_POSITION, 500);
        videoView.start();
        playOrPauseCenterIv.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        playOrPauseIv.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
    }

    public int getPosition() {
        return videoView.getCurrentPosition();
    }

    public void seekTo(int position) {
        videoView.seekTo(position);
    }

    /**
     * 停止
     */
    public void stop() {
        initSetting();
        handler.removeCallbacksAndMessages(null);
        videoView.stopPlayback();
    }

    private void setFullScreen(boolean fullScreen) {
        if (getContext() != null && getContext() instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) getContext()).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
//            WindowManager.LayoutParams attrs = ((Activity) getContext()).getWindow().getAttributes();
//            if (fullScreen) {
//                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                ((Activity) getContext()).getWindow().setAttributes(attrs);
//                ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            } else {
//                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                ((Activity) getContext()).getWindow().setAttributes(attrs);
//                ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            }
        }
    }

    public interface IFullScreenListener {
        void onClickFull(boolean isFull);
    }
}
