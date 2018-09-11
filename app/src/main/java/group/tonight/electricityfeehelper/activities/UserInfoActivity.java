package group.tonight.electricityfeehelper.activities;

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

import com.android.databinding.library.baseAdapters.BR;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.fragments.AddUserFragment;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;

/**
 * 用户资料
 */
public class UserInfoActivity extends BackEnableActivity implements OnFragmentInteractionListener {

    private int mId;
    private String mUserPhone;

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

        mId = getIntent().getIntExtra("_id", -1);
        LiveData<User> liveData = MainApp.getDaoSession().getUserDao().loadLiveDataUser(mId);
        liveData.observe(UserInfoActivity.this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user == null) {
                    return;
                }
                mUserPhone = user.getUserPhone();
                setLayoutData(BR.user, user);
                setLayoutData(BR.dial, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(mUserPhone)) {
                            return;
                        }
                        if (TextUtils.equals("0", mUserPhone)) {
                            return;
                        }
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + mUserPhone);
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_user_info) {
            AddUserFragment.newInstance(mId).show(getSupportFragmentManager(), "");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int result) {
    }
}
