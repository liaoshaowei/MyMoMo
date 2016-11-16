package com.liao.momo.feagment;

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
import com.liao.momo.util.HttpUtil;
import com.liao.momo.util.JsonUtil;
import com.liao.momo.util.ThreadUtil;
import com.liao.momo.adapter.JokeAdapter;
import com.liao.momo.model.JokeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-20.
 */
public class JokeFragment extends Fragment {

    private static final int MSG_JSON_JOKE = 50;
    private static final String TAG = "test";
    @Bind(R.id.ptrlv_joke)
    PullToRefreshListView ptrlvJoke;
    @Bind(R.id.tv)
    TextView tv;
    private View view;
    private boolean isPullUp = false;
    private int currentPage = 1;

    private JokeAdapter jokeAdapter;
    private List<JokeBean.ShowapiResBodyBean.ContentlistBean> joketlist = new ArrayList<>();
    private JokeBean jokeBean;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_JSON_JOKE:
                    try {
                        String json = (String) msg.obj;
                        jokeBean = JsonUtil.parseJokeBean(json);
                        if (jokeBean!=null){
                            if (!isPullUp){
                                joketlist.clear();
                                joketlist.addAll(jokeBean.getShowapi_res_body().getContentlist());
                            }else {
                                joketlist.addAll(jokeBean.getShowapi_res_body().getContentlist());
                            }
                            ptrlvJoke.onRefreshComplete();
                            tv.setVisibility(View.GONE);
                        }else {
                            ptrlvJoke.onRefreshComplete();
                            Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                            tv.setVisibility(View.VISIBLE);
                        }

                        if (jokeAdapter == null) {
                            jokeAdapter = new JokeAdapter(getActivity(), joketlist);
                            ptrlvJoke.setAdapter(jokeAdapter);
                        }
                        jokeAdapter.notifyDataSetChanged();
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
            view = inflater.inflate(R.layout.joke_fragment, container, false);
            ButterKnife.bind(this, view);

            getJson(1, false);

            onRefresh();
        }
        return view;
    }

    /**
     * 设置上拉下拉刷新
     */
    private void onRefresh() {
        ptrlvJoke.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout loadingLayoutProxy = ptrlvJoke.getLoadingLayoutProxy();
        loadingLayoutProxy.setPullLabel("拖动准备刷新");
        loadingLayoutProxy.setReleaseLabel("松开刷新");
        loadingLayoutProxy.setRefreshingLabel("正在努力刷新");
        loadingLayoutProxy.setLoadingDrawable(getResources().getDrawable(R.mipmap.pull_down_refresh));

        ptrlvJoke.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                    json = HttpUtil.getJokeText(page,getActivity());
                } else {
                    json = HttpUtil.getJokeText(1,getActivity());
                }
                Message msg = handler.obtainMessage();
                msg.what = MSG_JSON_JOKE;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
