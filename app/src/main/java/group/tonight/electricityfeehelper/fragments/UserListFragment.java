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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.MyUserRecyclerViewAdapter;
import group.tonight.electricityfeehelper.R;
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
 * 用户库
 */
public class UserListFragment extends Fragment implements OnFragmentInteractionListener {
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
                UserDao userDao = MainApp.getDaoSession().getUserDao();
                final List<User> list = userDao.loadAll();
                KLog.e("onCreateView: " + list.size());
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
                    DaoSession daoSession = MainApp.getDaoSession();
                    UserDao userDao = daoSession.getUserDao();

                    QueryBuilder<User> userQueryBuilder = userDao.queryBuilder()
                            .whereOr(
                                    UserDao.Properties.UserId.like("%" + newText + "%")//匹配用户ID
                                    , UserDao.Properties.UserName.like("%" + newText + "%")//匹配用户姓名
                                    , UserDao.Properties.PowerMeterId.like("%" + newText + "%")//匹配电能表编号
                                    , UserDao.Properties.UserPhone.like("%" + newText + "%")//匹配用户手机
                            );

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
                KLog.e();
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
                MyUtils.saveUserListToDb(responseBody1.bytes());
            }
            List<User> userList = MainApp.getDaoSession().getUserDao().loadAll();
            return userList;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            KLog.e(e);
        }

        @Override
        public void onResponse(List<User> response, int id) {
            if (response != null) {
                KLog.e(response.size());
                if (mMyUserRecyclerViewAdapter != null) {
                    mCountView.setText(response.size() + "");
                    mUserList.clear();
                    mUserList.addAll(response);
                    mMyUserRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onFragmentInteraction(int result) {
        KLog.e("onFragmentInteraction: " + result);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        List<User> userList = MainApp.getDaoSession().getUserDao().loadAll();
        if (mMyUserRecyclerViewAdapter != null) {
            mCountView.setText(userList.size() + "");
            mUserList.clear();
            mUserList.addAll(userList);
            mMyUserRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
