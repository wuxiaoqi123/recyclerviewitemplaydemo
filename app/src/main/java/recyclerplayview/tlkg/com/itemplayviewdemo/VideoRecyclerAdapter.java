package recyclerplayview.tlkg.com.itemplayviewdemo;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaoqi on 2017/9/27.
 */

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder> {

    private List<VideoBean> mList;

    private OnClickPlayListener listener;

    public void setListener(OnClickPlayListener listener) {
        this.listener = listener;
    }

    public VideoRecyclerAdapter(List<VideoBean> list) {
        this.mList = list;
    }

    public void addVideoBean(VideoBean videoBean) {
        if (videoBean == null) return;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(videoBean);
        notifyDataSetChanged();
    }

    public void addAllVideoBean(List<VideoBean> list) {
        if (list == null) return;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final VideoBean videoBean = mList.get(position);
        Glide.with(holder.itemView.getContext()).load(videoBean.mImageId).crossFade().into(holder.mImageView);
        holder.mImageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPlayClick(holder.mCardView, videoBean.mVideoPath);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        FrameLayout mVideoRootFl;
        ImageView mImageView;
        ImageView mImageViewPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.item_cardview);
            mVideoRootFl = (FrameLayout) itemView.findViewById(R.id.item_video_root_fl);
            mImageView = (ImageView) itemView.findViewById(R.id.item_imageview);
            mImageViewPlay = (ImageView) itemView.findViewById(R.id.item_image_play);
        }
    }

    public interface OnClickPlayListener {
        void onPlayClick(View view, String videoPath);
    }
}
