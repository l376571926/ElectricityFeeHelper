package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.OrderDao;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.fragments.AddUserFragment;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.utils.BaseRecyclerAdapter;
import group.tonight.electricityfeehelper.utils.MyUtils;
import group.tonight.electricityfeehelper.utils.SmartViewHolder;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends BackEnableActivity implements OnFragmentInteractionListener, AdapterView.OnItemClickListener, OnRefreshLoadMoreListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 3030;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private RefreshLayout mRefreshLayout;

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected String setActivityName() {
        return getString(R.string.app_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.smart_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("first_launch", true)) {
            preferences.edit()
                    .putBoolean("first_launch", false)
                    .apply();
            OkHttpUtils.get()
                    .url("https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_init.json")
                    .build()
                    .execute(new UserListCallback());
        }
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected boolean showBackArrow() {
        return false;
    }

    private BaseRecyclerAdapter<User> mAdapter = new BaseRecyclerAdapter<User>(R.layout.list_item_user) {
        @Override
        protected void onBindViewHolder(SmartViewHolder holder, User model, int position) {
            holder.text(R.id.user_id, model.getAccountId() + "");
            holder.text(R.id.user_name, model.getUserName());
            holder.text(R.id.phone, model.getPhone());

            List<Order> orderList = model.getOrders();
            double qianFeiAmount = 0;
            for (Order order : orderList) {
                double qianFei = order.getQianFei();
                qianFeiAmount += qianFei;
            }
            holder.text(R.id.money, getString(R.string.qian_fei_sum_place_holder, qianFeiAmount + ""));
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        //通过MenuItem得到SearchView
        mSearchView = ((SearchView) searchItem.getActionView());
        mSearchView.setQueryHint("输入用户编号或者用户姓名");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: ");
                DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
                UserDao userDao = daoSession.getUserDao();

                List<User> list;
                if (MyUtils.isInteger(newText)) {
                    list = userDao.queryBuilder()
                            .where(UserDao.Properties.AccountId.like("%" + newText + "%"))
                            .list();
                } else {
                    list = userDao.queryBuilder()
                            .where(UserDao.Properties.UserName.like("%" + newText + "%"))
                            .list();
                }

                Log.e(TAG, "onFragmentInteraction: " + list.size());
                mAdapter.refresh(list);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
//        if (requestCode == REQUEST_CODE) {
//            //处理扫描结果（在界面上显示）
//            if (null != data) {
//                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }
//                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
//                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//                    Log.e(TAG, "onActivityResult: " + result);
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
//                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e(TAG, "onResume: ");
        DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
        UserDao userDao = daoSession.getUserDao();
        List<User> userList = userDao.loadAll();
//        Log.e(TAG, "onCreate: " + userList.size());
        mAdapter.refresh(userList);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e(TAG, "onPause: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_user) {
            AddUserFragment addUserFragment = AddUserFragment.newInstance("", "");
            addUserFragment.show(getSupportFragmentManager(), "");
        }
//        else if (item.getItemId() == R.id.action_add_user_batch) {
        /**
         * 打开默认二维码扫描界面
         */
//            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);

//            OkHttpUtils.get()
//                    .url("https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj.json")
////                    .url("https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest.json")
//                    .build()
//                    .execute(new UserListCallback());
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        OkHttpUtils.get()
//                .url("https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj.json")
                .url("https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest.json")
                .build()
                .execute(new UserListCallback());
    }

    private class UserListCallback extends Callback<List<User>> {

        @Override
        public List<User> parseNetworkResponse(Response response, int id) throws Exception {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();

            Log.e(TAG, "onResponse: " + response);
            try {
                JSONArray jsonArray = new JSONArray(json);
                int length = jsonArray.length();

                DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
                UserDao userDao = daoSession.getUserDao();
                OrderDao orderDao = daoSession.getOrderDao();

                //更新用户信息
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    long userId = jsonObject.getLong("用户编号");
                    String userName = jsonObject.getString("用户姓名");
                    String address = jsonObject.getString("详细地址");
                    String orderDate = jsonObject.getString("电表号");//月份
                    String phone = null;
                    try {
                        phone = jsonObject.getString("电话");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    User unique = userDao.queryBuilder()
                            .where(UserDao.Properties.AccountId.eq(userId))
                            .build()
                            .unique();
                    boolean isNew = false;
                    if (unique == null) {
                        isNew = true;
                        unique = new User();
                    }
                    unique.setAccountId(userId);
                    unique.setUserName(userName);
                    unique.setAddress(address);
                    unique.setPhone(phone);
                    long currentTimeMillis = System.currentTimeMillis();
                    unique.setUpdateTime(currentTimeMillis);
                    if (isNew) {
                        unique.setCreateTime(currentTimeMillis);
                        userDao.insert(unique);
                    } else {
                        userDao.update(unique);
                    }
                }
                List<User> userList = userDao.loadAll();
                Log.e(TAG, "onFragmentInteraction: " + userList.size());


                //更新欠费信息
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    long userId = jsonObject.getLong("用户编号");
                    String orderDate = jsonObject.getString("电表号");//月份
                    double yingShou = jsonObject.getDouble("应收电费");
                    double shiShou = jsonObject.getDouble("实收电费");
                    double qianFei = jsonObject.getDouble("欠费金额");
                    Log.e(TAG, "onResponse: ");

                    User unique = userDao.queryBuilder()
                            .where(UserDao.Properties.AccountId.eq(userId))//以userId找出这个用户在用户表中的主键
                            .build()
                            .unique();
                    if (unique != null) {
                        Long uniqueId = unique.getId();//用户表的主键，欠费表的外键

                        Order order1 = orderDao.queryBuilder()
                                .where(OrderDao.Properties.Uid.eq(uniqueId), OrderDao.Properties.OrderDate.eq(orderDate))
                                .build()
                                .unique();
                        if (order1 == null) {
                            Order order = new Order();
                            order.setUid(uniqueId);
                            order.setYingShou(yingShou);
                            order.setShiShou(shiShou);
                            order.setQianFei(qianFei);
                            order.setOrderDate(orderDate);

                            /**
                             * 201802--->1517414400
                             */
                            try {
                                long epoch = MyUtils.mDateFormat.parse(orderDate + "").getTime();
                                order.setCreateTime(epoch);

                                orderDao.insert(order);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return userList;
//                mAdapter.refresh(userList);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onResponse: " + e);
            }
            return null;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Log.e(TAG, "onError: " + e);
            if (mRefreshLayout.getState().opening) {
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
        }

        @Override
        public void onResponse(List<User> response, int id) {
            if (mRefreshLayout.getState().opening) {
                Toast.makeText(MainActivity.this, "获取最新数据成功", Toast.LENGTH_SHORT).show();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
            if (response != null) {
                mAdapter.refresh(response);
            }
        }
    }

    @Override
    public void onFragmentInteraction(int result) {
        Log.e(TAG, "onFragmentInteraction: " + result);
        if (result == Activity.RESULT_OK) {
            DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
            UserDao userDao = daoSession.getUserDao();
            List<User> userList = userDao.loadAll();
            Log.e(TAG, "onFragmentInteraction: " + userList.size());
            mAdapter.refresh(userList);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = mAdapter.getItem(position);
        if (item instanceof User) {
            User user = (User) item;
//            Log.e(TAG, "onItemClick: ");

            Intent intent = new Intent(this, OrderListActivity.class);
            intent.putExtra("_id", user.getId());
            startActivity(intent);
        }
    }
}
