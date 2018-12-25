package group.tonight.electricityfeehelper.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import group.tonight.electricityfeehelper.App;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.dao.User;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;

/**
 * 添加用电户
 */
public class AddUserFragment extends DialogFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";

    private User mUser;

    private OnFragmentInteractionListener mListener;
    private EditText mUserIdEt;
    private EditText mUserNameEt;
    private EditText mDeviceIdEt;
    private EditText mAddressET;
    private EditText mPhoneEt;
    private TextView mSaveBtn;
    private TextView mDialogTitleTv;
    private EditText mPositionIdEt;
    private EditText mSerialIdEt;

    public AddUserFragment() {
        // Required empty public constructor
    }

    public static AddUserFragment newInstance(User user) {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_add_user, container, false);

        mDialogTitleTv = (TextView) rootView.findViewById(R.id.dialog_title);

        mUserIdEt = (EditText) rootView.findViewById(R.id.user_id);
        mUserNameEt = (EditText) rootView.findViewById(R.id.user_name);
        mAddressET = (EditText) rootView.findViewById(R.id.address);
        mPhoneEt = (EditText) rootView.findViewById(R.id.phone);

        mDeviceIdEt = (EditText) rootView.findViewById(R.id.device_id);
        mPositionIdEt = (EditText) rootView.findViewById(R.id.position_id);
        mSerialIdEt = (EditText) rootView.findViewById(R.id.serial_id);

        View mUserIdVg = rootView.findViewById(R.id.user_id_vg);
        View mDeviceIdVg = rootView.findViewById(R.id.device_id_vg);
        View mSerialIdVg = rootView.findViewById(R.id.serial_id_vg);

        mSaveBtn = (TextView) rootView.findViewById(R.id.ok);

        rootView.findViewById(R.id.cancel).setOnClickListener(this);
        rootView.findViewById(R.id.ok).setOnClickListener(this);

        if (mUser != null) {
            if (mUser.getId() != 0) {
                mDialogTitleTv.setText("修改用户资料");
                mUserIdVg.setVisibility(View.GONE);
//            mDeviceIdVg.setVisibility(View.GONE);
                mSerialIdVg.setVisibility(View.GONE);

                mUserIdEt.setText(mUser.getUserId());
                mUserNameEt.setText(mUser.getUserName());
                mAddressET.setText(mUser.getUserAddress());
                mPhoneEt.setText(mUser.getUserPhone());

                mDeviceIdEt.setText(mUser.getPowerMeterId());
                mPositionIdEt.setText(mUser.getMeterReadingId());
                mSerialIdEt.setText(mUser.getPowerLineId());

                mSaveBtn.setText("保存");
            }
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                final String userId = mUserIdEt.getText().toString();
                final String userName = mUserNameEt.getText().toString();
                final String address = mAddressET.getText().toString();
                final String phone = mPhoneEt.getText().toString();

                final String deviceId = mDeviceIdEt.getText().toString();
                final String positionId = mPositionIdEt.getText().toString();
                final String serialId = mSerialIdEt.getText().toString();


                if (mUser != null) {
                    int id = mUser.getId();
                    if (TextUtils.isEmpty(userId)) {
                        Toast.makeText(v.getContext(), "用户编号未填写", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mUser.setUserId(userId);
                    if (TextUtils.isEmpty(userName)) {
                        Toast.makeText(v.getContext(), "用户姓名未填写", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mUser.setUserName(userName);
                    mUser.setUserAddress(address);
                    mUser.setUserPhone(phone);
                    long currentTimeMillis = System.currentTimeMillis();
                    mUser.setUpdateTime(currentTimeMillis);

                    mUser.setPowerMeterId(deviceId);
                    mUser.setMeterReadingId(positionId);
                    mUser.setPowerLineId(serialId);

                    String apiUrl = App.BASE_HOST + "/feehelper/user/add";
                    if (id == 0) {
                        mUser.setCreateTime(currentTimeMillis);
                    } else {
                        apiUrl = App.BASE_HOST + "/feehelper/user/update";
                    }
//                    OkGo.<BaseResponseBean>post(apiUrl)
//                            .upJson(new Gson().toJson(mUser))
//                            .execute(new AbsCallback<BaseResponseBean>() {
//                                @Override
//                                public void onSuccess(Response<BaseResponseBean> response) {
//                                    if (getActivity() != null) {
//                                        Toast.makeText(getActivity().getApplicationContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
//                                    }
//                                    if (mListener != null) {
//                                        mListener.onFragmentInteraction(Activity.RESULT_OK);
//                                    }
//                                    dismiss();
//                                }
//
//                                @Override
//                                public BaseResponseBean convertResponse(okhttp3.Response response) throws Throwable {
//                                    return new Gson().fromJson(response.body().string(), BaseResponseBean.class);
//                                }
//                            });

                }
                break;
            default:
                break;
        }
    }
}
