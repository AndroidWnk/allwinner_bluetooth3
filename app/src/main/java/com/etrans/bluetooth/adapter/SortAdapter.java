package com.etrans.bluetooth.adapter;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.etrans.bluetooth.R;
import com.etrans.bluetooth.app.Myapplication;
import com.etrans.bluetooth.domain.ContactInfos;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private List<ContactInfos> list = null;
	private Context mContext;
	
	public SortAdapter(Context mContext, List<ContactInfos> list) {
		this.mContext = mContext;
		this.list = list;
	}
	

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<ContactInfos> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final ContactInfos mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_sort_listview, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.contact_name);
			viewHolder.contact_telephone = (TextView) view.findViewById(R.id.contact_telephone);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.mBtnDial = (ImageView) view.findViewById(R.id.btn_dial);//拨号
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
				int section = getSectionForPosition(position);
				
				//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
	
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		viewHolder.contact_telephone.setText(this.list.get(position).getNum());
		final int pos = position;
		viewHolder.mBtnDial.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pos >= 0 && pos < list.size()) {
					String phoneNumber = list.get(pos).getNum();
					callOut(phoneNumber);
//					ToastFactory.getToast(mContext, "拨打的号码是："+phoneNumber).show();
//					if(XxBluetoothPhoneManager.getInstance().getState() == XxBluetoothState.STATE_LINKED){
//
//						XxBluetoothPhoneManager.getInstance().call(phoneNumber);
//
//					}else{
//						PhoneUtil.call(mContext, phoneNumber);
//						//Toast.makeText(mContext, mContext.getResources().getString(R.string.dial_buletooth_waiting_connect), Toast.LENGTH_SHORT).show();
//					}
//                    BluetoothPhoneModule.getInstance().dial(phoneNumber);//注释test 拨打电话操作
				}
			}
		});

		return view;
	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		TextView contact_telephone;
		ImageView mBtnDial;
	}



	private void callOut(String phoneNumber2) {
		placeCall(phoneNumber2);
	}
	// 拨打正确的电话
	private static void placeCall(String mLastNumber) {
		if (mLastNumber.length() == 0)
			return;
		if (PhoneNumberUtils.isGlobalPhoneNumber(mLastNumber)) {
			// place the call if it is a valid number
			Log.d("GocApp", "isGlobalPhoneNumber 3: " + mLastNumber);
			if (mLastNumber == null || !TextUtils.isGraphic(mLastNumber)) {
				// There is no number entered.
				return;
			}
			try {
				Myapplication.getService().phoneDail(mLastNumber);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}



	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}