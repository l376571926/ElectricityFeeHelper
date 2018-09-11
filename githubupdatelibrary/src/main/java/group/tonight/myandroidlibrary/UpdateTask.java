package group.tonight.myandroidlibrary;


import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UpdateTask implements Runnable {
    private static final String BASE_API_URL = "https://api.github.com/repos/";
    private static String all;
    private static String latest;

    private static OkHttpClient okHttpClient = new OkHttpClient();

    private String mUserName = "l376571926";
    private String mRepositoryName = "ElectricityFeeHelper";
    private ResultCallback mResultCallback;

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public void setRepositoryName(String repositoryName) {
        mRepositoryName = repositoryName;
    }

    public void setCallback(ResultCallback resultCallback) {
        mResultCallback = resultCallback;
    }

    @Override
    public void run() {
        all = BASE_API_URL + mUserName + "/" + mRepositoryName + "/releases";
        latest = all + "/latest";

        okHttpClient.newCall(
                new Request.Builder()
                        .get()
                        .url(latest)
                        .build()
        )
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                        if (mResultCallback != null) {
                            Looper.prepare();
                            mResultCallback.onFailure(e);
                            Looper.loop();
                        }
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        int code = response.code();
                        String message = response.message();
                        if (code == 200) {
                            Release release = new Gson().fromJson(response.body().charStream(), Release.class);
                            if (mResultCallback != null) {
                                Looper.prepare();
                                mResultCallback.onResponse(release);
                                Looper.loop();
                            }
                        }
                    }
                });
    }

    public interface ResultCallback {
        void onFailure(Exception e);

        void onResponse(Release release);
    }
}
