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

    private static final String TAG = SplashActivity.class.getSimpleName();
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
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String[] strings = {
                            "nydlj_070.txt",
                            "nydlj_283.txt",
                            "nydlj_322.txt",
                            "nydlj_496.txt",
                            "nydlj_497.txt",
                            "nydlj_503.txt",
                            "nydlj_513.txt",
                            "nydlj_601.txt",
                            "nydlj_703.txt"
//                            "nydlj_735.txt"
                    };
                    int length = strings.length;
                    mInitDataPb.setMax(length);
                    for (int i = 0; i < length; i++) {
                        String fileName = strings[i];
                        try {
                            InputStream inputStream = getApplicationContext().getAssets().open(fileName);
                            int size = inputStream.available();
                            byte[] bytes = new byte[size];
                            int read = inputStream.read(bytes);
                            inputStream.close();
                            Log.e(TAG, "run: ----------------------" + fileName + "-----------------------------");
                            MyUtils.saveUserListToDb(SplashActivity.this, bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mInitDataPb.setProgress(finalI);
                            }
                        });
                    }
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
            }).start();
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }


}