package com.etrans.bluetooth;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.etrans.bluetooth.app.Myapplication;

public class InComingActivity extends Activity implements OnClickListener {
    public static final int MSG_INCOMINNG_HANGUP = 0;
    public static final int MSG_INCOMING_CONNECTION = 1;

    private ImageView iv_connect;
    private ImageView iv_hangup;
    private TextView tv_incoming_phonenumber;
    private String incomingNumber;

    private static Handler hand = null;
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_INCOMINNG_HANGUP:
                    finish();
                    break;
                case MSG_INCOMING_CONNECTION:
                    if(incomingNumber!=null){
                        System.out.println("来电了，你到底接没接");
                        connectInComing(incomingNumber);
                    }
                    break;
            }
        };
    };

    public static Handler getHandler() {
        return hand;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming2);

        Intent intent = getIntent();
        incomingNumber = intent.getStringExtra("incomingNumber");
        tv_incoming_phonenumber = (TextView)findViewById(R.id.tv_incoming_phonenumber);
        tv_incoming_phonenumber.setText(incomingNumber);
        iv_connect = (ImageView) findViewById(R.id.iv_connect);
        iv_hangup = (ImageView) findViewById(R.id.iv_hangup);
        iv_connect.setOnClickListener(this);
        iv_hangup.setOnClickListener(this);
        hand = handler;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_connect:
                try {
                    Myapplication.getService().phoneAnswer();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_hangup:
                hangupInComing();
                break;

        }
    }

    private void hangupInComing() {

		try {
            Myapplication.getService().phoneHangUp();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
        finish();
    }

    private void connectInComing(String incomingNumber2) {
        try {
            Myapplication.getService().phoneAnswer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.etrans.bluetooth",
                "com.etrans.bluetooth.CallActivity"));
        intent.putExtra("incomingNumber", incomingNumber2);
        intent.putExtra("isConnect", true);
        startActivity(intent);
        finish();
    }



}
