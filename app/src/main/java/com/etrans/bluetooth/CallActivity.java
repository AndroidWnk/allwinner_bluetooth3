package com.etrans.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etrans.bluetooth.Goc.GocsdkCallbackImp;
import com.etrans.bluetooth.db.Database;
import com.etrans.bluetooth.utils.ToastFactory;

public class CallActivity extends Activity implements OnClickListener {
    public static final int MSG_CALL_STATUS = 0;// 拨出电话
    public static final int Msg_CONNECT = 1;// 接通
    public static final int MSG_INCOMING_HANGUP = 2;// 拒接

    ImageView mBtnAnswer;
    ImageView mBtnHangup;
    ImageView mBtnVolType;
    TextView mTvTitle;
    ImageView mIvBack;
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
    LinearLayout mLlDialNum;
    ImageView mIvShowDialNum;
    ImageView mIvDefIcon;
    EditText mEtDtmfCode;

    //Goc
    private TextView tv_call_people_name;
    private RelativeLayout rl_call_pager;
    private RelativeLayout rl_connect;
    private TextView tv_connection_info;
    private Chronometer chronometer;
    ///////////////////////////////


    private boolean misShowDail = false;//切换拨号键
    private boolean volume_flag = false;//切声道
    private long click;
    private boolean num = false;


    //Goc
    private String callNumber;//前台拨号
    private String incomingNumber;//来电号码
    private boolean isConnect = false;
    private static Handler hand = null;
    private Handler handler = new Handler() {
        private String phoneNumber;

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CALL_STATUS:
                    phoneNumber = (String) msg.obj;
                    callOut(phoneNumber);
                    break;
                case MSG_INCOMING_HANGUP:
                    System.out.println("handler------");
                    chronometer.stop();
                    finish();
                    break;
                case Msg_CONNECT:
                    System.out.println("callNumber你是不是为空啊？" + callNumber);
                    if (callNumber != null) {
                        System.out.println("我都拨出去了，你接了没有哇");
                        callConnect(callNumber);
                    } else {
                        Handler handler2 = InComingActivity.getHandler();
                        if (handler2 != null) {
                            handler2.sendEmptyMessage(InComingActivity.MSG_INCOMING_CONNECTION);
                        }
                    }
                    break;
            }
        }

        ;
    };

    public static Handler getHandler() {
        return hand;
    }
    //////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call2);


        initView();
        initOnclickListener();
        initData();//加载数据
        isConnect();
        hand = handler;
    }


    private void initView() {
        mBtnAnswer = (ImageView) findViewById(R.id.iv_answer);
        mBtnHangup = (ImageView) findViewById(R.id.iv_hangup);
        mBtnVolType = (ImageView) findViewById(R.id.iv_vol_type);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvNum1 = (ImageView) findViewById(R.id.iv_num_1);
        mIvNum2 = (ImageView) findViewById(R.id.iv_num_2);
        mIvNum3 = (ImageView) findViewById(R.id.iv_num_3);
        mIvNum4 = (ImageView) findViewById(R.id.iv_num_4);
        mIvNum5 = (ImageView) findViewById(R.id.iv_num_5);
        mIvNum6 = (ImageView) findViewById(R.id.iv_num_6);
        mIvNum7 = (ImageView) findViewById(R.id.iv_num_7);
        mIvNum8 = (ImageView) findViewById(R.id.iv_num_8);
        mIvNum9 = (ImageView) findViewById(R.id.iv_num_9);
        mIvNum0 = (ImageView) findViewById(R.id.iv_num_0);
        mIvNumX = (ImageView) findViewById(R.id.iv_num_x);
        mIvNumJ = (ImageView) findViewById(R.id.iv_num_j);
        mLlDialNum = (LinearLayout) findViewById(R.id.ll_dial_num);
        mIvShowDialNum = (ImageView) findViewById(R.id.iv_show_dial_num);
        mIvDefIcon = (ImageView) findViewById(R.id.img_def_icon);
        mEtDtmfCode = (EditText) findViewById(R.id.et_dtmf_code);
        tv_call_people_name = (TextView) findViewById(R.id.tv_call_people_name);
        rl_call_pager = (RelativeLayout) findViewById(R.id.rl_call_pager);
        rl_connect = (RelativeLayout) findViewById(R.id.rl_connect);
        tv_connection_info = (TextView) findViewById(R.id.tv_connection_info);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        mLlDialNum.setVisibility(View.GONE);
        mIvBack.setVisibility(View.INVISIBLE);

    }

    private void initOnclickListener() {
        mBtnAnswer.setOnClickListener(this);
        mBtnHangup.setOnClickListener(this);
        mBtnVolType.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
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
        mIvShowDialNum.setOnClickListener(this);
        mEtDtmfCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//				proxy.sendDTMFCode(s, start);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void initData() {
        mTvTitle.setText(getString(R.string.bt_title));
        Intent intent = getIntent();
        callNumber = intent.getStringExtra("callNumber");
        System.out.println("CallActivity中拨出电话号" + callNumber);
        incomingNumber = intent.getStringExtra("incomingNumber");
        //calloutNumber = intent.getStringExtra("calloutNumber");
        isConnect = intent.getBooleanExtra("isConnect", false);
        if (!TextUtils.isEmpty(callNumber)) {
            SQLiteDatabase database = Database.getSystemDb();
            Database.createTable(database, Database.Sql_create_phonebook_tab);
            String phoneName = Database.queryPhoneName(database,
                    Database.PhoneBookTable, callNumber);
            if (TextUtils.isEmpty(phoneName)) {
                tv_call_people_name.setText(callNumber);
                System.out.println("没有名字的号码");
            } else {
                tv_call_people_name.setText(phoneName);
            }
            database.close();
        }
    }

    private void isConnect() {
        if (isConnect) {
            rl_call_pager.setVisibility(View.GONE);
            rl_connect.setVisibility(View.VISIBLE);
            if (callNumber != null) {
                tv_connection_info.setText(callNumber);
            }
            if (incomingNumber != null) {
                tv_connection_info.setText(incomingNumber);
            }
            chronometer.setFormat("%s");
            chronometer.setBase(SystemClock.elapsedRealtime());// 复位键
            chronometer.start();

        } else {
            rl_call_pager.setVisibility(View.VISIBLE);
            rl_connect.setVisibility(View.GONE);
            if (callNumber != null) {
                tv_call_people_name.setText(callNumber);
            }
			/*if(calloutNumber!=null){
				tv_call_people_name.setText(calloutNumber);
				callNumber = null;
			}	*/
        }
    }

    protected void callConnect(String callNumber2) {
        rl_call_pager.setVisibility(View.GONE);
        rl_connect.setVisibility(View.VISIBLE);

        tv_connection_info.setText(callNumber2);

        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());// 复位键
        chronometer.start();
    }

    protected void callOut(String phoneNumber) {
        placeCall(phoneNumber);
        rl_call_pager.setVisibility(View.VISIBLE);
        rl_connect.setVisibility(View.GONE);
        tv_call_people_name.setText(phoneNumber);
    }

    // 拨打正确的电话
    private static void placeCall(String mLastNumber) {
        if (mLastNumber.length() == 0)
            return;
        if (PhoneNumberUtils.isGlobalPhoneNumber(mLastNumber)) {
            if (mLastNumber == null || !TextUtils.isGraphic(mLastNumber)) {
                return;
            }
            try {
                MainActivity.getService().phoneDail(mLastNumber);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_show_dial_num:
                misShowDail = !misShowDail;
                showDial(misShowDail);
                break;
            case R.id.iv_hangup:
                hangUp();
                break;
            case R.id.iv_vol_type:
                if (GocsdkCallbackImp.hfpStatus > 0) {
                    switchCarAndphone();
                } else {
                    Toast.makeText(this, "请您先连接设备", Toast.LENGTH_SHORT).show();
                }
                break;
//			case R.id.iv_bujingyin:
//				switchMute();
//				break;
            case R.id.iv_num_1:
                break;
            case R.id.iv_num_2:
                break;
            case R.id.iv_num_3:
                break;
            case R.id.iv_num_4:
                break;
            case R.id.iv_num_5:
                break;
            case R.id.iv_num_6:
                break;
            case R.id.iv_num_7:
                break;
            case R.id.iv_num_8:
                break;
            case R.id.iv_num_9:
                break;
            case R.id.iv_num_x:
                break;
            case R.id.iv_num_0:
                break;
            case R.id.iv_num_j:
                break;
        }
    }

    // 挂断
    private void hangUp() {

        //Toast.makeText(this, "挂断", 0).show();
        try {
            MainActivity.getService().phoneHangUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // 切换声音在车机端与手机端
    private void switchCarAndphone() {
        volume_flag = !volume_flag;
        if (volume_flag) {// 手机端
            try {
                MainActivity.getService().phoneTransfer();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            ToastFactory.showToast(this, "手机端");
        } else {// 车机端
            try {
                MainActivity.getService().phoneTransferBack();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            ToastFactory.showToast(this, "车机端");
        }
    }

    void showDial(boolean show) {
        mIvShowDialNum.setBackgroundResource(show ? R.drawable.ic_call_close_dial : R.drawable.ic_call_show_dial);
        mLlDialNum.setVisibility(show ? View.VISIBLE : View.GONE);
        mEtDtmfCode.setVisibility(show ? View.VISIBLE : View.GONE);
        mIvDefIcon.setVisibility(show ? View.GONE : View.VISIBLE);
//		mTvCallPhoneNum.setVisibility(show ? View.GONE : View.VISIBLE);
//		mTvCallState.setVisibility(show ? View.GONE : View.VISIBLE);
//		mTvCallTime.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
