package com.liao.momo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liao.momo.R;
import com.liao.momo.model.JokeBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-29.
 */
public class JokeAdapter extends BaseAdapter {
    private static final String TAG = "test";
    private final LayoutInflater inflater;
    Context context;
    List<JokeBean.ShowapiResBodyBean.ContentlistBean> joketlist;
    private JokeBean.ShowapiResBodyBean.ContentlistBean jokeinfo;

    public JokeAdapter(Context context, List<JokeBean.ShowapiResBodyBean.ContentlistBean> joketlist) {
        this.context = context;
        this.joketlist = joketlist;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (joketlist != null) {
            int size = joketlist.size();
            return size;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_joke, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = ((Holder) convertView.getTag());
        }

        jokeinfo = joketlist.get(position);
        String title = jokeinfo.getTitle();
        String str = jokeinfo.getText();
        String s = str.replace("</p>", "");
        String text = s.replace("<p>", "");
        String ct = jokeinfo.getCt();


        holder.tvTitle.setText(title);
        holder.tvInfo.setText(text);
        holder.tvTime.setText(ct);

        return convertView;
    }

    static class Holder {
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_info)
        TextView tvInfo;
        @Bind(R.id.tv_time)
        TextView tvTime;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
