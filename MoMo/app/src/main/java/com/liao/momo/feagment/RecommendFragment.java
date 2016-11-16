package com.liao.momo.feagment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.liao.momo.R;
import com.liao.momo.adapter.GridViewAdapter;
import com.liao.momo.feagment.news.NewsFragment;
import com.liao.momo.model.ChannelBean;
import com.liao.momo.myview.DragGridView;
import com.liao.momo.util.HttpUtil;
import com.liao.momo.util.JsonUtil;
import com.liao.momo.util.SharedPreferencesUtils;
import com.liao.momo.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-10-20.
 */
public class RecommendFragment extends Fragment {

    private static final String TAG = "test";
    private static final int MSG_JSOM_NEWS = 10;

    @Bind(R.id.tl_News)
    TabLayout tlNews;
    @Bind(R.id.vp_News)
    ViewPager vpNews;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.llRoot)
    LinearLayout llRoot;
    @Bind(R.id.lltaRood)
    LinearLayout lltaRood;
    private View view;

    private NewsFragment newsFragment;
    private List<Fragment> fragments = new ArrayList<>();
    private List<ChannelBean.ShowapiResBodyBean.ChannelListBean> channelList;
    private ChannelBean channelBean;
    private PopupWindow popupWindow;
    private boolean isAlter=false;

    Handler handler = new Handler() {

        String titleJson;

        /**
         * 获得JSON数据，并解析
         * 如JSON数据为空，则从私有目录拿取JSON数据
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_JSOM_NEWS:
                    titleJson = (String) msg.obj;
                    channelBean = JsonUtil.parseChannelBean(titleJson);
                    if (channelBean != null) {
                        channelList = channelBean.getShowapi_res_body().getChannelList();
                        initFragment();
                    } else {
                        Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };


    private String path;
    private GridViewAdapter gridViewAdapter;
    private DragGridView idDGV;
    private Button btok;
    private Button btno;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.recommend_fragment, container, false);
            ButterKnife.bind(this, view);
            getChannnelJson();

        }
        return view;
    }

    /**
     * 弹框选择频道
     */
    @OnClick(R.id.iv_add)
    public void onAddClick() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_view, null, false);
            popupWindow = new PopupWindow(contentView, screenWidth, ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            idDGV = (DragGridView) contentView.findViewById(R.id.idDGV);
             btok = ((Button) contentView.findViewById(R.id.bt_ok));
             btno = ((Button) contentView.findViewById(R.id.bt_no));

            if (gridViewAdapter == null) {
                gridViewAdapter = new GridViewAdapter(getActivity(), channelList);
                idDGV.setAdapter(gridViewAdapter);
            }


            idDGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    vpNews.setCurrentItem(position, true);
                    tlNews.setScrollPosition(position, 0, true);
                }
            });
        }

        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    fragments.clear();
                     initFragment();
                    onSharedChannelList();
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });


        btno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.showAsDropDown(lltaRood, 0, 0);
    }

    /**
     * 通过HttpUtil获得Json，并将数据保存到私有目录
     */
    private void getChannnelJson() {
        ThreadUtil.executeThread(new Runnable() {
            @Override
            public void run() {
                String json = HttpUtil.getNewsChannel();
                Message msg = handler.obtainMessage();
                msg.what = MSG_JSOM_NEWS;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }


    public void onSharedChannelList(){
        if (channelList!=null) {
            ThreadUtil.executeThread(new Runnable() {
                @Override
                public void run() {
                    String json = JSON.toJSONString(channelList);
                    SharedPreferencesUtils.saveSPData("channel",json,getActivity());
                }
            });

        }
    }

    //通过list集合构造Fragment
    private void initFragment() {
        for (int i = 0; i < channelList.size(); i++) {
            newsFragment = new NewsFragment();
            String channelid = channelList.get(i).getChannelId();
            String name = channelList.get(i).getName();
            Log.e(TAG, "initFragment: "+name);
            Bundle bundle = new Bundle();
            bundle.putString("channelid", channelid);
            newsFragment.setArguments(bundle);
            fragments.add(newsFragment);

        }
        vpNews.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String channel = channelList.get(position).getName();
                String channelName = channel.replaceAll("焦点", "");
                return channelName;
            }
        });
        tlNews.setupWithViewPager(vpNews);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
