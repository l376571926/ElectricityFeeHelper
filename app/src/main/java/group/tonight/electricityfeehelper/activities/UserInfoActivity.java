package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.fragments.AddUserFragment;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.utils.MyUtils;

/**
 * 用户资料
 */
public class UserInfoActivity extends BackEnableActivity implements OnFragmentInteractionListener {

    private User mUser;
    private TextView mUserIdTv;
    private TextView mUserNameTv;
    private TextView mAddressTv;
    private TextView mPhoneTv;
    private TextView mDeviceIdTv;
    private TextView mYingShouTv;
    private TextView mShiShouTv;
    private TextView mQianFeiTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdTv = (TextView) findViewById(R.id.user_id);
        mUserNameTv = (TextView) findViewById(R.id.user_name);
        mAddressTv = (TextView) findViewById(R.id.address);
        mPhoneTv = (TextView) findViewById(R.id.phone);
        mDeviceIdTv = (TextView) findViewById(R.id.device_id);

        mYingShouTv = (TextView) findViewById(R.id.ying_shou);
        mShiShouTv = (TextView) findViewById(R.id.shi_shou);
        mQianFeiTv = (TextView) findViewById(R.id.qian_fei);

        long _id = getIntent().getLongExtra("_id", -1L);
        DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
        UserDao userDao = daoSession.getUserDao();

        mUser = userDao.load(_id);
        if (mUser == null) {
            return;
        }
        initData();

    }

    private void initData() {
        long userId = mUser.getAccountId();
        String userName = mUser.getUserName();
        String address = mUser.getAddress();
        String phone = mUser.getPhone();
//        String orderDate = mUser.getOrderDate();

        mUserIdTv.setText(userId + "");
        mUserNameTv.setText(userName);
        mAddressTv.setText(address);
        mPhoneTv.setText(phone);
//        mDeviceIdTv.setText(orderDate);

        List<Order> orderList = mUser.getOrders();
        double yingShouSum = 0;
        double shiShouSum = 0;
        double qianfeiSum = 0;
        for (Order order : orderList) {
            yingShouSum += order.getYingShou();
            shiShouSum += order.getShiShou();
            qianfeiSum += order.getQianFei();
        }
        mYingShouTv.setText(getString(R.string.yuan_place_holder, MyUtils.formatDecimal(yingShouSum)));
        mShiShouTv.setText(getString(R.string.yuan_place_holder, MyUtils.formatDecimal(shiShouSum)));
        mQianFeiTv.setText(getString(R.string.yuan_place_holder, MyUtils.formatDecimal(qianfeiSum)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_user_info) {
            if (mUser != null) {
                AddUserFragment addUserFragment = AddUserFragment.newInstance(mUser.getId() + "", "");
                addUserFragment.show(getSupportFragmentManager(), "");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected String setActivityName() {
        return "用户资料";
    }

    @Override
    public void onFragmentInteraction(int result) {
        if (result == Activity.RESULT_OK) {
            DaoSession daoSession = ((MainApp) getApplication()).getDaoSession();
            UserDao userDao = daoSession.getUserDao();
            mUser = userDao.load(mUser.getId());

            initData();
        }
    }
}
