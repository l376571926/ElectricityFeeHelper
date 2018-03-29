package group.tonight.electricityfeehelper;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.greendao.database.Database;

import group.tonight.electricityfeehelper.dao.DaoMaster;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.utils.CrashHandler;
import okhttp3.OkHttpClient;

/**
 * Created by liyiwei on 2018/2/20.
 */

public class MainApp extends Application {

    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "users-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

//        ZXingLibrary.initDisplayOpinion(this);
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();
            OkHttpUtils.initClient(okHttpClient);
        }

        CrashHandler.getInstance().init(this);
    }

    public static DaoSession getDaoSession() {
        daoSession.clear();
        return daoSession;
    }
}
