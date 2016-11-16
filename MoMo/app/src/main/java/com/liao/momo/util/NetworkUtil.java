package com.liao.momo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * 判断是否联网
 * Created by qf on 2016/11/1.
 */
public class NetworkUtil {

    public static final boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = cm.getAllNetworks();
                for (Network network : networks) {
                    NetworkInfo networkInfo = cm.getNetworkInfo(network);
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED ){
                        return true;
                    }
                }
            }else {
                NetworkInfo[] info = cm.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            }
        return false;
    }

}
