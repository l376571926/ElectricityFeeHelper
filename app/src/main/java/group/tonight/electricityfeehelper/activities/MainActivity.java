package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import group.tonight.electricityfeehelper.utils.SmartViewHolder;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends BackEnableActivity implements OnFragmentInteractionListener, AdapterView.OnItemClickListener, OnRefreshLoadMoreListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 3030;

    private static final String LATEST_USER_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_user.json";
    private static final String LATEST_ORDER_URL = "https://raw.githubusercontent.com/l376571926/l376571926.github.io/master/nydlj_latest_order.json";

    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private RefreshLayout mRefreshLayout;
    private ProgressDialog mProgressDialog;
    private CheckBox mShowQianFeiCb;

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected String setActivityName() {
        return getString(R.string.app_name);
    }

    private boolean mShowQianFei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShowQianFeiCb = (CheckBox) findViewById(R.id.show_qian_fei);
        mShowQianFeiCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mShowQianFei = isChecked;

                DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
                UserDao userDao = daoSession.getUserDao();

                if (isChecked) {
                    List<User> list = userDao.queryBuilder()
                            .where(UserDao.Properties.QianFeiSum.notEq(0))
                            .list();
                    mAdapter.refresh(list);
                } else {
                    List<User> userList = userDao.loadAll();
                    Log.e(TAG, "onResume: " + userList.size());
                    mAdapter.refresh(userList);
                }
            }
        });

        mRefreshLayout = (RefreshLayout) findViewById(R.id.smart_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("用户数据准备中，请稍等");

        mRefreshLayout.setOnRefreshLoadMoreListener(this);

        OkHttpUtils.get()
                .url(LATEST_ORDER_URL)
                .build()
                .execute(mOrderListCallback);
    }

    @Override
    protected boolean showBackArrow() {
        return false;
    }

    private BaseRecyclerAdapter<User> mAdapter = new BaseRecyclerAdapter<User>(R.layout.list_item_user) {
        @Override
        protected void onBindViewHolder(SmartViewHolder holder, User model, int position) {
            holder.text(R.id.user_id, model.getUserId() + "");
            holder.text(R.id.user_name, model.getUserName());
            holder.text(R.id.phone, model.getUserPhone());

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
//                Log.e(TAG, "onQueryTextChange: ");
                DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
                UserDao userDao = daoSession.getUserDao();

                QueryBuilder<User> userQueryBuilder = userDao.queryBuilder()
                        .whereOr(UserDao.Properties.UserId.like("%" + newText + "%"), UserDao.Properties.UserName.like("%" + newText + "%"));

                if (mShowQianFei) {
                    userQueryBuilder.where(UserDao.Properties.QianFeiSum.notEq(0));
                }

                List<User> list = userQueryBuilder
                        .list();
                mAdapter.refresh(list);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_user) {
            AddUserFragment addUserFragment = AddUserFragment.newInstance("", "");
            addUserFragment.show(getSupportFragmentManager(), "");
        } else if (item.getItemId() == R.id.action_add_user_batch) {
            if (mProgressDialog != null) {
                mProgressDialog.show();
            }
            OkHttpUtils.get()
                    .url(LATEST_USER_URL)
                    .build()
                    .execute(mUserListCallback);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
        UserDao userDao = daoSession.getUserDao();
        List<User> userList = userDao.loadAll();
        Log.e(TAG, "onResume: " + userList.size());
        mAdapter.refresh(userList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        OkHttpUtils.get()
                .url(LATEST_ORDER_URL)
                .build()
                .execute(mOrderListCallback);
    }

    private Callback<List<User>> mOrderListCallback = new Callback<List<User>>() {
        @Override
        public List<User> parseNetworkResponse(Response response, int id) throws Exception {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();
            JSONArray jsonArray = new JSONArray(json);

            DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
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
                if (mAdapter != null) {
                    mAdapter.refresh(response);
                }
            }
        }
    };

    private Callback<List<User>> mUserListCallback = new Callback<List<User>>() {
        @Override
        public List<User> parseNetworkResponse(Response response, int id) throws Exception {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String json = responseBody.string();
            JSONArray jsonArray = new JSONArray(json);
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
                saveUserInfoToDb(MainActivity.this, new String(Base64.decode(bytes, Base64.DEFAULT)));
            }
            return ((MainApp) getApplication()).getDaoSession().getUserDao().loadAll();
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Log.e(TAG, "onError: " + e);
        }

        @Override
        public void onResponse(List<User> response, int id) {
            Log.e(TAG, "onResponse: ");
            if (response != null) {
                Toast.makeText(getApplicationContext(), "获取最新用户数据成功", Toast.LENGTH_SHORT).show();
                if (mAdapter != null) {
                    mAdapter.refresh(response);
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                }
            }
        }
    };

    @Override
    public void onFragmentInteraction(int result) {
        if (result == Activity.RESULT_OK) {
            DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
            UserDao userDao = daoSession.getUserDao();
            List<User> userList = userDao.loadAll();
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

    /**
     * 更新用户信息到数据库
     *
     * @param jsonArrayStr
     */
    public static void saveUserInfoToDb(Activity activity, String jsonArrayStr) {
        try {
            JSONArray data = new JSONArray(jsonArrayStr);
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                String userId = jsonObject.getString("用户编号");
                String userName = jsonObject.getString("用户名称");
                String userPhone = null;
                try {
                    userPhone = jsonObject.getString("联系方式");//有的用户可能没有填写联系方式
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "saveUserInfoToDb: " + userId + " " + userName + " " + e);
                }
                String powerLineId = jsonObject.getString("抄表段编号");
                String powerLineName = jsonObject.getString("抄表段名称");
                String meterReadingDay = jsonObject.getString("抄表例日");
                String meterReader = jsonObject.getString("抄表员");
                String measurementPointId = jsonObject.getString("计量点编号");
                String meterReadingId = jsonObject.getString("抄表序号");
                String powerMeterId = jsonObject.getString("电能表编号");
                String powerValueType = jsonObject.getString("示数类型");
                String lastPowerValue = jsonObject.getString("上次示数");
                String currentPowerValue = jsonObject.getString("本次示数");
                String consumePowerValue = jsonObject.getString("抄见电量");
                String comprehensiveRatio = jsonObject.getString("综合倍率");
                String meterReadingNumber = jsonObject.getString("抄表位数");
                String exceptionTypes = jsonObject.getString("异常类型");
                String meterReadingStatus = jsonObject.getString("抄表状态");
                String powerSupplyId = jsonObject.getString("供电单位");
                String powerSupplyName = jsonObject.getString("供电所");
                String userAddress = jsonObject.getString("用户地址");

                DaoSession daoSession = ((MainApp) activity.getApplication()).getDaoSession();
                UserDao userDao = daoSession.getUserDao();

                User unique = userDao.queryBuilder()
                        .where(UserDao.Properties.UserId.eq(userId))
                        .build()
                        .unique();
                boolean isNew = false;
                if (unique == null) {
                    isNew = true;
                    unique = new User();
                }
                boolean dataChange = false;//当数据改变时，才执行update数据库操作
                if (!TextUtils.isEmpty(userId) && !TextUtils.equals(unique.getUserId(), userId)) {
                    dataChange = true;
                    unique.setUserId(userId);
                }
                if (!TextUtils.isEmpty(userName) && !TextUtils.equals(unique.getUserName(), userName)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setUserName(userName);
                }
                if (!TextUtils.isEmpty(userPhone) && !TextUtils.equals(unique.getUserPhone(), userPhone)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setUserPhone(userPhone);
                }
                if (!TextUtils.isEmpty(powerLineId) && !TextUtils.equals(unique.getPowerLineId(), powerLineId)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setPowerLineId(powerLineId);
                }
                if (!TextUtils.isEmpty(powerLineName) && !TextUtils.equals(unique.getPowerLineName(), powerLineName)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setPowerLineName(powerLineName);
                }
                if (!TextUtils.isEmpty(meterReadingDay) && !TextUtils.equals(unique.getMeterReadingDay(), meterReadingDay)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setMeterReadingDay(meterReadingDay);
                }
                if (!TextUtils.isEmpty(meterReader) && !TextUtils.equals(unique.getMeterReader(), meterReader)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setMeterReader(meterReader);
                }
                if (!TextUtils.isEmpty(measurementPointId) && !TextUtils.equals(unique.getMeasurementPointId(), measurementPointId)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setMeasurementPointId(measurementPointId);
                }
                if (!TextUtils.isEmpty(meterReadingId) && !TextUtils.equals(unique.getMeterReadingId(), meterReadingId)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setMeterReadingId(meterReadingId);
                }
                if (!TextUtils.isEmpty(powerMeterId) && !TextUtils.equals(unique.getPowerMeterId(), powerMeterId)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setPowerMeterId(powerMeterId);
                }
                if (!TextUtils.isEmpty(powerValueType) && !TextUtils.equals(unique.getPowerValueType(), powerValueType)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setPowerValueType(powerValueType);
                }
                if (!TextUtils.isEmpty(lastPowerValue) && !TextUtils.equals(unique.getLastPowerValue(), lastPowerValue)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setLastPowerValue(lastPowerValue);
                }
                if (!TextUtils.isEmpty(currentPowerValue) && !TextUtils.equals(unique.getCurrentPowerValue(), currentPowerValue)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setCurrentPowerValue(currentPowerValue);
                }
                if (!TextUtils.isEmpty(consumePowerValue) && !TextUtils.equals(unique.getConsumePowerValue(), consumePowerValue)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setConsumePowerValue(consumePowerValue);
                }
                if (!TextUtils.isEmpty(comprehensiveRatio) && !TextUtils.equals(unique.getComprehensiveRatio(), comprehensiveRatio)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setComprehensiveRatio(comprehensiveRatio);
                }
                if (!TextUtils.isEmpty(meterReadingNumber) && !TextUtils.equals(unique.getMeterReadingNumber(), meterReadingNumber)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setMeterReadingNumber(meterReadingNumber);
                }
                if (!TextUtils.isEmpty(exceptionTypes) && !TextUtils.equals(unique.getExceptionTypes(), exceptionTypes)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setExceptionTypes(exceptionTypes);
                }
                if (!TextUtils.isEmpty(meterReadingStatus) && !TextUtils.equals(unique.getMeterReadingStatus(), meterReadingStatus)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setMeterReadingStatus(meterReadingStatus);
                }
                if (!TextUtils.isEmpty(powerSupplyId) && !TextUtils.equals(unique.getPowerSupplyId(), powerSupplyId)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setPowerSupplyId(powerSupplyId);
                }
                if (!TextUtils.isEmpty(powerSupplyName) && !TextUtils.equals(unique.getPowerSupplyName(), powerSupplyName)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setPowerSupplyName(powerSupplyName);
                }
                if (!TextUtils.isEmpty(userAddress) && !TextUtils.equals(unique.getUserAddress(), userAddress)) {
                    if (!dataChange) {
                        dataChange = true;
                    }
                    unique.setUserAddress(userAddress);
                }

                long currentTimeMillis = System.currentTimeMillis();
                unique.setUpdateTime(currentTimeMillis);
                if (isNew) {
                    unique.setCreateTime(currentTimeMillis);
                    userDao.insert(unique);
                } else {
                    if (dataChange) {
                        userDao.update(unique);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "saveUserInfoToDb: " + e);
        }
    }
}
