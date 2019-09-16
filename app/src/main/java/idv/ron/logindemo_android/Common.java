package idv.ron.logindemo_android;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {
    // 實機或模擬器
//	public static String URL = "http://192.168.196.185:8080/TextToJson_Web/";
    // 模擬器
    public final static String URL = "http://10.0.2.2:8080/LoginDemo/";

    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

}
