package group.tonight.electricityfeehelper;

import android.app.Application;

import com.facebook.stetho.Stetho;


import group.tonight.electricityfeehelper.crud.UserDatabase;
import group.tonight.electricityfeehelper.utils.CrashHandler;

/**
 * Created by liyiwei on 2018/2/20.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        UserDatabase.init(this);

        CrashHandler.getInstance().init(this);
    }

    public static UserDatabase getDaoSession() {
        return UserDatabase.get();
    }
}
