package group.tonight.electricityfeehelper.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            startActivity(new Intent(SplashActivity.this, HomeNewActivity.class));
            finish();
        } else {
            Toast.makeText(this, "请允许app获取相关权限", Toast.LENGTH_SHORT).show();
        }
    }
}