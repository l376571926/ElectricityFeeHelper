package group.tonight.electricityfeehelper.activities;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.crud.UserDatabase;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.fragments.AddUserFragment;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;

/**
 * 用户资料
 */
public class UserInfoActivity extends BackEnableActivity implements OnFragmentInteractionListener, View.OnClickListener {

    private User mUser;
    private TextView mUserIdTv;
    private TextView mUserNameTv;
    private TextView mAddressTv;
    private TextView mPhoneTv;
    private TextView mDeviceIdTv;
    private TextView mYingShouTv;
    private TextView mShiShouTv;
    private TextView mQianFeiTv;
    private TextView mPositionIdTv;
    private TextView mSerialIdTv;

    @Override
    protected int setChildLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected String setActivityName() {
        return "用户资料";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdTv = (TextView) findViewById(R.id.user_id);
        mUserNameTv = (TextView) findViewById(R.id.user_name);
        mAddressTv = (TextView) findViewById(R.id.address);
        mPhoneTv = (TextView) findViewById(R.id.phone);

        mDeviceIdTv = (TextView) findViewById(R.id.device_id);//电能表号
        mPositionIdTv = (TextView) findViewById(R.id.position_id);//位置序号
        mSerialIdTv = (TextView) findViewById(R.id.serial_id);//抄表段编号

        mYingShouTv = (TextView) findViewById(R.id.ying_shou);
        mShiShouTv = (TextView) findViewById(R.id.shi_shou);
        mQianFeiTv = (TextView) findViewById(R.id.qian_fei);

        mPhoneTv.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = UserDatabase.get()
                        .getUserDao()
                        .load(getIntent().getIntExtra("_id", -1));
                if (user == null) {
                    return;
                }
                mUser = user;
                LiveData<User> liveData = MainApp.getDaoSession().getUserDao().loadLiveDataUser(mUser.getId());
                liveData.observe(UserInfoActivity.this, new Observer<User>() {
                    @Override
                    public void onChanged(@Nullable User user) {
                        if (user == null) {
                            return;
                        }
                        mUser = user;
                        initData();
                    }
                });
            }
        }).start();
    }

    private void initData() {
        String userId = mUser.getUserId();
        String userName = mUser.getUserName();
        String address = mUser.getUserAddress();
        String phone = mUser.getUserPhone();

        String deviceId = mUser.getPowerMeterId();
        String positionId = mUser.getMeterReadingId();
        String serialId = mUser.getPowerLineId();

        mUserIdTv.setText(userId);
        mUserNameTv.setText(userName);
        mAddressTv.setText(address);
        mPhoneTv.setText(phone);

        mDeviceIdTv.setText(deviceId);
        mPositionIdTv.setText(positionId);
        mSerialIdTv.setText(serialId);

//        List<Order> orderList = mUser.getOrders();
//        double yingShouSum = 0;
//        double shiShouSum = 0;
//        double qianfeiSum = 0;
//        for (Order order : orderList) {
//            yingShouSum += order.getYingShou();
//            shiShouSum += order.getShiShou();
//            qianfeiSum += order.getQianFei();
//        }
//        mYingShouTv.setText(getString(R.string.yuan_place_holder, MyUtils.formatDecimal(yingShouSum)));
//        mShiShouTv.setText(getString(R.string.yuan_place_holder, MyUtils.formatDecimal(shiShouSum)));
//        mQianFeiTv.setText(getString(R.string.yuan_place_holder, MyUtils.formatDecimal(qianfeiSum)));
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
                AddUserFragment.newInstance(mUser.getId()).show(getSupportFragmentManager(), "");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int result) {
        if (result == Activity.RESULT_OK) {


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone:
                String phone = mPhoneTv.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    return;
                }
                if (TextUtils.equals("0", phone)) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
