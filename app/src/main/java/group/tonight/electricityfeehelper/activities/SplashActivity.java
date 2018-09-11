package group.tonight.electricityfeehelper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import group.tonight.electricityfeehelper.BR;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.utils.ExcelParseTask;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private ProgressBar mInitDataPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        mInitDataPb = (ProgressBar) findViewById(R.id.init_data_pb);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = preferences.getBoolean("first_launch", true);

        mViewDataBinding.setVariable(BR.firstLaunch, isFirstLaunch);

        if (isFirstLaunch) {
            preferences.edit()
                    .putBoolean("first_launch", false)
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


}