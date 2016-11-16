package com.liao.momo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liao.momo.R;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2016-10-22.
 */
public class WebActivity extends AppCompatActivity {

    @Bind(R.id.webview)
    WebView webview;
    @Bind(R.id.pbProgressBar)
    ProgressBar pbProgressBar;
    @Bind(R.id.iv_Back)
    ImageView ivBack;
    @Bind(R.id.iv_Share)
    ImageView ivShare;
    @Bind(R.id.tv_text)
    TextView tvText;
    private boolean isOnPause;
    private String Url;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        String webUrl = getIntent().getStringExtra("webUrl");
        title = getIntent().getStringExtra("title");
        String UrlString = getIntent().getStringExtra("info");

        if (webUrl != null) {
            Url = webUrl;
        }

        if (UrlString != null) {
            try {
                URL QRUrl = new URL(UrlString);
                Url = UrlString;
            } catch (Exception e) {
                webview.setVisibility(View.GONE);
                tvText.setText(UrlString);
            }
        }
        if (Url != null) {
            showHtml();
        }
    }

    private void showHtml() {
        WebSettings settings = webview.getSettings();
//        settings.setPluginState(WebSettings.PluginState.ON); //设置webview的插件转状态
        settings.setAppCacheEnabled(true);//告诉webview启用应用程序缓存api
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//webview的缓存模式
        settings.setDomStorageEnabled(true);//开启DOM  DOM storage API 功能
        settings.setJavaScriptEnabled(true);//支持js
        settings.setSupportZoom(true);//自动缩放
        settings.setBuiltInZoomControls(true);//显示放大缩小按钮
        settings.setUseWideViewPort(true);//支持双击放大缩小
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");//设置默认的字符编码
        webview.setWebViewClient(new WebViewClient() {

            /**
             * 网页开始加载
             * @param view
             * @param url
             * @param favicon
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("网页开始加载");
                pbProgressBar.setVisibility(View.VISIBLE);
            }

            /**
             * 网页加载结束
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("网页加载结束");
                pbProgressBar.setVisibility(View.GONE);
            }

            /**
             * WebViewClient很多方法可覆盖，大多数用不到，必须要覆盖shouldOverrideUrlLoading（WebView, String）默认方法。
             * 当有新的URL加载到WebView时，譬如说点击某个链接，该方法会决定下一步行动。如返回true，表示WebView不要处理URL，
             * 如返回false，表示叫WebView，去加载这个URL。
             * @param view
             * @return
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

        });

        webview.setWebChromeClient(new WebChromeClient() {

            /**
             * 网页加载进度改变
             * @param view
             * @param newProgress
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                System.out.println("加载进度：" + newProgress);
                super.onProgressChanged(view, newProgress);
                pbProgressBar.setProgress(newProgress);
            }

            /**
             * 网页title
             * @param view
             * @param title
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("网页Title" + title);
                super.onReceivedTitle(view, title);
            }
        });
        webview.loadUrl(Url);
    }

    /**
     * 回退键监听
     */
    @OnClick(R.id.iv_Back)
    public void onBackClick() {
        finish();
    }

    /**
     * 分享监听
     */
    @OnClick(R.id.iv_Share)
    public void onShareOclick() {
        showShare();
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title);

        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(Url);

        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(Url);

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://news.sina.com.cn");

        // 启动分享GUI
        oks.show(this);
    }
}
