package group.tonight.electricityfeehelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.fragments.OrderListFragment;
import group.tonight.electricityfeehelper.fragments.SettingFragment;
import group.tonight.electricityfeehelper.fragments.UserListFragment;
import group.tonight.electricityfeehelper.interfaces.OnListFragmentInteractionListener;

public class HomeActivity extends AppCompatActivity implements OnListFragmentInteractionListener {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mToolbar.setTitle("首页");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, OrderListFragment.newInstance(1))
                            .commit();
                    return true;
                case R.id.navigation_dashboard:
                    mToolbar.setTitle("用户库");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, UserListFragment.newInstance(1))
                            .commit();
                    return true;
                case R.id.navigation_notifications:
                    mToolbar.setTitle("设置");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, SettingFragment.newInstance())
                            .commit();
                    return true;
            }
            return false;
        }
    };
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, OrderListFragment.newInstance(1))
                .commit();
    }

    @Override
    public void onListFragmentInteraction(User item) {
        Intent intent = new Intent(this, OrderListActivity.class);
        intent.putExtra("_id", item.getId());
        startActivity(intent);
    }
}
