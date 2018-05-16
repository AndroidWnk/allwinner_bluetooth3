package com.etrans.bluetooth;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.etrans.bluetooth.utils.HomeKey;

public class BaseActivity extends FragmentActivity {

	private HomeKey mHomeKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHomeKey = new HomeKey(this);
		mHomeKey.setOnHomePressedListener(new HomeKey.OnHomePressedListener() {
			
			@Override
			public void onHomePressed() {
				finish();
			}
			
			@Override
			public void onHomeLongPressed() {
				finish();
			}
		});
		mHomeKey.startWatch();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHomeKey.stopWatch();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
}
