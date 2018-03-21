package group.tonight.electricityfeehelper.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.activities.MainActivity;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.OrderDao;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.utils.MyUtils;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ProgressDialog mProgressDialog;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        view.findViewById(R.id.update).setOnClickListener(this);
        view.findViewById(R.id.update_order).setOnClickListener(this);
        view.findViewById(R.id.update_user).setOnClickListener(this);

        MyUtils.setBtnWaterBg(view.findViewById(R.id.update));
        MyUtils.setBtnWaterBg(view.findViewById(R.id.update_order));
        MyUtils.setBtnWaterBg(view.findViewById(R.id.update_user));

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("用户数据准备中，请稍等");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private static final String TAG = SettingFragment.class.getSimpleName();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                OkHttpUtils.get()
                        .url(MyUtils.LATEST_UPDATE_URL)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e(TAG, "onError: " + e);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String url = jsonObject.getString("url");
                                    int versionCode = jsonObject.getInt("versionCode");
                                    String versionName = jsonObject.getString("versionName");
                                    String description = jsonObject.getString("description");

                                    KLog.e(versionCode + " " + versionName + " " + description);

                                    PackageManager packageManager = getActivity().getPackageManager();
                                    PackageInfo packageInfo = null;
                                    try {
                                        packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    if (packageInfo != null) {
                                        if (packageInfo.versionCode != versionCode) {
                                            Uri uri = Uri.parse(url);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext().getApplicationContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                break;
            case R.id.update_order:
                mProgressDialog.show();
                OkHttpUtils.get()
                        .url(MyUtils.LATEST_ORDER_URL)
                        .build()
                        .execute(mOrderListCallback);
                break;
            case R.id.update_user:
                mProgressDialog.show();
                OkHttpUtils.get()
                        .url(MyUtils.LATEST_USER_URL)
                        .build()
                        .execute(mUserListCallback);
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

    private Callback<List<User>> mUserListCallback = new Callback<List<User>>() {
        @Override
        public List<User> parseNetworkResponse(Response response, int id) throws Exception {
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
            for (int i = 0; i < jsonArray.length(); i++) {
                String userInfoUrl = jsonArray.getString(i);
                Response execute = OkHttpUtils.get()
                        .url(userInfoUrl)
                        .build()
                        .execute();
                ResponseBody responseBody1 = execute.body();
                if (responseBody1 == null) {
                    return null;
                }
                byte[] bytes = responseBody1.bytes();
                MainActivity.saveUserInfoToDb(activity, new String(Base64.decode(bytes, Base64.DEFAULT)));
            }
            List<User> userList = ((MainApp) activity.getApplication()).getDaoSession().getUserDao().loadAll();
            return userList;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Log.e(TAG, "onError: " + e);
        }

        @Override
        public void onResponse(List<User> response, int id) {
            Log.e(TAG, "onResponse: ");
            if (response != null) {
                if (getContext() != null) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getContext().getApplicationContext(), "获取最新用户数据成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private Callback<List<User>> mOrderListCallback = new Callback<List<User>>() {
        @Override
        public List<User> parseNetworkResponse(Response response, int id) throws Exception {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();
            JSONArray jsonArray = new JSONArray(json);
            if (getActivity() == null) {
                return null;
            }

            DaoSession daoSession = ((MainApp) getActivity().getApplication()).getDaoSession();
            OrderDao orderDao = daoSession.getOrderDao();
            UserDao userDao = daoSession.getUserDao();

            for (int i = 0; i < jsonArray.length(); i++) {
                String orderUrl = jsonArray.getString(i);
                Response execute = OkHttpUtils.get()
                        .url(orderUrl)
                        .build()
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

                    User user = userDao.queryBuilder()
                            .where(UserDao.Properties.UserId.eq(userId))
                            .unique();
                    if (user != null) {
                        Long id1 = user.getId();

                        Order unique = orderDao.queryBuilder()
                                .where(OrderDao.Properties.Uid.eq(id1), OrderDao.Properties.OrderDate.eq(orderDate))
                                .build()
                                .unique();
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
            List<User> userList = userDao.loadAll();
            return userList;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Log.e(TAG, "onError: " + e);
        }

        @Override
        public void onResponse(List<User> response, int id) {
            if (response != null) {
                if (getContext() != null) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), "欠费用户数据更新成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
