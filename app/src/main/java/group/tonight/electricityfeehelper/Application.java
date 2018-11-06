package group.tonight.electricityfeehelper;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.lzy.okgo.OkGo;


import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import okhttp3.OkHttpClient;

/**
 * Created by liyiwei on 2018/2/20.
 */

public class Application extends android.app.Application {
    public static final String BASE_HOST = "http://tonight.group:8080";

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        CustomActivityOnCrash.install(this);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        OkGo.getInstance()
                .setOkHttpClient(okHttpClient)
                .init(this);
    }

}
