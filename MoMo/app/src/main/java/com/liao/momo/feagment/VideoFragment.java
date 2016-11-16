package com.liao.momo.feagment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.liao.momo.R;
import com.liao.momo.util.HttpUtil;
import com.liao.momo.util.JsonUtil;
import com.liao.momo.util.ThreadUtil;
import com.liao.momo.adapter.VideoAdapter;
import com.liao.momo.model.VideoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-20.
 */
public class VideoFragment extends Fragment {

    private static final int MSG_JSON_VIDEO = 30;
    private static final String TAG = "test";
    @Bind(R.id.ptrlv_Video)
    PullToRefreshListView ptrlvVideo;
    @Bind(R.id.tv)
    TextView tv;
    private View view;
    private boolean isPullUp = false;
    private int currentPage = 1;
    private VideoAdapter videoAdapter;

    List<VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> videolist = new ArrayList<>();

    Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_JSON_VIDEO:
                    try {
                        String json = (String) msg.obj;
                        videoBean = JsonUtil.parseVideoBean(json);
                        if (videoBean!=null){
                            if (!isPullUp){
                                videolist.clear();
                                videolist.addAll(videoBean.getShowapi_res_body().getPagebean().getContentlist());
                            }else {
                                videolist.addAll(videoBean.getShowapi_res_body().getPagebean().getContentlist());
                            }

                            ptrlvVideo.onRefreshComplete();
                            tv.setVisibility(View.GONE);
                        }else {
                            ptrlvVideo.onRefreshComplete();
                            Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                            tv.setVisibility(View.VISIBLE);
                        }

                        if (videoAdapter == null) {
                            videoAdapter = new VideoAdapter(getActivity(), videolist);
                            ptrlvVideo.setAdapter(videoAdapter);
                        }
                        videoAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        }
    };
    private VideoBean videoBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.video_fragment, container, false);
            ButterKnife.bind(this, view);

            getJson(1, false);
            onRefresh();
            Log.e(TAG, "onCreateView: ");
        }
        return view;
    }

    /**
     * 设置上拉下拉刷新
     */
    private void onRefresh() {
        ptrlvVideo.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout loadingLayoutProxy = ptrlvVideo.getLoadingLayoutProxy();
        loadingLayoutProxy.setPullLabel("拖动准备刷新");
        loadingLayoutProxy.setReleaseLabel("松开刷新");
        loadingLayoutProxy.setRefreshingLabel("正在努力刷新");
        loadingLayoutProxy.setLoadingDrawable(getResources().getDrawable(R.mipmap.pull_down_refresh));

        ptrlvVideo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            /**
             *设置上拉刷新
             * @param refreshView
             */
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isPullUp = false;
                        getJson(1, false);
                    }
                }, 3000);
            }

            /**
             * 设置下拉刷新
             * @param refreshView
             */
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isPullUp = true;
                        getJson(++currentPage, true);
                    }
                }, 3000);
            }
        });
    }

    /**
     * 通过Http获得json数据，并通过handler来处理数据
     * @param page
     * @param append
     */
    private void getJson(final int page, final boolean append) {
        ThreadUtil.executeThread(new Runnable() {
            @Override
            public void run() {
                String json;
                if (append) {
                    json = HttpUtil.getFunnyVideo(page,getActivity());
                } else {
                    json = HttpUtil.getFunnyVideo(1,getActivity());
                }
                Message msg = handler.obtainMessage();
                msg.what = MSG_JSON_VIDEO;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden==true){
            try {
                if (videoAdapter.mediaPlayer.isPlaying()==true){
                    videoAdapter.mediaPlayer.pause();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");
        ButterKnife.unbind(this);
    }
}
