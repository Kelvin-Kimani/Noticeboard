package com.noticeboard.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class AppUtils {


    private final Context myContext;

    public AppUtils(Context context) {
        myContext = context.getApplicationContext();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
