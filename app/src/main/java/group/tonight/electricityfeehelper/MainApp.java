package group.tonight.electricityfeehelper;

import android.app.Application;

import com.facebook.stetho.Stetho;


import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import group.tonight.electricityfeehelper.crud.UserDatabase;

/**
 * Created by liyiwei on 2018/2/20.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        UserDatabase.init(this);

        CustomActivityOnCrash.install(this);
    }

    public static UserDatabase getDaoSession() {
        return UserDatabase.get();
    }
}
