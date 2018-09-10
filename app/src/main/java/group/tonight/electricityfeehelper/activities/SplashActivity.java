package group.tonight.electricityfeehelper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.utils.MyUtils;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar mInitDataPb;
    private TextView mHintTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHintTv = (TextView) findViewById(R.id.first_launch_hint_tv);
        mInitDataPb = (ProgressBar) findViewById(R.id.init_data_pb);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("first_launch", true)) {
            preferences.edit()
                    .putBoolean("first_launch", false)
                    .apply();
            mHintTv.setVisibility(View.VISIBLE);
            mInitDataPb.setVisibility(View.VISIBLE);
            new IndexActivity.ExcelParseTask(SplashActivity.this).execute();
            startActivity(new Intent(SplashActivity.this, IndexActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, IndexActivity.class));
            finish();
        }
    }


}