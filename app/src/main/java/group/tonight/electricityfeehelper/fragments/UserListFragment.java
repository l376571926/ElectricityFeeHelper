package group.tonight.electricityfeehelper.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.socks.library.KLog;

import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.activities.SettingActivity;
import group.tonight.electricityfeehelper.activities.UserInfoActivity;
import group.tonight.electricityfeehelper.crud.UserDatabase;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;

/**
 * 用户库
 */
public class UserListFragment extends Fragment implements OnFragmentInteractionListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private TextView mCountView;
    private RecyclerView mRecyclerView;

    @SuppressWarnings("unused")
    public static UserListFragment newInstance(int columnCount) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mCountView = (TextView) view.findViewById(R.id.count);

        setHasOptionsMenu(true);

        // Set the adapter
        final Context context = mRecyclerView.getContext();

        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mRecyclerView.setAdapter(mBaseQuickAdapter);
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(view.getContext(), UserInfoActivity.class);
                intent.putExtra("_id", ((User) adapter.getItem(position)).getId());
                startActivity(intent);
            }
        });
        LiveData<List<User>> liveData = UserDatabase.get()
                .getUserDao()
                .loadAllLiveData();
        liveData.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users == null) {
                    return;
                }
                mCountView.setText(users.size() + "");
                mBaseQuickAdapter.replaceData(users);
            }
        });
        return view;
    }

    /**
     * 重写这个方法要先调用setHasOptionsMenu方法
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_user_list_fragment, menu);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setQueryHint("电能表编号、姓名、手机、用户编号");
//        mSearchView.setIconifiedByDefault(false);//false表示加载后默认为搜索框输入状态
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KLog.e(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                KLog.e(newText);
                if (getActivity() != null) {
                    LiveData<List<User>> liveData = UserDatabase.get()
                            .getUserDao()
                            .searchUser(newText, newText, newText, newText);
                    liveData.observe(UserListFragment.this, new Observer<List<User>>() {
                        @Override
                        public void onChanged(@Nullable List<User> users) {
                            if (users == null) {
                                return;
                            }
                            mBaseQuickAdapter.replaceData(users);
                        }
                    });
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user:
                AddUserFragment.newInstance(0).show(getChildFragmentManager(), "");
                break;
            case R.id.nav_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int result) {
        KLog.e("onFragmentInteraction: " + result);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        List<User> userList = MainApp.getDaoSession().getUserDao().loadAll();
        mCountView.setText(userList.size() + "");
        mBaseQuickAdapter.replaceData(userList);
    }

    private BaseQuickAdapter<User, BaseViewHolder> mBaseQuickAdapter = new BaseQuickAdapter<User, BaseViewHolder>(R.layout.fragment_user) {
        @Override
        protected void convert(BaseViewHolder helper, User item) {
            helper.setText(R.id.power_meter_id, item.getPowerMeterId());
            helper.setText(R.id.id, item.getUserId());
            helper.setText(R.id.content, item.getUserName());
            helper.setText(R.id.phone, item.getUserPhone());
        }
    };
}
