package com.example.c_mode_launcher;

import android.util.Log;
import android.app.Activity;
import android.app.usage.UsageEvents.Event;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case KeyEvent.KEYCODE_F2:
			Log.d("zhangyi", "F2 key down1111");
			Toast.makeText(getApplicationContext(), "F2 key down1111",
					Toast.LENGTH_SHORT).show();
			break;
		case KeyEvent.KEYCODE_F4:
			Log.d("zhangyi", "F4 key down1111");
			Toast.makeText(getApplicationContext(), "F4 key down1111",
					Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
