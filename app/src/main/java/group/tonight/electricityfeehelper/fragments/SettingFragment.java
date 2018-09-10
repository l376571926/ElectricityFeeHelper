package group.tonight.electricityfeehelper.fragments;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.activities.SettingActivity;
import group.tonight.electricityfeehelper.crud.OrderDao;
import group.tonight.electricityfeehelper.crud.UserDao;
import group.tonight.electricityfeehelper.crud.UserDatabase;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.utils.MyUtils;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 设置
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    private ProgressDialog mProgressDialog;

    private int mAddCount;
    private int mUpdateCount;
    private String mNewApkUrl;
    private View mNewVersionLabel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mNewVersionLabel = view.findViewById(R.id.new_version);

        view.findViewById(R.id.update).setOnClickListener(this);
        view.findViewById(R.id.update_order).setOnClickListener(this);
        view.findViewById(R.id.update_user).setOnClickListener(this);

        MyUtils.setBtnWaterBg(view.findViewById(R.id.update));
        MyUtils.setBtnWaterBg(view.findViewById(R.id.update_order));
        MyUtils.setBtnWaterBg(view.findViewById(R.id.update_user));

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("用户数据下载中，请稍等");

        OkGo.<String>get(MyUtils.LATEST_UPDATE_URL)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String apkUrl = jsonObject.getString("url");
                            int versionCode = jsonObject.getInt("versionCode");
                            String versionName = jsonObject.getString("versionName");
                            String description = jsonObject.getString("description");

                            KLog.e(versionCode + " " + versionName + " " + description);
                            if (getActivity() != null) {
                                PackageManager packageManager = getActivity().getPackageManager();
                                PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
                                if (packageInfo.versionCode != versionCode) {
                                    mNewApkUrl = apkUrl;
                                    mNewVersionLabel.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                if (getActivity() != null) {
                    if (mNewApkUrl == null) {
                        Toast.makeText(getActivity().getApplicationContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
                    } else {
                        if (getActivity() instanceof SettingActivity) {
                            SettingActivity homeActivity = (SettingActivity) getActivity();
                            homeActivity.versionUpdate(mNewApkUrl);
                        }
                    }
                }
                break;
            case R.id.update_order:
                mProgressDialog.show();
                OkGo.<List<User>>get(MyUtils.LATEST_ORDER_URL)
                        .execute(mOrderAbsCallback);
                break;
            case R.id.update_user:
                mProgressDialog.show();
                OkGo.<List<User>>get(MyUtils.LATEST_USER_URL)
                        .execute(mUserAbsCallback);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mProgressDialog.dismiss();
    }

    private AbsCallback<List<User>> mUserAbsCallback = new AbsCallback<List<User>>() {
        @Override
        public void onSuccess(com.lzy.okgo.model.Response<List<User>> response) {
            if (response != null) {
                KLog.e(response.body().size());
                if (getContext() != null) {
                    mProgressDialog.dismiss();
                    new AlertDialog.Builder(getContext())
                            .setMessage("更新成功，新增用户数：" + mAddCount + "，更新用户数：" + mUpdateCount)
                            .show();
                }
            }
        }

        @Override
        public List<User> convertResponse(Response response) throws Throwable {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();
            JSONArray jsonArray = new JSONArray(json);
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return null;
            }
            mAddCount = 0;
            mUpdateCount = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                String userInfoUrl = jsonArray.getString(i);
                KLog.e(userInfoUrl);
                Response execute = OkGo.<byte[]>get(userInfoUrl)
                        .execute();
                ResponseBody responseBody1 = execute.body();
                if (responseBody1 == null) {
                    return null;
                }
                int[] ints = MyUtils.saveUserListToDb(responseBody1.bytes());
                mAddCount += ints[0];
                mUpdateCount += ints[1];
            }
            return MainApp.getDaoSession().getUserDao().loadAll();
        }
    };
    private AbsCallback<List<User>> mOrderAbsCallback = new AbsCallback<List<User>>() {
        @Override
        public void onSuccess(com.lzy.okgo.model.Response<List<User>> response) {
            if (response != null) {
                if (getContext() != null) {
                    mProgressDialog.dismiss();
                    new AlertDialog.Builder(getContext())
                            .setMessage("更新成功，新增欠费记录数：" + response.body().size() + "，更新欠费记录数：" + 0)
                            .show();
                }
            }
        }

        @Override
        public List<User> convertResponse(Response response) throws Throwable {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();
            JSONArray jsonArray = new JSONArray(json);
            if (getActivity() == null) {
                return null;
            }

            UserDatabase daoSession = MainApp.getDaoSession();
            OrderDao orderDao = daoSession.getOrderDao();
            UserDao userDao = daoSession.getUserDao();

            for (int i = 0; i < jsonArray.length(); i++) {
                String orderUrl = jsonArray.getString(i);
                Response execute = OkGo.<byte[]>get(orderUrl)
                        .execute();
                ResponseBody responseBody1 = execute.body();
                if (responseBody1 == null) {
                    return null;
                }
                byte[] bytes = responseBody1.bytes();
                byte[] decode = Base64.decode(bytes, Base64.DEFAULT);
                String orderJson = new String(decode);
                JSONArray data = new JSONArray(orderJson);

                for (int m = 0; m < data.length(); m++) {
                    JSONObject orderObj = data.getJSONObject(m);
                    String userId = orderObj.getString("用户编号");
                    String userName = orderObj.getString("用户名称");
                    String userAddress = orderObj.getString("用电地址");
                    String orderDate = orderObj.getString("年月");
                    String userPhone = orderObj.getString("电话");

                    double yingShou = orderObj.getDouble("应收电费");
                    double shiShou = orderObj.getDouble("实收电费");
                    double qianFei = orderObj.getDouble("欠费金额");
                    double yingShouWeiYue = orderObj.getDouble("应收违约金");
                    double shiShouWeiYue = orderObj.getDouble("实收违约金");
                    double qianJiaoWeiYue = orderObj.getDouble("欠交违约金");

                    String orderStatus = orderObj.getString("电费类别");

                    User user = userDao.loadUserByUserId(userId);
                    if (user != null) {
                        int id1 = user.getId();
                        Order unique = UserDatabase.get()
                                .getOrderDao()
                                .loadOrderByUidAndOrderDate(id1,orderDate);
                        if (unique == null) {
                            unique = new Order();
                            unique.setUid(id1);
                            unique.setOrderDate(orderDate);

                            unique.setYingShou(yingShou);
                            unique.setShiShou(shiShou);
                            unique.setQianFei(qianFei);

                            unique.setYingShouWeiYue(yingShouWeiYue);
                            unique.setShiShouWeiYue(shiShouWeiYue);
                            unique.setQianJiaoWeiYue(qianJiaoWeiYue);
                            unique.setOrderStatus(orderStatus);

                            user.setYingShouSum(user.getYingShouSum() + yingShou);
                            user.setShiShouSum(user.getShiShouSum() + shiShou);
                            user.setQianFeiSum(user.getQianFeiSum() + qianFei);

                            orderDao.insert(unique);
                            userDao.update(user);
                        }
                    }
                }
            }
            return userDao.loadAll();
        }
    };
}
