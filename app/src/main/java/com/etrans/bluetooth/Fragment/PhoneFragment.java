package com.etrans.bluetooth.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etrans.bluetooth.CallogActivity;
import com.etrans.bluetooth.ContactActivity;
import com.etrans.bluetooth.MainActivity;
import com.etrans.bluetooth.R;
import com.etrans.bluetooth.db.GocDatabase;


public class PhoneFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = PhoneFragment.class.getSimpleName();

    private MainActivity activity;
    private  View mview;
    ImageView mIvNum1;
    ImageView mIvNum2;
    ImageView mIvNum3;
    ImageView mIvNum4;
    ImageView mIvNum5;
    ImageView mIvNum6;
    ImageView mIvNum7;
    ImageView mIvNum8;
    ImageView mIvNum9;
    ImageView mIvNumX;
    ImageView mIvNum0;
    ImageView mIvNumJ;
    EditText mEtPhone;
    ImageView mIvClearNum,iv_contact,iv_ref_contact,iv_callog;
    Button mIvDial;



    public static Handler hand = null;

    public static Handler getHandler() {
        return hand;
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
//                case MSG_CURRENT_CONNECT_DEVICE_NAME:
//                    currentDeviceName = (String) msg.obj;
//                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载View，并返回
        mview = inflater.inflate(R.layout.phonefragment, null);
        activity = (MainActivity) getActivity();
        initView();
        initListener();
        hand = handler;

        return mview;
    }

    private void initView() {
        mIvNum0 = (ImageView) mview.findViewById(R.id.iv_num_0);
        mIvNum1 = (ImageView) mview.findViewById(R.id.iv_num_1);
        mIvNum2 = (ImageView) mview.findViewById(R.id.iv_num_2);
        mIvNum3 = (ImageView) mview.findViewById(R.id.iv_num_3);
        mIvNum4 = (ImageView) mview.findViewById(R.id.iv_num_4);
        mIvNum5 = (ImageView) mview.findViewById(R.id.iv_num_5);
        mIvNum6 = (ImageView) mview.findViewById(R.id.iv_num_6);
        mIvNum7 = (ImageView) mview.findViewById(R.id.iv_num_7);
        mIvNum8 = (ImageView) mview.findViewById(R.id.iv_num_8);
        mIvNum9 = (ImageView) mview.findViewById(R.id.iv_num_9);
        mIvNumX = (ImageView) mview.findViewById(R.id.iv_num_x);
        mIvNumJ = (ImageView) mview.findViewById(R.id.iv_num_j);
        mIvNumJ = (ImageView) mview.findViewById(R.id.iv_num_j);
        mEtPhone = (EditText) mview.findViewById(R.id.et_phone);
        mIvClearNum = (ImageView) mview.findViewById(R.id.iv_clear_num);
        iv_contact = (ImageView) mview.findViewById(R.id.iv_contact);
        iv_ref_contact = (ImageView) mview.findViewById(R.id.iv_ref_contact);
        iv_callog = (ImageView) mview.findViewById(R.id.iv_callog);
        mIvDial = (Button) mview.findViewById(R.id.iv_dial);


    }
    private void initListener() {
        mIvNum1.setOnClickListener(this);
        mIvNum2.setOnClickListener(this);
        mIvNum3.setOnClickListener(this);
        mIvNum4.setOnClickListener(this);
        mIvNum5.setOnClickListener(this);
        mIvNum6.setOnClickListener(this);
        mIvNum7.setOnClickListener(this);
        mIvNum8.setOnClickListener(this);
        mIvNum9.setOnClickListener(this);
        mIvNum0.setOnClickListener(this);
        mIvNumX.setOnClickListener(this);
        mIvNumJ.setOnClickListener(this);
        mIvDial.setOnClickListener(this);
        mIvClearNum.setOnClickListener(this);
        iv_contact.setOnClickListener(this);
        iv_ref_contact.setOnClickListener(this);
        iv_callog.setOnClickListener(this);
        mIvDial.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mIvDial.setBackgroundResource(R.drawable.dial_press);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mIvDial.setBackgroundResource(R.drawable.dial);
                }
                return false;
            }
        });
        mIvClearNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mEtPhone.getText().toString().length() > 0) {
                    mEtPhone.setText("");
//                    showQueryList(false);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iv_num_0:
                appendNum("0");
                break;
            case R.id.iv_num_1:
                appendNum("1");
                break;
            case R.id.iv_num_2:
                appendNum("2");
                break;
            case R.id.iv_num_3:
                appendNum("3");
                break;
            case R.id.iv_num_4:
                appendNum("4");
                break;
            case R.id.iv_num_5:
                appendNum("5");
                break;
            case R.id.iv_num_6:
                appendNum("6");
                break;
            case R.id.iv_num_7:
                appendNum("7");
                break;
            case R.id.iv_num_8:
                appendNum("8");
                break;
            case R.id.iv_num_9:
                appendNum("9");
                break;
            case R.id.iv_num_x:
                appendNum("*");
                break;
            case R.id.iv_num_j:
                appendNum("#");
                break;
            case R.id.iv_clear_num:
                delNum();
                break;
            case R.id.iv_contact://通讯录

                startActivity(new Intent(activity, ContactActivity.class));
                break;
            case R.id.iv_callog://通话记录

                startActivity(new Intent(activity, CallogActivity.class));
                break;
            case R.id.iv_ref_contact://更新通讯录
                try {
                    Handler mainActivityHandler = MainActivity.getHandler();
                    if (mainActivityHandler == null) {
                        return;
                    }
                    mainActivityHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_PHONEBOOK);

                    GocDatabase.getDefault().clearPhonebook();
                    // 联系人列表下载
                    MainActivity.getService().phoneBookStartUpdate();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.iv_dial:

//                if (GocsdkCallbackImp.hfpStatus >= 3) {
                String number = mEtPhone.getText().toString().trim();
//                    if (TextUtils.isEmpty(number)) {
//                        ToastFactory.showToast(getApplicationContext(), "请输入电话号码");
//                    } else {
                placeCall(number);
//                    }
//                } else {
//                    ToastFactory.showToast(getApplicationContext(), "请您先连接设备");
//                }

                break;
        }
    }

    private void appendNum(String num) {
        mEtPhone.append(num);
        Log.d(TAG, "appendNum() called with: " + "num = [" + mEtPhone.getText().toString() + "]");
    }
    private void delNum() {
        String num = mEtPhone.getText().toString();
        if (!TextUtils.isEmpty(num) && num.length() > 0) {
            String substring = num.substring(0, num.length() - 1);
            mEtPhone.setText(substring);
            mEtPhone.setSelection(substring.length());
        } else {
            mEtPhone.setText("");
//            showQueryList(false);
        }

    }

    // 拨打正确的电话
    private static void placeCall(String number) {
        if (number.length() == 0) return;
        if (MainActivity.getService() == null) return;

        if (PhoneNumberUtils.isGlobalPhoneNumber(number)) {
            Log.d("GocApp", "isGlobalPhoneNumber 1: " + number);
            if (number == null || !TextUtils.isGraphic(number)) {
                return;
            }
            try {
                MainActivity.getService().phoneDail(number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
