package com.etrans.bluetooth.Goc;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.etrans.bluetooth.CallActivity;
import com.etrans.bluetooth.CallogActivity;
import com.etrans.bluetooth.Fragment.PairedListinfoFg;
import com.etrans.bluetooth.Fragment.SearchInfoFg;
import com.etrans.bluetooth.Fragment.SettingInfoFg;
import com.etrans.bluetooth.InComingActivity;
import com.etrans.bluetooth.MainActivity;
import com.etrans.bluetooth.app.Myapplication;
import com.etrans.bluetooth.bean.Phonebook;
import com.etrans.bluetooth.domain.CallLogInfo;
import com.etrans.bluetooth.event.A2dpStatusEvent;
import com.etrans.bluetooth.event.AutoConnectAcceptEvent;
import com.etrans.bluetooth.event.MusicInfoEvent;
import com.goodocom.gocsdk.IGocsdkCallback;

import org.greenrobot.eventbus.EventBus;

public class GocsdkCallbackImp extends IGocsdkCallback.Stub {
	public static String number = "";
	public static int hfpStatus = 0;
	private static int callType = 0;
	public static int a2dpStatus = 1;

	public class BtDevices {
		public String name = null;
		public String addr = null;
	}

	public class callLog {
		public int callType = 0;
		public String num = null;
	}

	@Override
	public void onHfpConnected() throws RemoteException {
		Log.i("stateNK","OK");
		Handler handler2 = PairedListinfoFg.getHandler();
		if(handler2!=null){
			handler2.sendEmptyMessage(PairedListinfoFg.MSG_CONNECT_SUCCESS);
		}
		Handler handler = SearchInfoFg.getHandler();
		if(handler == null)
			return;
		handler.sendEmptyMessage(SearchInfoFg.MSG_CONNECT_SUCCESS);
		GocsdkCallbackImp.hfpStatus = 1;
		Log.i("stateNK_hfpStatus","1");
	}

	@Override
	public void onHfpDisconnected() throws RemoteException {
		Log.i("stateNK","OK");
		Handler handler2 = PairedListinfoFg.getHandler();
		if(handler2!=null){
			handler2.sendEmptyMessage(PairedListinfoFg.MSG_CONNECT_FAILE);
		}
		Handler handler = SearchInfoFg.getHandler();
		if(handler == null)
			return;
		handler.sendEmptyMessage(SearchInfoFg.MSG_CONNECT_FAILE);
		GocsdkCallbackImp.hfpStatus = 0;
		Log.i("stateNK_hfpStatus","0");
	}

	@Override
	public void onCallSucceed(String number) throws RemoteException {
		GocsdkCallbackImp.hfpStatus = 5;
		Log.i("stateNK_hfpStatus","5");
	}

	@Override
	public void onIncoming(String number) throws RemoteException {
		
		Handler handler1 = Myapplication.getHandler();
		handler1.sendMessage(handler1.obtainMessage(Myapplication.MSG_COMING, number));
		GocsdkCallbackImp.number = number;
		GocsdkCallbackImp.hfpStatus = 4;
		Log.i("stateNK_hfpStatus","4");

	}

	@Override
	public void onHangUp() throws RemoteException {

		Log.i("stateNKGocsdkCallback","onHangUp");


		Handler handler2 = InComingActivity.getHandler();
		if(handler2!=null){
			handler2.sendEmptyMessage(InComingActivity.MSG_INCOMINNG_HANGUP);
		}
		Handler handler = CallActivity.getHandler();
		if(handler == null){
			return;
		}
		handler.sendEmptyMessage(CallActivity.MSG_INCOMING_HANGUP);
		GocsdkCallbackImp.hfpStatus = 7;
		Log.i("stateNK_hfpStatus","7");
	}

	@Override
	public void onTalking(String str) throws RemoteException {
		System.out.println("接通了");
		Handler handler = Myapplication.getHandler();
		if(handler==null){
			return;
		}
		handler.sendEmptyMessage(Myapplication.MSG_TALKING);
		GocsdkCallbackImp.hfpStatus = 6;
		Log.i("stateNK_hfpStatus","6");

	}

	@Override
	public void onRingStart() throws RemoteException {
	}

	@Override
	public void onRingStop() throws RemoteException {
	}

	@Override
	public void onHfpLocal() throws RemoteException {
	}

	@Override
	public void onHfpRemote() throws RemoteException {
	}

	@Override
	public void onInPairMode() throws RemoteException {
	}

	@Override
	public void onExitPairMode() throws RemoteException {
	}

	@Override
	public void onInitSucceed() throws RemoteException {
	}

	@Override
	public void onMusicPlaying() throws RemoteException {/*

		Handler handler = MainActivity.getHandler();
		if (null == handler)
			return;
		handler.sendEmptyMessage(MainActivity.MSG_MUSIC_PLAY);

		Handler musicHandler = FragmentMusic.getHandler();
		Message msg_music = new Message();
		msg_music.what = FragmentMusic.MSG_FRAGMENT_MUSIC_PLAY;
		if (musicHandler == null) {
			return;
		}
		musicHandler.sendMessage(msg_music);
		EventBus.getDefault().postSticky(new PlayStatusEvent(true));
	*/}

	@Override
	public void onMusicStopped() throws RemoteException {/*
		Handler handler = MainActivity.getHandler();
		if (null == handler)
			return;
		handler.sendEmptyMessage(MainActivity.MSG_MUSIC_STOP);

		Handler musicHandler = FragmentMusic.getHandler();
		Message msg_music = new Message();
		msg_music.what = FragmentMusic.MSG_FRAGMENT_MUSIC_PAUSE;
		if (musicHandler == null) {
			return;
		}
		musicHandler.sendMessage(msg_music);
		EventBus.getDefault().postSticky(new PlayStatusEvent(false));
	*/}

	@Override
	public void onAutoConnectAccept(boolean autoConnect, boolean autoAccept)
			throws RemoteException {
		Log.i("stateNK","autoConnect="+autoConnect+"autoAccept="+autoAccept);

		EventBus.getDefault().post(new AutoConnectAcceptEvent(autoConnect,autoAccept));
	}

	@Override
	public void onCurrentAddr(String addr) throws RemoteException {
		Handler handler2 = PairedListinfoFg.getHandler();
		if(handler2!=null){
			Message msg = new Message();
			msg.what = PairedListinfoFg.MSG_CONNECT_ADDRESS;
			msg.obj = addr;
			handler2.sendMessage(msg);
		}
		Handler handler3 = SearchInfoFg.getHandler();
		if(handler3!=null){
			Message msg = new Message();
			msg.what = SearchInfoFg.MSG_CONNECT_ADDRESS;
			msg.obj = addr;
			handler3.sendMessage(msg);
		}
//		Handler handler = FragmentMailList.getHandler();
//		if(handler == null){
//			return;
//		}
//		Message msg = new Message();
//		msg.what = FragmentMailList.MSG_CURRENT_DEVICE_ADDRESS;
//		msg.obj = addr;
//		handler.sendMessage(msg);
	}

	@Override
	public void onCurrentName(String name) throws RemoteException {
		Handler handler = MainActivity.getHandler();

		if(handler!=null){
			Message msg = Message.obtain();
			msg.what = MainActivity.MSG_CURRENT_CONNECT_DEVICE_NAME;
			msg.obj = name;
			handler.sendMessage(msg);
		}
//		Handler handler1 = MainActivity.getHandler();
//		if(handler1!=null){
//			Message msg = Message.obtain();
//			msg.what = MainActivity.MSG_CURRENT_CONNECT_DEVICE_NAME;
//			msg.obj = name;
//			handler1.sendMessage(msg);
//		}





	}

	// 1:未连接 3:已连接 4：电话拨出 5：电话打入 6：通话中
	/*
	 * 0~初始化 1~待机状态 2~连接中 3~连接成功 4~电话拨出 5~电话打入 6~通话中
	 */
	@Override
	public void onHfpStatus(int status) throws RemoteException {
		switch (status) {
			case 0:
				hfpStatus = 0;
				Log.i("stateNK_hfpStatus","0");
				break;
			case 1:
				hfpStatus = 0;
				Log.i("stateNK_hfpStatus","0");
				break;
			case 2:
				hfpStatus = 0;
				Log.i("stateNK_hfpStatus","0");
				break;
			case 3:
				hfpStatus = 1;
				Log.i("stateNK_hfpStatus","1");
				break;
			case 4:
				hfpStatus = 5;
				Log.i("stateNK_hfpStatus","5");
				break;
			case 5:
				hfpStatus = 4;
				Log.i("stateNK_hfpStatus","4");
				break;
			case 6:
				hfpStatus = 6;
				Log.i("stateNK_hfpStatus","6");
				break;
		}
	}

	@Override
	public void onAvStatus(int status) throws RemoteException {

		a2dpStatus = status;

		EventBus.getDefault().post(new A2dpStatusEvent(status));
	}

	@Override
	public void onVersionDate(String version) throws RemoteException {
	}

	@Override
	public void onCurrentDeviceName(String name) throws RemoteException {
		Handler handler = SettingInfoFg.getHandler();
		if(handler==null){
			return;
		}
		Message msg = new Message();
		msg.what = SettingInfoFg.MSG_DEVICE_NAME;
		msg.obj = name;
		handler.sendMessage(msg);
	}

	@Override
	public void onCurrentPinCode(String code) throws RemoteException {
		Handler handler = SettingInfoFg.getHandler();
		if(handler==null){
			return;
		}
		Message msg = new Message();
		msg.what = SettingInfoFg.MSG_PIN_CODE;
		msg.obj = code;
		handler.sendMessage(msg);
	}

	@Override
	public void onA2dpConnected() throws RemoteException {
	}
	//配对列表
	@Override
	public void onCurrentAndPairList(int index, String name, String addr)
			throws RemoteException {
		Handler handler = PairedListinfoFg.getHandler();
		if(handler == null){
			return;
		}
		BlueToothPairedInfo info = new BlueToothPairedInfo();
		info.index = index;
		info.name = name;
		info.address = addr;
		Message msg = new Message();
		msg.obj = info;
		msg.what = PairedListinfoFg.MSG_PAIRED_DEVICE;
		handler.sendMessage(msg);
	}

	@Override
	public void onA2dpDisconnected() throws RemoteException {
	}

	@Override
	public void onPhoneBook(String name, String number) throws RemoteException {
		Handler handler = MainActivity.getHandler();
		if (handler == null) {
			return;
		}
		Message msg = Message.obtain();
		msg.what = MainActivity.MSG_PHONE_BOOK;
		Phonebook phonebook = new Phonebook();
		phonebook.name = name;
		phonebook.num = number;
		msg.obj = phonebook;
		handler.sendMessage(msg);
	}

	@Override
	public void onPhoneBookDone() throws RemoteException {
		Handler mainActivityHandler = MainActivity.getHandler();
		if (mainActivityHandler == null)
			return;
		mainActivityHandler
				.sendEmptyMessage(MainActivity.MSG_UPDATE_PHONEBOOK_DONE);

		Handler handler = MainActivity.getHandler();
		if (handler == null) {
			return;
		}
		Message msg = Message.obtain();
		msg.what = MainActivity.MSG_PHONE_BOOK_DONE;
		handler.sendMessage(msg);
	}

	@Override
	public void onSimBook(String name, String number) throws RemoteException {

	}

	@Override
	public void onSimDone() throws RemoteException {

	}

	@Override
	public void onCalllog(int type, String name, String number)
			throws RemoteException {
		Handler handler = CallogActivity.getHandler();
		if (handler == null) {
			return;
		}
		CallLogInfo info = new CallLogInfo();
		info.number = number;
		info.type = type;
		info.name = name;
		Message msg = new Message();
		msg.obj = info;
		msg.what = CallogActivity.MSG_CALLLOG;
		handler.sendMessage(msg);
//		Handler handler = FragmentCallog.getHandler();
//		if (handler == null) {
//			return;
//		}
//		CallLogInfo info = new CallLogInfo();
//		info.phonenumber = number;
//		info.calltype = type;
//		info.phonename = name;
//		Message msg = new Message();
//		msg.obj = info;
//		msg.what = FragmentCallog.MSG_CALLLOG;
//		handler.sendMessage(msg);
	}

	@Override
	public void onCalllogDone() throws RemoteException {
//		Handler mainHandler = MainActivity.getHandler();
//		mainHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_CALLLOG_DONE);
//
//		Handler handler = FragmentCallog.getHandler();
//		if (handler == null) {
//			return;
//		}
//		Message msg = new Message();
//		msg.what = FragmentCallog.MSG_CALLLOG_DONE;
//		handler.sendMessage(msg);
	}

	@Override
	public void onDiscovery(String name, String addr) throws RemoteException {
		Handler handler = SearchInfoFg.getHandler();
		Message msg = new Message();
		msg.what = SearchInfoFg.MSG_SEARCHE_DEVICE;
		BlueToothInfo info = new BlueToothInfo();
		info.name = name;
		info.address = addr;
		msg.obj = info;
		if (handler == null) {
			return;
		}
		handler.sendMessage(msg);
	}

	@Override
	public void onDiscoveryDone() throws RemoteException {
		Handler handler = SearchInfoFg.getHandler();
		if (handler == null) {
			return;
		}
		handler.sendEmptyMessage(SearchInfoFg.MSG_SEARCHE_DEVICE_DONE);
	}

	@Override
	public void onLocalAddress(String addr) throws RemoteException {
	}

	//得到拨出或者通话中的号码
	@Override
	public void onOutGoingOrTalkingNumber(String number) throws RemoteException {

		Handler handler = Myapplication.getHandler();
		Message msg = new Message();
		msg.obj = number;
		System.out.println("onCallSucceed---" + number);
		msg.what = Myapplication.MSG_OUTGONG;
		handler.sendMessage(msg);
	}

	@Override
	public void onConnecting() throws RemoteException {
	}

	@Override
	public void onSppData(int index, String data) throws RemoteException {
	}

	@Override
	public void onSppConnect(int index) throws RemoteException {
	}

	@Override
	public void onSppDisconnect(int index) throws RemoteException {
	}

	@Override
	public void onSppStatus(int status) throws RemoteException {
	}

	@Override
	public void onOppReceivedFile(String path) throws RemoteException {
	}

	@Override
	public void onOppPushSuccess() throws RemoteException {
	}

	@Override
	public void onOppPushFailed() throws RemoteException {
	}

	@Override
	public void onHidConnected() throws RemoteException {
	}

	@Override
	public void onHidDisconnected() throws RemoteException {
	}

	@Override
	public void onHidStatus(int status) throws RemoteException {
	}

	@Override
	public void onMusicInfo(String name, String artist, int duration, int pos,
                            int total) throws RemoteException {
		Log.i("stateNK_GocsdkCallback","歌名="+name+",歌手="+artist);
		EventBus.getDefault().post(
				new MusicInfoEvent(name, artist, duration, pos, total));
	}

	@Override
	public void onPanConnect() throws RemoteException {
	}

	@Override
	public void onPanDisconnect() throws RemoteException {
	}

	@Override
	public void onPanStatus(int status) throws RemoteException {

	}

	@Override
	public void onVoiceConnected() throws RemoteException {
	}

	@Override
	public void onVoiceDisconnected() throws RemoteException {

	}

	@Override
	public void onProfileEnbled(boolean[] enabled) throws RemoteException {
	}

	@Override
	public void onMessageInfo(String content_order, String read_status,
                              String time, String name, String num, String title)
			throws RemoteException {/*
		EventBus.getDefault().post(
				new MessageListEvent(content_order,
						read_status.equals("1") ? true : false, time, name,
						num, title));
	*/}

	@Override
	public void onMessageContent(String content) throws RemoteException {
		//EventBus.getDefault().post(new MessageTextEvent(content));
	}

}
