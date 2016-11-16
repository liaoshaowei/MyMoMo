package com.liao.momo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liao.momo.R;
import com.liao.momo.model.ChannelBean;

import java.util.List;

/**
 * Created by xiaolin on 2015/1/24.
 */
public class GridViewAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private Context context;
    private List<ChannelBean.ShowapiResBodyBean.ChannelListBean> channelList;
    private int hidePosition = AdapterView.INVALID_POSITION;


    public GridViewAdapter(Context context, List<ChannelBean.ShowapiResBodyBean.ChannelListBean> channelList) {
        this.context = context;
        this.channelList = channelList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (channelList!=null) {
            int size = channelList.size();
            return size;
        }
        return 0;
    }

    @Override
    public String getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         Holder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_column, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        ChannelBean.ShowapiResBodyBean.ChannelListBean channelListBean = channelList.get(position);
        String name = channelListBean.getName();

        //hide时隐藏Text
        if(position != hidePosition) {
            holder.tv.setText(name);
        }
        else {
            holder.tv.setText("");
        }
        holder.tv.setId(position);

        return convertView;
    }

    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int pos) {
        channelList.remove(pos);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if(draggedPos < destPos) {
            channelList.add(destPos+1,channelList.get(draggedPos));
            channelList.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if(draggedPos > destPos) {
            channelList.add(destPos,channelList.get(draggedPos));
            channelList.remove(draggedPos+1);
        }

        hidePosition = destPos;
        notifyDataSetChanged();

    }






    class Holder{

        private  TextView tv;

        public Holder(View view) {
            tv = ((TextView) view.findViewById(R.id.tv));
        }
    }


}
