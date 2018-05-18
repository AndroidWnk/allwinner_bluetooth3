package com.etrans.bluetooth;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.etrans.bluetooth.Fragment.MusicFragment;
import com.etrans.bluetooth.Fragment.PhoneFragment;
import com.etrans.bluetooth.Goc.BootReceiver;
import com.etrans.bluetooth.Goc.GocsdkCallbackImp;
import com.etrans.bluetooth.Goc.GocsdkService;
import com.etrans.bluetooth.Goc.PlayerService;
import com.etrans.bluetooth.bean.Phonebook;
import com.etrans.bluetooth.db.Database;
import com.etrans.bluetooth.domain.ContactInfos;
import com.goodocom.gocsdk.IGocsdkService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{
    //goc///////////////////////////////////////////Service
    public static final int MSG_DEVICENAME = 11;
    public static final int MSG_DEVICEPINCODE = 12;
    public static final int MSG_CURRENT_CONNECT_DEVICE_NAME = 29;//获取设备名称
    public static final int MSG_UPDATE_MISSED_CALLLOG = 27;
    public static final int MSG_UPDATE_PHONEBOOK = 17;
    public static final int MSG_UPDATE_CALLOUT_CALLLOG = 26;
    public static final int MSG_UPDATE_INCOMING_CALLLOG = 25;
    public static final int MSG_SET_MICPHONE_OFF = 20;
    public static final int MSG_SET_MICPHONE_ON = 19;
    public static final int MSG_UPDATE_PHONEBOOK_DONE = 18;
    public static String mLocalName = null;
    public static String mPinCode = null;
    public static String currentDeviceName = "";
    private Intent gocsdkService;
    private MyConn conn;
    public static GocsdkCallbackImp callback;
    //    private AudioManager mAudioManager;
    private static IGocsdkService iGocsdkService;
    private BootReceiver receiver;
    private static Handler hand = null;
    public static Handler getHandler() {
        return hand;
    }
    // 暴露方法，让其他页面能够获取主页面的参数
    public static IGocsdkService getService() {
        return iGocsdkService;
    }
    public boolean isConnected() {
        return iGocsdkService != null;
    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case MSG_DEVICENAME:// 蓝牙设备名称
                    String name = (String) msg.obj;
                    mLocalName = name;
                    break;
                case MSG_DEVICEPINCODE:// 蓝牙设备的PIN码
                    String pincode = (String) msg.obj;
                    mPinCode = pincode;
                    break;
                case MSG_CURRENT_CONNECT_DEVICE_NAME:
                    currentDeviceName = (String) msg.obj;
                    tv_download_count.setText(currentDeviceName);
                    break;
                case MSG_PHONE_BOOK:

                    Log.i("stateNK","OK");

                    Phonebook phonebook = (Phonebook) msg.obj;
                    Map<String, String> phoBook = new HashMap<String, String>();
                    phoBook.put("itemName", phonebook.name);
                    phoBook.put("itemNum", phonebook.num);
                    contacts.add(phoBook);
                    // 更改联系人个数
                    tv_download_count.setText("正在更新联系人： " + contacts.size());
//                    simpleAdapter.notifyDataSetChanged();
                    if (systemDb != null) {
                        Database.createTable(systemDb, Database.Sql_create_phonebook_tab);
                        Database.insert_phonebook(systemDb,
                                Database.PhoneBookTable, phonebook.name,
                                phonebook.num);
                    }

//                    GocDatabase.getDefault().insertPhonebook(phonebook.name, phonebook.num);
                    break;
                case MSG_PHONE_BOOK_DONE:
                    Log.i("stateNK","OK");//电话本下载完成
                    try {
                        MainActivity.getService().getCurrentDeviceName();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    tv_download_count.setText("下载完成");
                    showConnected();
                    break;
                case MainActivity.MSG_UPDATE_PHONEBOOK://准备更新联系人
                    // 判断联系人列表是否为空，不为空时清空它。
                    if (contacts.isEmpty() == false) {
                        contacts.clear();
                    }
                    break;

            }
        };
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public final static int MSG_PHONE_BOOK = 1;// 更新联系人
    public final static int MSG_PHONE_BOOK_DONE = 2;// 更新联系人结束

    //goc
    private ViewPager vpPager;
    private FragmentPagerAdapter adapter;
    ImageView mIvBack;
    private ImageView mSelect1;
    private ImageView mSelect2;
    private TextView tv_download_count;
    private List<Map<String, String>> contacts = new ArrayList<Map<String, String>>();
    private SQLiteDatabase systemDb;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //goc///////////////////////////////////////////////////Service
        // 注册开机广播接收者
        myRegisterReceiver();
        gocsdkService = new Intent(this, GocsdkService.class);
        stopService(gocsdkService);
        conn = new MyConn();
        bindService(gocsdkService, conn, BIND_AUTO_CREATE);
        // 开启播放服务
        Intent playerService = new Intent(this, PlayerService.class);
        startService(playerService);
        callback = new GocsdkCallbackImp();
        hand = handler;
////////////////////////////////////////////////////////////////////////////////////////////////
        InitData();
        initView();
        initListener();

        if (systemDb != null) {
            Database.createTable(systemDb, Database.Sql_create_phonebook_tab);
            Database.insert_phonebook(systemDb,
                    Database.PhoneBookTable, "张三",
                    "10086");
            Database.createTable(systemDb, Database.Sql_create_phonebook_tab);
            Database.insert_phonebook(systemDb,
                    Database.PhoneBookTable, "李四",
                    "10010");
        }


    }

    private void InitData() {
        systemDb = Database.getSystemDb();
        if (GocsdkCallbackImp.hfpStatus > 0) {
            reflashContactsData();
        } else {
            List<ContactInfos> contactInfos = Database.queryAllContacts(systemDb);
            Map<String, String> map = null;
            for (int i = 0; i < contactInfos.size(); i++) {
                ContactInfos contactInfo = contactInfos.get(i);
                map = new HashMap<String, String>();
                map.put("itemName", contactInfo.name);
                map.put("itemNum", contactInfo.num);
                contacts.add(map);
            }
        }
    }

    private void reflashContactsData() {
        try {
            Handler mainActivityHandler = MainActivity.getHandler();
            if (mainActivityHandler == null) {
                return;
            }
            mainActivityHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_PHONEBOOK);
            // 判断联系人列表是否为空，不为空时清空它。
            if (contacts.isEmpty() == false) {
                contacts.clear();
            }
            // 联系人列表下载
            MainActivity.getService().phoneBookStartUpdate();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        vpPager = (ViewPager) findViewById(R.id.vp_pager);
        adapter = new InnerFragmentPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapter);
        vpPager.addOnPageChangeListener(this);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        //选择页的点
        mSelect1 = (ImageView) findViewById(R.id.iv_select_1);
        mSelect2 = (ImageView) findViewById(R.id.iv_select_2);
        tv_download_count = (TextView) findViewById(R.id.tv_download_count);
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        tv_download_count.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_download_count:
                try {
                    MainActivity.getService().getCurrentDeviceName();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            mSelect1.setImageResource(R.drawable.around_slip_cur);
            mSelect2.setImageResource(R.drawable.around_slip_other);
        } else {
            mSelect1.setImageResource(R.drawable.around_slip_other);
            mSelect2.setImageResource(R.drawable.around_slip_cur);

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class InnerFragmentPagerAdapter extends FragmentPagerAdapter {

        public InnerFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new PhoneFragment();
                    break;
                case 1:
                    frag = new MusicFragment();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }
    private void showConnected() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (MainActivity.currentDeviceName != null) {
                    tv_download_count.setText(MainActivity.currentDeviceName);
                }
            }
        }, 200);

    }


    //Goc////////////////////////////////////////////////////////////////Service
    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            iGocsdkService = IGocsdkService.Stub.asInterface(service);
            // 蓝牙回调注册
            // 查询当前HFP状态
            try {
                iGocsdkService.registerCallback(callback);

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            iGocsdkService.inqueryHfpStatus();
                            iGocsdkService.musicUnmute();
                            iGocsdkService.getLocalName();
                            iGocsdkService.getPinCode();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void myRegisterReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BOOT_COMPLETED");
        receiver = new BootReceiver();
        registerReceiver(receiver, filter);
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Goc///////////////////////////////////////////////////Service

        // 注销蓝牙回调
        try {
            iGocsdkService.unregisterCallback(callback);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // 注销开机广播
        unregisterReceiver(receiver);
        // 解绑服务
        unbindService(conn);
        startService(gocsdkService);

        ///////////////////////////////////////////////////////////////////

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
