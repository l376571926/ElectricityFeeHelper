package group.tonight.electricityfeehelper.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import group.tonight.electricityfeehelper.MainApp;
import group.tonight.electricityfeehelper.dao.OrderDao;
import group.tonight.electricityfeehelper.interfaces.OnFragmentInteractionListener;
import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.R;
import group.tonight.electricityfeehelper.utils.MyUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddOrderFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = AddOrderFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView mDateTv;
    private EditText mYingShouEt;
    private EditText mShiShouEt;
    private TextView mQianFeiTv;
    private double mYingShou;
    private double mShiShou;

    public AddOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddOrderFragment.
     */
    public static AddOrderFragment newInstance(String param1, String param2) {
        AddOrderFragment fragment = new AddOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_order, container, false);

        mDateTv = (TextView) rootView.findViewById(R.id.date);
        mYingShouEt = (EditText) rootView.findViewById(R.id.ying_shou);
        mShiShouEt = (EditText) rootView.findViewById(R.id.shi_shou);
        mQianFeiTv = (TextView) rootView.findViewById(R.id.qian_fei);

        rootView.findViewById(R.id.choose_date).setOnClickListener(this);
        rootView.findViewById(R.id.cancel).setOnClickListener(this);
        rootView.findViewById(R.id.ok).setOnClickListener(this);

        mYingShouEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (s.toString().startsWith(".")) {
                    return;
                }
                mYingShou = Double.parseDouble(s.toString());
                mQianFeiTv.setText(MyUtils.formatDecimal(mYingShou - mShiShou));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mShiShouEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (s.toString().startsWith(".")) {
                    return;
                }
                mShiShou = Double.parseDouble(s.toString());
                mQianFeiTv.setText(MyUtils.formatDecimal(mYingShou - mShiShou));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private long mSelectDateMills;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_date:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.e(TAG, "onDateSet: " + year + " " + month + " " + dayOfMonth);

                        Calendar instance = Calendar.getInstance();
                        instance.set(Calendar.YEAR, year);
                        instance.set(Calendar.MONTH, month);
                        instance.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        instance.set(Calendar.HOUR_OF_DAY, 0);
                        instance.set(Calendar.MINUTE, 0);
                        instance.set(Calendar.SECOND, 0);
                        instance.set(Calendar.MILLISECOND,0);

                        Date time = instance.getTime();
                        mSelectDateMills = time.getTime();
                        String formatDate = MyUtils.mDateFormat.format(time);
                        mDateTv.setText(formatDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.cancel:

                dismiss();
                break;
            case R.id.ok:
                String yingShou = mYingShouEt.getText().toString();
                if (TextUtils.isEmpty(yingShou)) {
                    return;
                }
                String shiShou = mShiShouEt.getText().toString();
                if (TextUtils.isEmpty(shiShou)) {
                    mShiShou = 0;
                }
                String qianFei = mQianFeiTv.getText().toString();
                if (TextUtils.isEmpty(qianFei)) {
                    return;
                }
                if (getActivity() != null) {
                    OrderDao orderDao = ((MainApp) getActivity().getApplication()).getDaoSession().getOrderDao();

                    Order order = new Order();
                    order.setUid(Long.parseLong(mParam1));
                    order.setYingShou(mYingShou);
                    order.setShiShou(mShiShou);
                    order.setQianFei(mYingShou - mShiShou);
                    order.setCreateTime(mSelectDateMills);

                    orderDao.insert(order);

                    if (mListener != null) {
                        mListener.onFragmentInteraction(Activity.RESULT_OK);
                    }
                }
                dismiss();
                break;
            default:
                break;
        }
    }
}
