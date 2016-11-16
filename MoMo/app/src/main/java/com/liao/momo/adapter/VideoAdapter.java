package com.liao.momo.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liao.momo.R;
import com.liao.momo.model.VideoBean;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-27.
 */
public class VideoAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    Context context;

    List<VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> videoList;
    private VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean contentlistBean;

    private int currentPlayingPosition = -1;
    public final MediaPlayer mediaPlayer;


    public VideoAdapter(Context context, List<VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> videolist) {
        this.context = context;
        this.videoList = videolist;
        inflater = LayoutInflater.from(context);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int getCount() {
        if (videoList != null) {
            return videoList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_video, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = ((Holder) convertView.getTag());
        }

        contentlistBean = videoList.get(position);
        String text = contentlistBean.getText();
        String name = contentlistBean.getName();
        String createTime = contentlistBean.getCreate_time();
        String videoUri = contentlistBean.getVideo_uri();
        String profileImage = contentlistBean.getProfile_image();

        //显示基本信息
        holder.tvTitle.setText(text);
        holder.tvName.setText(name);
        holder.tvTime.setText(createTime);
        Glide.with(context).load(Uri.parse(profileImage))
                .override(4, 4)//以裁剪像素的方式实现背景的虚化
                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘缓存【原始图片】和【处理后的图片】
                .into(holder.ivBlur);
        Glide.with(context).load(Uri.parse(profileImage)).into(holder.ivImg);//显示封面

        //点击封面开始播放
        holder.ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPlayingPosition = position;
                notifyDataSetChanged();
            }
        });


        //控制播放与暂停
        holder.svVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }

            }
        });

        //播放谁就显示谁的surfaceView，否则显示封面
        if (position == currentPlayingPosition) {
            holder.ivImg.setVisibility(View.INVISIBLE);
            holder.ivPlay.setVisibility(View.INVISIBLE);
            holder.svVideo.setVisibility(View.VISIBLE);//将surfaceView先隐藏后显示，能够清除surfaceView的图像残留
            playVideo(videoUri, holder);
            if (!mediaPlayer.isPlaying()) {
                holder.svVideo.setAlpha(0);
            }
        } else {
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.ivImg.setVisibility(View.VISIBLE);
            holder.svVideo.setVisibility(View.INVISIBLE);
        }

        //播放谁就显示谁的surfaceView，否则显示封面
        Integer formerPosition = (Integer) holder.svVideo.getTag();
        if (formerPosition != null && position != currentPlayingPosition && formerPosition == currentPlayingPosition) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            currentPlayingPosition = -1;
        }

        holder.svVideo.setTag(position);
        return convertView;
    }

    private void playVideo(String videoUrl, final Holder holder) {
        try {
            mediaPlayer.reset();//重置mediaPlayer
            mediaPlayer.setDataSource(context, Uri.parse(videoUrl));//设置数据源
            mediaPlayer.setDisplay(holder.svVideo.getHolder());//将画面输出到surfaceView
            mediaPlayer.prepareAsync();//准备播放
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();//异步回调：准备好之后开始播放视频
                    holder.svVideo.setAlpha(1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    static class Holder {
        @Bind(R.id.iv_Blur)
        ImageView ivBlur;
        @Bind(R.id.sv_Video)
        SurfaceView svVideo;
        @Bind(R.id.iv_Img)
        ImageView ivImg;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.iv_play)
        ImageView ivPlay;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time)
        TextView tvTime;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
