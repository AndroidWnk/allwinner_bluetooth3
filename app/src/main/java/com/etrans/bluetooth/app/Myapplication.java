package com.etrans.bluetooth.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.etrans.bluetooth.CallActivity;
import com.etrans.bluetooth.Goc.BlueToothInfo;
import com.etrans.bluetooth.InComingActivity;
import com.etrans.bluetooth.MainActivity;
import com.etrans.bluetooth.db.Database;

import java.util.ArrayList;
import java.util.List;

public class Myapplication extends Application{
    private static Context context;
    public static String currentDeviceName = "";
    public static String mComingPhoneNum = null; // 来电号码
    public static boolean isInComing = false;
    private List<BlueToothInfo> bts = new ArrayList<BlueToothInfo>();
    private static Handler hand = null;
    public static Handler getHandler() {
        return hand;
    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_COMING:// 来电
                    isInComing = true;
                    String phonenum = (String) msg.obj;
                    String phonename = "";
                    mComingPhoneNum = phonenum;
                    SQLiteDatabase mDbDataBase = Database.getSystemDb();
                    Database.createTable(mDbDataBase, Database.Sql_create_phonebook_tab);
                    phonename = Database.queryPhoneName(mDbDataBase,
                            Database.PhoneBookTable, phonenum);// 根据号码查询联系人
                    Intent intent = new Intent(context, InComingActivity.class);
                    if (TextUtils.isEmpty(phonename)) {
                        intent.putExtra("incomingNumber", phonenum);
                    } else {
                        intent.putExtra("incomingNumber", phonename);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(intent);
                    break;
                case MSG_TALKING:// 接听
                    if (isInComing) {// 来电接听
                        Handler handler = InComingActivity.getHandler();
                        if (handler == null) {
                            return;
                        }
                        handler.sendEmptyMessage(InComingActivity.MSG_INCOMING_CONNECTION);
                    } else {// 拨出接听
                        Handler handler = CallActivity.getHandler();
                        if (handler == null) {
                            return;
                        }
                        System.out.println("命令来了我就发送");
                        handler.sendEmptyMessage(CallActivity.Msg_CONNECT);
                    }
                    break;
                case MSG_OUTGONG:// 拨出
                    isInComing = false;
                    String call_number = (String) msg.obj;
                    System.out.println("MainAcitivity中拨出的电话" + call_number);
                    callOut(call_number);
                    break;


            }

        }

    };


    private void callOut(String phoneNumber2) {
        placeCall(phoneNumber2);
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("callNumber", phoneNumber2);
        intent.putExtra("isConnect", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }

    // 拨打正确的电话
    private static void placeCall(String mLastNumber) {
        if (mLastNumber.length() == 0)
            return;
        if (PhoneNumberUtils.isGlobalPhoneNumber(mLastNumber)) {
            // place the call if it is a valid number
            if (mLastNumber == null || !TextUtils.isGraphic(mLastNumber)) {
                // There is no number entered.
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
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext() ;

        hand = handler;
    }

    //goc

    public static final int MSG_OUTGONG = 5;//拨出
    public static final int MSG_COMING = 4;//来电
    public static final int MSG_TALKING = 6;//接听
}
