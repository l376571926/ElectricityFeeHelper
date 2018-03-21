package group.tonight.electricityfeehelper.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.MyUserRecyclerViewAdapter;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.activities.MainActivity;
import group.tonight.electricityfeehelper.activities.UserInfoActivity;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.interfaces.OnListFragmentInteractionListener;
import group.tonight.electricityfeehelper.utils.MyUtils;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserListFragment extends Fragment implements OnFragmentInteractionListener {
    public static final String TAG = UserListFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyUserRecyclerViewAdapter mMyUserRecyclerViewAdapter;
    private List<User> mUserList;
    private TextView mCountView;
    private RecyclerView mListView;
    private RefreshLayout mRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserListFragment() {
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.smart_refresh_layout);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh();
                OkHttpUtils.get()
                        .url(MyUtils.LATEST_USER_URL)
                        .build()
                        .execute(mUserListCallback);
            }
        });
        mListView = (RecyclerView) view.findViewById(R.id.list);
        mCountView = (TextView) view.findViewById(R.id.count);

        setHasOptionsMenu(true);

        // Set the adapter
        final Context context = mListView.getContext();

        mListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        if (mColumnCount <= 1) {
            mListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mUserList = new ArrayList<>();
        mMyUserRecyclerViewAdapter = new MyUserRecyclerViewAdapter(mUserList, new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(User item) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("_id", item.getId());
                startActivity(intent);
            }
        });
        mListView.setAdapter(mMyUserRecyclerViewAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = ((MainApp) getActivity().getApplication()).getDaoSession().getUserDao();
                final List<User> list = userDao.loadAll();
                Log.e(TAG, "onCreateView: " + list.size());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCountView.setText(list.size() + "");
                            mUserList.clear();
                            mUserList.addAll(list);
                            mMyUserRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
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
        mSearchView.setQueryHint("请输入用户编号或者用户名称");
//        mSearchView.setIconifiedByDefault(false);//false表示加载后默认为搜索框输入状态
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: " + newText);
                if (getActivity() != null) {
                    DaoSession daoSession = ((MainApp) getActivity().getApplication()).getDaoSession();
                    UserDao userDao = daoSession.getUserDao();

                    QueryBuilder<User> userQueryBuilder = userDao.queryBuilder()
                            .whereOr(UserDao.Properties.UserId.like("%" + newText + "%"), UserDao.Properties.UserName.like("%" + newText + "%"));

                    List<User> list = userQueryBuilder
                            .limit(50)
                            .list();
                    mUserList.clear();
                    mUserList.addAll(list);
                    mMyUserRecyclerViewAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user:
                Log.e(TAG, "onOptionsItemSelected: ");
                AddUserFragment addUserFragment = AddUserFragment.newInstance("", "");
                addUserFragment.show(getChildFragmentManager(), "");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
//                if (getContext() != null) {
//                    mProgressDialog.dismiss();
//                    Toast.makeText(getContext().getApplicationContext(), "获取最新用户数据成功", Toast.LENGTH_SHORT).show();
//                }

                if (mMyUserRecyclerViewAdapter != null) {
                    mCountView.setText(response.size() + "");
                    mUserList.clear();
                    mUserList.addAll(response);
                    mMyUserRecyclerViewAdapter.notifyDataSetChanged();
//                    mAdapter.refresh(response);
//                    if (mProgressDialog != null) {
//                        mProgressDialog.dismiss();
//                    }
                }
            }
        }
    };

    @Override
    public void onFragmentInteraction(int result) {
        Log.e(TAG, "onFragmentInteraction: " + result);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        List<User> userList = ((MainApp) activity.getApplication()).getDaoSession().getUserDao().loadAll();
        if (mMyUserRecyclerViewAdapter != null) {
            mCountView.setText(userList.size() + "");
            mUserList.clear();
            mUserList.addAll(userList);
            mMyUserRecyclerViewAdapter.notifyDataSetChanged();
//                    mAdapter.refresh(response);
//                    if (mProgressDialog != null) {
//                        mProgressDialog.dismiss();
//                    }
        }
    }
}
