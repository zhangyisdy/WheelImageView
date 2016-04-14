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

	// Scrolling
	private RelativeLayout c;
	private WheelScroller scroller;
	private boolean isScrollingPerformed;
	private int scrollingOffset;

	// scroll image
	private RelativeLayout mCModeLauncher;
	private ImageView mIconHigherSmall;
	private ImageView mIconHigherMudium;
	private ImageView mIconLarge;
	private ImageView mIconLowerMudium;
	private ImageView mIconLowerSmall;

	// scroll image index
	private int mHigherSmallIndex = 0;
	private int mHigherMudiumIndex = 1;
	private int mLargeIndex = 2;
	private int mLowerMudiumIndex = 3;
	private int mLowerSmallIndex = 4;

	// scroll image click
	private final int IMAGE_CALLING = 0;
	private final int IMAGE_MAP = 1;
	private final int IMAGE_WECHAT = 2;
	private final int IMAGE_MUSIC = 3;
	private final int IMAGE_PHOTO = 4;

	// scroll image position
	private int CLICKED_IMAGE_TOUCH = -1;
	private final int mIconHigherSmallTouch = 0;
	private final int mIconHigherMudiumTouch = 1;
	private final int mIconLargeTouch = 2;
	private final int mIconLowerMudiumTouch = 3;
	private final int mIconLowerSmallTouch = 4;

	// scroll image distance
	private int mScrollImageDistance;

	private int flags[] = new int[] { R.drawable.c_mode_calling_large,
			R.drawable.c_mode_map_large, R.drawable.c_mode_wechat_large,
			R.drawable.c_mode_music_large, R.drawable.c_mode_photo_large };

	private int flags1[] = new int[] { R.drawable.c_mode_calling_higher_small,
			R.drawable.c_mode_map_higher_small,
			R.drawable.c_mode_wechat_higher_small,
			R.drawable.c_mode_music_higher_small,
			R.drawable.c_mode_photo_higher_small };

	private int flags2[] = new int[] { R.drawable.c_mode_calling_higher_medium,
			R.drawable.c_mode_map_higher_medium,
			R.drawable.c_mode_wechat_higher_medium,
			R.drawable.c_mode_music_higher_medium,
			R.drawable.c_mode_photo_higher_medium };

	private int flags3[] = new int[] { R.drawable.c_mode_calling_lower_small,
			R.drawable.c_mode_map_lower_small,
			R.drawable.c_mode_wechat_lower_small,
			R.drawable.c_mode_music_lower_small,
			R.drawable.c_mode_photo_lower_small };

	private int flags4[] = new int[] { R.drawable.c_mode_calling_lower_medium,
			R.drawable.c_mode_map_lower_medium,
			R.drawable.c_mode_wechat_lower_medium,
			R.drawable.c_mode_music_lower_medium,
			R.drawable.c_mode_photo_lower_medium };

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







	private void subIndex() {
		if (mLargeIndex == 0) {
			mLargeIndex = 5;
		} else if (mHigherMudiumIndex == 0) {
			mHigherMudiumIndex = 5;
		} else if (mHigherSmallIndex == 0) {
			mHigherSmallIndex = 5;
		} else if (mLowerMudiumIndex == 0) {
			mLowerMudiumIndex = 5;
		} else if (mLowerSmallIndex == 0) {
			mLowerSmallIndex = 5;
		}
		mLargeIndex = (mLargeIndex - 1) % 5;
		mHigherMudiumIndex = (mHigherMudiumIndex - 1) % 5;
		mHigherSmallIndex = (mHigherSmallIndex - 1) % 5;
		mLowerMudiumIndex = (mLowerMudiumIndex - 1) % 5;
		mLowerSmallIndex = (mLowerSmallIndex - 1) % 5;
	}

	private void replaceImage() {
		mIconLarge.setBackgroundResource(flags[mLargeIndex]);
		mIconHigherSmall.setBackgroundResource(flags1[mHigherSmallIndex]);
		mIconHigherMudium.setBackgroundResource(flags4[mHigherMudiumIndex]);
		mIconLowerSmall.setBackgroundResource(flags3[mLowerSmallIndex]);
		mIconLowerMudium.setBackgroundResource(flags4[mLowerMudiumIndex]);
	}

	private void addIndex() {
		mLargeIndex = (mLargeIndex + 1) % 5;
		mHigherMudiumIndex = (mHigherMudiumIndex + 1) % 5;
		mHigherSmallIndex = (mHigherSmallIndex + 1) % 5;
		mLowerMudiumIndex = (mLowerMudiumIndex + 1) % 5;
		mLowerSmallIndex = (mLowerSmallIndex + 1) % 5;
	}

	public void onIconClick(int what) {
		switch (what) {
		case mIconHigherSmallTouch:
			handleIconHigherSmallClick();
			break;
		case mIconHigherMudiumTouch:
			handleIconHigherMediumClick();
			break;
		case mIconLargeTouch:
			handleIconLargeClick();
			break;
		case mIconLowerMudiumTouch:
			handleIconLowerMediumClick();
			break;
		case mIconLowerSmallTouch:
			handleIconLowerSmallClick();
			break;
		}
	}

	private void handleIconLowerSmallClick() {
		switch (mLowerSmallIndex) {
		case IMAGE_CALLING:
			callIconClicked();
			break;
		case IMAGE_MAP:
			mapIconClicked();
			break;
		case IMAGE_WECHAT:
			wechatIconClicked();
			break;
		case IMAGE_MUSIC:
			musicIconClicked();
			break;
		case IMAGE_PHOTO:
			photoIconClicked();
			break;
		}

	}

	private void handleIconLowerMediumClick() {
		switch (mLowerMudiumIndex) {
		case IMAGE_CALLING:
			callIconClicked();
			break;
		case IMAGE_MAP:
			mapIconClicked();
			break;
		case IMAGE_WECHAT:
			wechatIconClicked();
			break;
		case IMAGE_MUSIC:
			musicIconClicked();
			break;
		case IMAGE_PHOTO:
			photoIconClicked();
			break;
		}

	}

	private void handleIconLargeClick() {
		switch (mLargeIndex) {
		case IMAGE_CALLING:
			callIconClicked();
			break;
		case IMAGE_MAP:
			mapIconClicked();
			break;
		case IMAGE_WECHAT:
			wechatIconClicked();
			break;
		case IMAGE_MUSIC:
			musicIconClicked();
			break;
		case IMAGE_PHOTO:
			photoIconClicked();
			break;
		}

	}

	private void handleIconHigherMediumClick() {
		switch (mHigherMudiumIndex) {
		case IMAGE_CALLING:
			callIconClicked();
			break;
		case IMAGE_MAP:
			mapIconClicked();
			break;
		case IMAGE_WECHAT:
			wechatIconClicked();
			break;
		case IMAGE_MUSIC:
			musicIconClicked();
			break;
		case IMAGE_PHOTO:
			photoIconClicked();
			break;
		}
	}

	private void handleIconHigherSmallClick() {
		switch (mHigherSmallIndex) {
		case IMAGE_CALLING:
			callIconClicked();
			break;
		case IMAGE_MAP:
			mapIconClicked();
			break;
		case IMAGE_WECHAT:
			wechatIconClicked();
			break;
		case IMAGE_MUSIC:
			musicIconClicked();
			break;
		case IMAGE_PHOTO:
			photoIconClicked();
			break;
		}

	}

	private void photoIconClicked() {
		Toast.makeText(getApplicationContext(), "photo icon clicked",
				Toast.LENGTH_SHORT).show();
	}

	private void musicIconClicked() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "music icon clicked",
				Toast.LENGTH_SHORT).show();
	}

	private void wechatIconClicked() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "wechat icon clicked",
				Toast.LENGTH_SHORT).show();
	}

	private void mapIconClicked() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "map icon clicked",
				Toast.LENGTH_SHORT).show();
	}

	private void callIconClicked() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "call icon clicked",
				Toast.LENGTH_SHORT).show();
	}

}
