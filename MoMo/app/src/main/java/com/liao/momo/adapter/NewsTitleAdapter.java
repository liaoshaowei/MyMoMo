package com.liao.momo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liao.momo.R;
import com.liao.momo.model.NewsTitleBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-21.
 */
public class NewsTitleAdapter extends BaseAdapter {

    private static final String TAG = "test";
    private LayoutInflater inflater;

    Context context;
    List<NewsTitleBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> newsTtile;
    private NewsTitleBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean titlebean;

    public NewsTitleAdapter(Context context, List<NewsTitleBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> newsTitle) {
        this.context = context;
        this.newsTtile = newsTitle;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if (newsTtile != null) {
            return newsTtile.size();
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
            convertView = inflater.inflate(R.layout.item_newsinfo, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = ((Holder) convertView.getTag());
        }

        titlebean = newsTtile.get(position);
        String title = titlebean.getTitle();
        String time = titlebean.getPubDate();
        String channelName = titlebean.getChannelName();
        holder.tvNewsTitle.setText(title);
        holder.tvNewsTime.setText(time);
        holder.tvType.setText(channelName);

        holder.ivNewsImg.setImageResource(R.mipmap.fail_img);
        List<NewsTitleBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean.ImageurlsBean> imageurls = titlebean.getImageurls();
        if (imageurls.size() != 0) {
            String imgUrls = imageurls.get(0).getUrl();
            loadImg(imgUrls, holder);
        }else {
            holder.ivNewsImg.setVisibility(View.GONE);
        }


        holder.rlOnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        return convertView;
    }

    public void loadImg(String url, Holder holder) {
        Glide.with(context)
                .load(Uri.parse(url))
                .placeholder(R.mipmap.fail_img)
                .error(R.mipmap.fail_img)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.ivNewsImg);
        holder.ivNewsImg.setVisibility(View.VISIBLE);

    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class Holder {
        @Bind(R.id.ivNewsImg)
        ImageView ivNewsImg;
        @Bind(R.id.tvNewsTitle)
        TextView tvNewsTitle;
        @Bind(R.id.tvNewsTime)
        TextView tvNewsTime;
        @Bind(R.id.tvType)
        TextView tvType;
        @Bind(R.id.rlOnItem)
        RelativeLayout rlOnItem;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
