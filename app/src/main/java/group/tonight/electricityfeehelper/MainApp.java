package group.tonight.electricityfeehelper;

import android.app.Application;

import com.lzy.okgo.OkGo;

import org.greenrobot.greendao.database.Database;

import group.tonight.electricityfeehelper.dao.DaoMaster;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.utils.CrashHandler;

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

        OkGo.getInstance().init(this);
        CrashHandler.getInstance().init(this);
    }

    public static DaoSession getDaoSession() {
        daoSession.clear();
        return daoSession;
    }
}
