package group.tonight.myandroidlibrary;


import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class UpdateTask implements Runnable {
    private static final String BASE_API_URL = "https://api.github.com/repos/";
    private static String all;
    private static String latest;

    private static OkHttpClient okHttpClient;

    private String mUserName = "l376571926";
    private String mRepositoryName = "ElectricityFeeHelper";
    private ResultCallback mResultCallback;
    private Handler mHandler= new Handler(Looper.getMainLooper());

    public UpdateTask() {
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLSocketFactoryCompat sslSocketFactoryCompat = new SSLSocketFactoryCompat(x509TrustManager);
        okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactoryCompat, x509TrustManager)
                .build();
    }

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
                    public void onFailure(okhttp3.Call call, final IOException e) {
                        e.printStackTrace();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mResultCallback != null) {
                                    mResultCallback.onFailure(e);
                                }
                            }
                        });
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        int code = response.code();
                        String message = response.message();
                        if (code == 200) {
                            ResponseBody body = response.body();
                            if (body == null) {
                                return;
                            }
                            final Release release = new Gson().fromJson(body.charStream(), Release.class);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mResultCallback != null) {
                                        mResultCallback.onResponse(release);
                                    }
                                }
                            });

                        }
                    }
                });
    }

    public interface ResultCallback {
        void onFailure(Exception e);

        void onResponse(Release release);
    }
}
