package com.etrans.bluetooth.View;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.etrans.bluetooth.R;

public class TransparentInComingDialog extends AlertDialog {
	private Context mContext;
	private View view;
	public TransparentInComingDialog(Context context, boolean cancelable,
                                     OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public TransparentInComingDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		view = View.inflate(context, R.layout.dialog_incoming, null);
	}

	public TransparentInComingDialog(Context context) {
		super(context);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(view);
	}
	@Override
	public View findViewById(int id) {
		return super.findViewById(id);
	}
	public View getCustomView(){
		return view;
	}
}
