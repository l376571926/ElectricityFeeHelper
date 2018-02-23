package group.tonight.electricityfeehelper.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.MyUserRecyclerViewAdapter;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.activities.UserInfoActivity;
import group.tonight.electricityfeehelper.dao.DaoSession;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.dao.UserDao;
import group.tonight.electricityfeehelper.interfaces.OnListFragmentInteractionListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserListFragment extends Fragment {
    public static final String TAG = UserListFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyUserRecyclerViewAdapter mMyUserRecyclerViewAdapter;
    private List<User> mUserList;
    private TextView mCountView;
    private RecyclerView mListView;
    private SearchView mSearchView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserListFragment() {
    }

    // TODO: Customize parameter initialization
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
        mSearchView = (SearchView) view.findViewById(R.id.search_view);
        mListView = (RecyclerView) view.findViewById(R.id.list);
        mCountView = (TextView) view.findViewById(R.id.count);

        mSearchView.setQueryHint("请输入用户编号或者用户名称");
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
                return true;
            }
        });

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

}
