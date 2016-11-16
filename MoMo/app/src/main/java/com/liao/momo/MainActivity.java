package com.liao.momo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liao.momo.util.ScreenUtil;
import com.liao.momo.activity.WebActivity;
import com.liao.momo.feagment.JokeFragment;
import com.liao.momo.feagment.RecommendFragment;
import com.liao.momo.feagment.VideoFragment;
import com.liao.momo.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.vTopExtra)
    View vTopExtra;
    @Bind(R.id.flContainer)
    FrameLayout flContainer;
    @Bind(R.id.rbRecommend)
    RadioButton rbRecommend;
    @Bind(R.id.rbVideo)
    RadioButton rbVideo;
    @Bind(R.id.rbjoke)
    RadioButton rbjoke;
    @Bind(R.id.rg)
    RadioGroup rg;
    @Bind(R.id.tvmy)
    TextView tvmy;
    @Bind(R.id.nv_Menu)
    NavigationView nvMenu;
    @Bind(R.id.dl)
    DrawerLayout dl;
    private RecommendFragment recommendFragment;
    private VideoFragment videoFragment;
    private JokeFragment jokeFragment;

    private List<Fragment> fragmentList = new ArrayList<>();
    private View menu_head;
    private View menu_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //设置状态栏透明背景
        int statusBarHeight = ScreenUtil.getStatusBarHeight(getResources());
        findViewById(R.id.vTopExtra).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));

        initFragment();

        setMenuItem();

    }


    /**
     * 【我的】点击事件，点击打开抽屉
     * @param v
     */
    @OnClick(R.id.tvmy)
    public void onClick(View v) {
        dl.openDrawer(nvMenu);
    }

    private void setMenuItem() {

        //找到menu头像的控件
        View headerView = nvMenu.getHeaderView(0);
        menu_head = headerView.findViewById(R.id.ivHead);
        menu_name = headerView.findViewById(R.id.tvUserName);


        nvMenu.setItemIconTintList(null);//sh设置图标颜色为默认的颜色
        Resources resources = (Resources) getBaseContext().getResources();
        ColorStateList csl = resources.getColorStateList(R.color.navigation_menu_item_color);
        nvMenu.setItemTextColor(csl);
        //设置menu中Item的点击事件
        nvMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_my:
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_message:
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_sign:
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_QRcode:
                        readQRCode();
                        break;
                    case R.id.nav_attribute:
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_skin:
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_we:
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                }

                item.setChecked(true);
                dl.closeDrawers();
                return true;
            }
        });
    }

    /**
     * 设置控件与Fragment的联动
     */
    private void initFragment() {
        recommendFragment = new RecommendFragment();
        videoFragment = new VideoFragment();
        jokeFragment = new JokeFragment();

        fragmentList.add(recommendFragment);
        fragmentList.add(videoFragment);
        fragmentList.add(jokeFragment);

        showFragment(recommendFragment);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbRecommend:
                        showFragment(recommendFragment);
                        setTextColor(checkedId);
                        break;
                    case R.id.rbVideo:
                        showFragment(videoFragment);
                        setTextColor(checkedId);
                        break;
                    case R.id.rbjoke:
                        showFragment(jokeFragment);
                        setTextColor(checkedId);
                        break;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer,fragment);
//        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * 构造碎片管理器
     *
     * @param fragment
     */
    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            ft.add(R.id.flContainer, fragment);
        }
        for (Fragment f : fragmentList) {
            if (fragment == f) {
                ft.show(f);
            } else {
                ft.hide(f);
            }
        }
        ft.commit();
    }




    /**
     * 设置RadioButton文字的颜色
     * @param checkedId
     */
    private void setTextColor(int checkedId) {
        for (int i = 0; i < rg.getChildCount(); i++) {
            RadioButton childAt = ((RadioButton) rg.getChildAt(i));
            if (childAt.getId() == checkedId) {
                childAt.setTextColor(getResources().getColor(R.color.yellow));
            } else {
                childAt.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }



    /**
     * back键退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitTowClick();//调用双击的方法
        }
        return false;
    }

    private boolean isExit = false;

    /**
     * 双击则退出程序
     */
    private void exitTowClick() {
        Timer exit = null;
        if (isExit == false) {
            isExit = true;//准备退出
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exit = new Timer();
            exit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            }, 3000);//如果两秒钟内没点击第二次 ，则启动定时器取消刚才的执行任务
        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 启动扫描二维码
     */
    public void readQRCode() {
        startActivityForResult(new Intent(this, CaptureActivity.class), 100);
    }

    /**
     * 获得二维码数据
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            String result = intent.getExtras().getString("result");
            Intent webIntent = new Intent(this, WebActivity.class);
            webIntent.putExtra("info",result);
            startActivity(webIntent);
        }
    }
}
