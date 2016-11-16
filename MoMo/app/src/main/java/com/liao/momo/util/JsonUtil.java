package com.liao.momo.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.liao.momo.model.ChannelBean;
import com.liao.momo.model.JokeBean;
import com.liao.momo.model.NewsTitleBean;
import com.liao.momo.model.VideoBean;

/**
 * Created by Administrator on 2016-10-20.
 */
public class JsonUtil {

    /**
     * 解析频道
     * @param json
     * @return NewsBean
     */

    public static ChannelBean parseChannelBean(String json){
        ChannelBean channelBean = null;
        try {
            channelBean = new Gson().fromJson(json, ChannelBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return channelBean;
    }


    public static NewsTitleBean parseNewsTitleBean(String json){
        NewsTitleBean newsTitleBean = null;
        try {
            newsTitleBean = new Gson().fromJson(json, NewsTitleBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return newsTitleBean;
    }

    public static VideoBean parseVideoBean(String json){

        VideoBean videoBean = null;
        try {
            videoBean = new Gson().fromJson(json, VideoBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return videoBean;
    }


    public static JokeBean parseJokeBean(String json){

        JokeBean jokeBean = null;
        try {
            jokeBean = new Gson().fromJson(json, JokeBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return jokeBean;
    }
}
