package recyclerplayview.tlkg.com.itemplayviewdemo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recyclerView;

    private VideoRecyclerAdapter mAdapter;

    private FrameLayout videoRootViewFl;

    private MyVideoView videoView;

    private FrameLayout fullScreen;

    private View lastView;

    private int videoPosition = -1;

    private List<VideoBean> videoBeanList = new ArrayList<>();

    private int[] imageIds = new int[]{R.drawable.hzw_a, R.drawable.hzw_b,
            R.drawable.hzw_d, R.drawable.hzw_e, R.drawable.hzw_f, R.drawable.hzw_h,
            R.drawable.hzw_i, R.drawable.hzw_j, R.drawable.hzw_k};

    private static String VIDEO_PATH = "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initEvent();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setTitle("");
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        videoRootViewFl = (FrameLayout) findViewById(R.id.video_root_fl);
        fullScreen = (FrameLayout) findViewById(R.id.video_full_screen);
        mAdapter = new VideoRecyclerAdapter(videoBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    private void showVideo(View view, final String videoPath) {
        View v;
        removeVideoView();
        if (videoRootViewFl.getVisibility() == View.VISIBLE) {
            videoRootViewFl.removeAllViews();
            videoRootViewFl.setVisibility(View.GONE);
        }
        if (videoView == null) {
            videoView = new MyVideoView(MainActivity.this);
//            videoView.setListener(new MyVideoView.IFullScreenListener() {
//                @Override
//                public void onClickFull(boolean isFull) {
//                    if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
//                        fullScreen.setVisibility(View.VISIBLE);
//                        removeVideoView();
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                        fullScreen.addView(videoView, new ViewGroup.LayoutParams(-1, -1));
//                        videoView.setVideoPath(VIDEO_PATH);
//                        videoView.start();
//                    } else {
//                        fullScreen.removeAllViews();
//                        fullScreen.setVisibility(View.GONE);
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        if (lastView instanceof ViewGroup) {
//                            ((ViewGroup) lastView).addView(videoView);
//                        }
//                        videoView.setVideoPath(VIDEO_PATH);
//                        videoView.start();
//                    }
//
//                }
//            })
        }
        videoView.stop();
        v = view.findViewById(R.id.item_imageview);
        if (v != null) v.setVisibility(View.INVISIBLE);
        v = view.findViewById(R.id.item_image_play);
        if (v != null) v.setVisibility(View.INVISIBLE);
        v = view.findViewById(R.id.item_video_root_fl);
        if (v != null) {
            v.setVisibility(View.VISIBLE);
            FrameLayout fl = (FrameLayout) v;
            fl.removeAllViews();
            fl.addView(videoView, new ViewGroup.LayoutParams(-1, -1));
            VIDEO_PATH = videoPath;
            videoView.setVideoPath(videoPath);
            videoView.start();
        }
        lastView = view;
    }

    private void removeVideoView() {
        View v;
        if (lastView != null) {
            v = lastView.findViewById(R.id.item_imageview);
            if (v != null) v.setVisibility(View.VISIBLE);
            v = lastView.findViewById(R.id.item_image_play);
            if (v != null) v.setVisibility(View.VISIBLE);
            v = lastView.findViewById(R.id.item_video_root_fl);
            if (v != null) {
                FrameLayout ll = (FrameLayout) v;
                ll.removeAllViews();
                v.setVisibility(View.GONE);
            }
        }
    }

    private void initData() {
        VideoBean videoBean;
        for (int i = 0; i < 100; i++) {
            videoBean = new VideoBean(imageIds[i % imageIds.length], VIDEO_PATH);
            videoBeanList.add(videoBean);
        }
    }

    private void initEvent() {
        mAdapter.setListener(new VideoRecyclerAdapter.OnClickPlayListener() {
            @Override
            public void onPlayClick(View view, String videoPath) {
                showVideo(view, videoPath);
            }
        });
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (videoPosition == -1 || videoRootViewFl.getVisibility() != View.VISIBLE) {
                    return;
                }
                if (videoPosition == recyclerView.getChildAdapterPosition(view)) {
                    videoPosition = -1;
                    showVideo(view, VIDEO_PATH);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (videoView == null || videoRootViewFl.getVisibility() == View.VISIBLE) return;
                View v = view.findViewById(R.id.item_video_root_fl);
                if (v != null) {
                    FrameLayout fl = (FrameLayout) v;
                    videoPosition = recyclerView.getChildAdapterPosition(view);
                    if (fl.getChildCount() > 0) {
                        fl.removeAllViews();
                        int position = 0;
                        if (videoView.isPlaying()) {
                            position = videoView.getPosition();
                            videoView.stop();
                        }
                        videoRootViewFl.setVisibility(View.VISIBLE);
                        videoRootViewFl.removeAllViews();
                        lastView = videoRootViewFl;
                        videoRootViewFl.addView(videoView, new ViewGroup.LayoutParams(-1, -1));
                        videoView.setVideoPath(VIDEO_PATH);
                        videoView.start();
                        videoView.seekTo(position);
//                        if (videoView.isPause()) {
//                            videoView.resume();
//                        }
                    }
                    fl.setVisibility(View.GONE);
                }
                v = view.findViewById(R.id.item_imageview);
                if (v != null) {
                    if (v.getVisibility() != View.VISIBLE) {
                        v.setVisibility(View.VISIBLE);
                    }
                }
                v = view.findViewById(R.id.item_image_play);
                if (v != null) {
                    if (v.getVisibility() != View.VISIBLE) {
                        v.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (videoView != null) {
            videoView.stop();
        }
        super.onDestroy();
    }
}
