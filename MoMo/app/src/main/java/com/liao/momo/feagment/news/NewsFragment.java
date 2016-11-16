package com.liao.momo.feagment.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.liao.momo.activity.WebActivity;
import com.liao.momo.adapter.NewsTitleAdapter;
import com.liao.momo.model.NewsTitleBean;
import com.liao.momo.util.HttpUtil;
import com.liao.momo.util.JsonUtil;
import com.liao.momo.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-24.
 */
public class NewsFragment extends Fragment {

    private static final int MSG_JSOM_NEWS = 10;
    private static final String TAG = "test";


    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.ptrlv_NewsInfo)
    PullToRefreshListView ptrlvNewsInfo;


    private View view;
    private String channelid;
    private NewsTitleAdapter newsTitleAdapter;
    private List<NewsTitleBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> newsTitle = new ArrayList<>();
    private NewsTitleBean newsTitleBean;
    private boolean isPullUp = false;

    /**
     * 获得Json并解析获得新闻数据集合
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_JSOM_NEWS:
                    try {
                        String json = (String) msg.obj;
                        newsTitleBean = JsonUtil.parseNewsTitleBean(json);
                        if (newsTitleBean != null) {
                            if (!isPullUp) {
                                newsTitle.clear();
                                newsTitle.addAll(newsTitleBean.getShowapi_res_body().getPagebean().getContentlist());
                            } else {
                                newsTitle.addAll(newsTitleBean.getShowapi_res_body().getPagebean().getContentlist());
                            }
                            ptrlvNewsInfo.onRefreshComplete();
                            tv.setVisibility(View.GONE);
                        } else {

                            ptrlvNewsInfo.onRefreshComplete();
                            Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                            tv.setVisibility(View.VISIBLE);
                        }

                        if (newsTitleAdapter == null) {
                            newsTitleAdapter = new NewsTitleAdapter(getActivity(), newsTitle);
                            ptrlvNewsInfo.setAdapter(newsTitleAdapter);
                        }
                        newsTitleAdapter.notifyDataSetChanged();
                        ClickItem();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.news_fragment, container, false);
            ButterKnife.bind(this, view);
            Bundle bundle = getArguments();
            channelid = bundle.getString("channelid");
            getJson(1, false);
            onRefresh();
        }
        return view;
    }


    private int currentPage = 1;

    /**
     * 设置上拉下拉刷新
     */
    private void onRefresh() {
        ptrlvNewsInfo.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout loadingLayoutProxy = ptrlvNewsInfo.getLoadingLayoutProxy();
        loadingLayoutProxy.setPullLabel("拖动准备刷新");
        loadingLayoutProxy.setReleaseLabel("松开刷新");
        loadingLayoutProxy.setRefreshingLabel("正在努力刷新");
        loadingLayoutProxy.setLoadingDrawable(getResources().getDrawable(R.mipmap.pull_down_refresh));

        ptrlvNewsInfo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                    json = HttpUtil.getNewsInfo(channelid, page,getActivity());
                } else {
                    json = HttpUtil.getNewsInfo(channelid, 1,getActivity());
                }
                Message msg = handler.obtainMessage();
                msg.what = MSG_JSOM_NEWS;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * item的点击监听
     * 通过点击Item跳转WebActivity
     */
    private void ClickItem() {
        newsTitleAdapter.setOnItemClickListener(new NewsTitleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String url = newsTitle.get(position).getLink();
                String title = newsTitle.get(position).getTitle();
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("webUrl", url);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
