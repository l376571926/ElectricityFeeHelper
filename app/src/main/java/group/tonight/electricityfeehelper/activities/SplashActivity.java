package group.tonight.electricityfeehelper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.tonight.electricityfeehelper.BR;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.crud.UserDao;
import group.tonight.electricityfeehelper.crud.UserDatabase;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.utils.ExcelParseTask;
import group.tonight.workbookhelper.WorkbookHelper;

public class SplashActivity extends AppCompatActivity {

    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_USER_DATA_1021 = "user_data_1021";
    private Handler mHandler = new Handler();
    private ProgressBar mInitDataPb;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        mInitDataPb = (ProgressBar) findViewById(R.id.init_data_pb);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = mPreferences.getBoolean(KEY_FIRST_LAUNCH, true);

        mViewDataBinding.setVariable(BR.firstLaunch, isFirstLaunch);

        if (isFirstLaunch) {
            mPreferences.edit()
                    .putBoolean(KEY_FIRST_LAUNCH, false)
                    .apply();
            ExcelParseTask excelParseTask = new ExcelParseTask(SplashActivity.this);
            excelParseTask.setOnProgressListener(new ExcelParseTask.OnProgressListener() {
                @Override
                public void onProgress(int total, int progress) {
                    if (total == progress) {
                        toHome();
                    } else {
                        mInitDataPb.setMax(total);
                        mInitDataPb.setProgress(progress);
                    }
                }
            });
            new Thread(excelParseTask).start();
        } else {
            if (!mPreferences.getBoolean(KEY_USER_DATA_1021, false)) {
                update1021Data();
            }
            toHome();
        }
    }

    private void toHome() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, 2000);
    }

    private void update1021Data() {
        WorkbookHelper helper = new WorkbookHelper(this);
        helper.setFileName("update_1021.xls");
        helper.setKeyArray(new String[]{
                "userId"
                , "userName"
                , "userPhone"
                , "powerMeterId"
                , "remarks"
        });
        helper.parse(new WorkbookHelper.OnParseResultListener() {
            @Override
            public void onError(Exception e) {
                KLog.e(e);
            }

            @Override
            public void onResult(String json) {
                KLog.e(json);

                Type type = new TypeToken<List<User>>() {
                }.getType();
                List<User> beanList = new Gson().fromJson(json, type);
                System.out.println();

                UserDao userDao = UserDatabase.get().getUserDao();
                for (User user : beanList) {
                    String userId = user.getUserId();
                    if (TextUtils.isEmpty(userId)) {
                        continue;
                    }
                    String userName = user.getUserName();
                    String userPhone = user.getUserPhone();
                    String powerMeterId = user.getPowerMeterId();
                    String remarks = user.getRemarks();

                    User oldUser = userDao.loadUser(userId);
                    if (oldUser == null) {
//                        KLog.e("添加新用户：" + user.toString());
                        userDao.insert(user);
                    } else {
//                        KLog.e("更新已有用户：" + oldUser.toString() + " " + user.toString());
                        String oldUserPhone = oldUser.getUserPhone();
                        if (userId.equals("9503247178")) {
                            System.out.println();
                        }
                        if (!TextUtils.isEmpty(userPhone) && isNumeric(userPhone) && !userPhone.equals(oldUserPhone)) {
                            KLog.e("更新电话：" + userId + " " + user.getUserName() + " " + oldUserPhone + "-->" + userPhone);
                            oldUser.setUserPhone(userPhone);
                        }
                        if (!TextUtils.isEmpty(remarks)) {
                            oldUser.setRemarks(remarks);
                        }
                        userDao.update(oldUser);
                    }
                }
                if (mPreferences != null) {
                    mPreferences.edit()
                            .putBoolean(KEY_USER_DATA_1021, true)
                            .apply();
                }
            }
        });
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}