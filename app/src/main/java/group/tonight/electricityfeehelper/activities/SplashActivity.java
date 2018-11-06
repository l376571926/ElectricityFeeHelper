package group.tonight.electricityfeehelper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import group.tonight.electricityfeehelper.BR;
import group.tonight.electricityfeehelper.R;

public class SplashActivity extends AppCompatActivity {

    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = mPreferences.getBoolean(KEY_FIRST_LAUNCH, true);

        mViewDataBinding.setVariable(BR.firstLaunch, isFirstLaunch);
        if (isFirstLaunch) {
            mPreferences.edit()
                    .putBoolean(KEY_FIRST_LAUNCH, false)
                    .apply();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeNewActivity.class));
                finish();
            }
        }, 2000);
    }

}