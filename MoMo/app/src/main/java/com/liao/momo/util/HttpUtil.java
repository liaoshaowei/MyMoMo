package com.liao.momo.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/20.
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil-test";
    public static final int APPID = 25271;
    public static final String SHOWAPI_SIGN = "f6847ef7abea4edfa0321b5171d2980a";
    private static OkHttpClient okHttpClient;


    public static String getNewsChannel(){
        String json = getStringByOkHttp("https://route.showapi.com/109-34?showapi_appid="+APPID+"&showapi_sign="+SHOWAPI_SIGN);
        return json;
    }


    public static String getNewsInfo(String channelid,int page,Context context){
        String json = getStringCanCache(context,"https://route.showapi.com/109-35?channelId="+channelid+"&channelName=&maxResult=20&needAllList=0&needContent=0&needHtml=0&page="+page+ "&showapi_appid=" + APPID + "&title=&showapi_sign=" + SHOWAPI_SIGN);
        return json;
    }

    public static String getFunnyVideo(int page,Context context){
        String json = getStringCanCache(context,"https://route.showapi.com/255-1?page="+page+"&showapi_appid="+APPID+"&title=&type=41&showapi_sign="+SHOWAPI_SIGN);
        return json;
    }


    public static String getJokeText(int page,Context context){
        String json = getStringCanCache(context,"https://route.showapi.com/341-1?maxResult=15&page="+page+"&showapi_appid="+APPID+"&time=&showapi_sign="+SHOWAPI_SIGN);
        return json;
    }



    private static String getStringByOkHttp(String url) {
        String json = "";
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
        }
        Request request = new Request.Builder()
                .url(url)
                .tag("tag")
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                json = response.body().string();
            } else {
                json = "response not successful";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


    /**
     * 自动缓存数据到缓存文件中的OKHttp下载
     *
     * @param context 上下文
     * @param url     网址
     * @return Json数据
     */
    private static String getStringCanCache(Context context, String url) {
        String json = null;
        //做了修改，让okhttp单例
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new CacheInterceptor())
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .cache(new Cache(context.getExternalCacheDir(), 10 * 1024 * 1024))   //配置缓存路径及缓存空间
                    .build();
        }
        Request request = new Request.Builder()
                .url(url)
                .tag("Tag")
                .build();

        try {
            if (!NetworkUtil.isNetWorkAvailable(context)) {    //没有网络，从缓存中拿取数据
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    json = response.body().string();
                    Log.e(TAG, "getStringCanCache: 有没有？" + response.code());
                }

            } else {              //在有网络的情况下，联网更新数据
                request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    json = response.body().string();
                    Log.e(TAG, "getStringCanCache: 有网络？" + response.code());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            Response response1 = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "max-age=" + 3600 * 24 * 30)       //缓存24*30小时
                    .build();
            return response1;
        }
    }
}
