package com.example.c_mode_launcher;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WheelLayout extends RelativeLayout {

	private Context mContext;
	private TextView mIconDesc;
	// Scrolling
	private RelativeLayout c;
	private WheelScroller scroller;
	private boolean isScrollingPerformed;
	private int scrollingOffset = 0;

	// scroll image
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

	private String[] mIconDescs = new String[]{"Calling","Map","WeChat","Music","Photo"};
	
	private int flags[] = new int[] { R.drawable.c_mode_calling_large,
			R.drawable.c_mode_map_large, 
			R.drawable.c_mode_wechat_large,
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

	private RelativeLayout layout;
	
	// icon home position
	private int mIconLargeTop;
	private int mIconLargeBottom;
	private int mIconHigherSmallTop;
	private int mIconHigherSmallBottom;
	private int mIconHigherMudiumTop;
	private int mIconHigherMudiumBottom;
	private int mIconLowerMudiumTop;
	private int mIconLowerMudiumBottom;
	private int mIconLowerSmallTop;
	private int mIconLowerSmallBottom;
	
	//icon enlarge and reduce ratio
	Matrix mLargeScaleMatrix = new Matrix();
	Matrix mHigherSmallMatrix = new Matrix();
	Matrix mHigherMudiumMatrix = new Matrix();
	Matrix mLowerMudiumMatrx = new Matrix();
	Matrix mLowerSmallMatrix = new Matrix();
	float mScaleX = 1;
	float mScaleY = 1;
	float mScaleXEnlarge = 1;
	float mScaleYEnlarge = 1;
	float mScaleXReduce = 1;
	float mScaleYReduce = 1;
	int mDesc = 0;
	
	private final int MAX_FING_BOUND = 60;

	public WheelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView(context);
	}

	public WheelLayout(Context context) {
		super(context);
		this.mContext = context;
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		if (layout == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout) inflater.inflate(R.layout.c_mode_layout,this);
		}
		mIconDesc = (TextView) findViewById(R.id.icon_desc);
		mIconHigherSmall = (ImageView) layout.findViewById(R.id.icon_higher_small);
		mIconHigherMudium = (ImageView) layout.findViewById(R.id.icon_higher_medium);
		mIconLarge = (ImageView) layout.findViewById(R.id.icon_large);
		mIconLowerMudium = (ImageView) layout.findViewById(R.id.icon_lower_medium);
		mIconLowerSmall = (ImageView) layout.findViewById(R.id.icon_lower_small);
		iconSetTouchListener();
		scroller = new WheelScroller(context, scrollingListener);
	}

	int scrollOffset = 0;

	
	// Scrolling listener
	WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {

		@SuppressLint("NewApi")
		public void onStarted() {
			isScrollingPerformed = true;
		}

		public void onScroll(int distance) {
			scrollingOffset += distance;
			// abandon fing scroll
			if(Math.abs(distance) < MAX_FING_BOUND)
				rotateImage(distance) ;
			
			int height = 10;
			if (scrollingOffset > height) {
				scrollingOffset = height;
				scroller.stopScrolling();
			} else if (scrollingOffset < -height) {
				scrollingOffset = -height;
				scroller.stopScrolling();
			}
			invalidate();
		}

		@SuppressLint("NewApi")
		public void onFinished(int mode) {
			if (isScrollingPerformed) {
				isScrollingPerformed = false;
			}
			// slid finish to replace image
			if (mode == 0) {
				addIndex();
			} else {
				subIndex();
			}
			replaceImage();
			invalidate();
		}

		public void onJustify() {
			if (Math.abs(scrollingOffset+2) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
				scroller.scroll(scrollingOffset, 0);
			}
		}
	};

	private int mDownX;
	private int mDownY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = (int) event.getX();
				mDownY = (int) event.getY();
				setIconOriginPositon();
				replaceImage();
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				int offsetY = (mDownY - (int) event.getY());
				int offsetX = (mDownX - (int) event.getX());
				mDownY = (int) event.getY();
				// click icon
				if (Math.abs(offsetX) < 5 && Math.abs(offsetY) < 5) {
					onIconClick(CLICKED_IMAGE_TOUCH);
				}
				CLICKED_IMAGE_TOUCH = -1;
				replaceImage();
				// abandon event to sroller
				if (Math.abs(offsetY) < 50) {
					return true;
				}
				break;
			}
		return scroller.onTouchEvent(event);
	}

	private void iconSetTouchListener() {
		mIconLarge.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				CLICKED_IMAGE_TOUCH = mIconLargeTouch;
				return false;
			}
		});
		mIconHigherSmall.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				CLICKED_IMAGE_TOUCH = mIconHigherSmallTouch;
				return false;
			}
		});
		mIconHigherMudium.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				CLICKED_IMAGE_TOUCH = mIconHigherMudiumTouch;
				return false;
			}
		});
		mIconLowerMudium.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				CLICKED_IMAGE_TOUCH = mIconLowerMudiumTouch;
				return false;
			}
		});
		mIconLowerSmall.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				CLICKED_IMAGE_TOUCH = mIconLowerSmallTouch;
				return false;
			}
		});
	}

	private void subIndex() {
		if (mLargeIndex == 0) {
			mLargeIndex = 5;
		}
		if (mHigherMudiumIndex == 0) {
			mHigherMudiumIndex = 5;
		} 
		if (mHigherSmallIndex == 0) {
			mHigherSmallIndex = 5;
		}
		if (mLowerMudiumIndex == 0) {
			mLowerMudiumIndex = 5;
		} 
		if (mLowerSmallIndex == 0) {
			mLowerSmallIndex = 5;
		}
		mLargeIndex = (mLargeIndex - 1) % 5;
		mHigherMudiumIndex = (mHigherMudiumIndex - 1) % 5;
		mHigherSmallIndex = (mHigherSmallIndex - 1) % 5;
		mLowerMudiumIndex = (mLowerMudiumIndex - 1) % 5;
		mLowerSmallIndex = (mLowerSmallIndex - 1) % 5;
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
		Toast.makeText(mContext, "photo icon clicked", Toast.LENGTH_SHORT)
				.show();
	}

	private void musicIconClicked() {
		Toast.makeText(mContext, "music icon clicked", Toast.LENGTH_SHORT)
				.show();
	}

	private void wechatIconClicked() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "wechat icon clicked", Toast.LENGTH_SHORT)
				.show();
	}

	private void mapIconClicked() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "map icon clicked", Toast.LENGTH_SHORT).show();
	}

	private void callIconClicked() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "call icon clicked", Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 *  set icoc home position
	 */
	private void setIconOriginPositon(){
		mIconLargeTop = mIconLarge.getTop();
		mIconLargeBottom = mIconLarge.getBottom();
		mIconLargeBottom = mIconLarge.getBottom();
		mIconHigherSmallTop = mIconHigherSmall.getTop();
		mIconHigherSmallBottom = mIconHigherSmall.getBottom();
		mIconHigherMudiumTop = mIconHigherMudium.getTop();
		mIconHigherMudiumBottom = mIconHigherMudium.getBottom();
		mIconLowerMudiumTop= mIconLowerMudium.getTop();
		mIconLowerMudiumBottom = mIconLowerMudium.getBottom();
		mIconLowerSmallTop = mIconLowerSmall.getTop();
		mIconLowerSmallBottom = mIconLowerSmall.getBottom();
	}

	/**
	 * rotate image
	 */
	@SuppressLint("NewApi")
	private void rotateImage(int move) {
		if(move > 0){
			mDesc +=1;	//向下滑动
		}else{
			mDesc -= 1;	//向上滑动
		}

		if(mScaleX > 0.9f){
			// 设置放大 缩小比率 最小缩小为90%
			mScaleX -= 0.01f;
			mScaleY -= 0.03f;
			//large icon reduce
			mLargeScaleMatrix.setScale(mScaleX, mScaleY,
					mIconLarge.getWidth() / 2,
					mIconLarge.getHeight() / 2);
			mIconLarge.layout(mIconLarge.getLeft(),
					mIconLarge.getTop()+mDesc, mIconLarge.getRight(), mIconLarge.getBottom()+mDesc);
			mIconLarge.setImageMatrix(mLargeScaleMatrix);
		}

		if(move > 0){
			// 向下滑动
			if(mScaleXEnlarge < 1.1f){
				// 设置放大 缩小比率 最小缩小为90% 最大放大为110%
				mScaleXEnlarge += 0.01f;
				mScaleYEnlarge += 0.05f;
				mScaleXReduce -= 0.01f;
				mScaleYReduce -= 0.02f;

				// higher small icon enlarge
				mHigherSmallMatrix.setScale(mScaleXEnlarge, mScaleYEnlarge,
						mIconHigherSmall.getWidth() / 2,
						mIconHigherSmall.getHeight() / 2);
				mIconHigherSmall.layout(mIconHigherSmall.getLeft(),mIconHigherSmall.getTop()+mDesc/2, 
																	mIconHigherSmall.getRight(), mIconHigherSmall.getBottom()+mDesc/2);
				mIconHigherSmall.setImageMatrix(mHigherSmallMatrix);

				// higher mudium icon enlarge
				mHigherMudiumMatrix.setScale(mScaleXEnlarge, mScaleYEnlarge,
						mIconHigherMudium.getWidth() / 2,
						mIconHigherMudium.getHeight() / 2);
				mIconHigherMudium.layout(mIconHigherMudium.getLeft(),mIconHigherMudium.getTop()+mDesc, 
																		  mIconHigherMudium.getRight(), mIconHigherMudium.getBottom()+mDesc);
				mIconHigherMudium.setImageMatrix(mHigherMudiumMatrix);

				//lower small icon reduce
				mLowerSmallMatrix.setScale(mScaleXReduce, mScaleYReduce,
						mIconLowerSmall.getWidth() / 2,
						mIconLowerSmall.getHeight() / 2);
				mIconLowerSmall.layout(mIconLowerSmall.getLeft(),mIconLowerSmall.getTop()+mDesc/2, 
						mIconLowerSmall.getRight(), mIconLowerSmall.getBottom()+mDesc/2);
				mIconLowerSmall.setImageMatrix(mLowerSmallMatrix);
				
				//lower mudium icon reduce
				mLowerMudiumMatrx.setScale(mScaleXReduce, mScaleYReduce,
						mIconLowerMudium.getWidth() / 2,
						mIconLowerMudium.getHeight() / 2);
				mIconLowerMudium.layout(mIconLowerMudium.getLeft(),mIconLowerMudium.getTop()+mDesc/2, 
																		  mIconLowerMudium.getRight(), mIconLowerMudium.getBottom()+mDesc/2);
				mIconLowerMudium.setImageMatrix(mLowerMudiumMatrx);
			}
		}else{
			// 向上滑动
			if(mScaleXEnlarge < 1.1f){
				// 设置放大 缩小比率 最小缩小为90% 最大放大为110%
				mScaleXEnlarge += 0.01f;
				mScaleYEnlarge += 0.05f;
				mScaleXReduce -= 0.01f;
				mScaleYReduce -= 0.02f;

				// lower mudium icon enlarge
				mLowerMudiumMatrx.setScale(mScaleXEnlarge, mScaleYEnlarge,
						mIconLowerMudium.getWidth() / 2,
						mIconLowerMudium.getHeight() / 2);
				mIconLowerMudium.layout(mIconLowerMudium.getLeft(),mIconLowerMudium.getTop()+mDesc, 
																		mIconLowerMudium.getRight(), mIconLowerMudium.getBottom()+mDesc);
				mIconLowerMudium.setImageMatrix(mLowerMudiumMatrx);

				//lower small icon enlarge
				mLowerSmallMatrix.setScale(mScaleXEnlarge, mScaleYEnlarge,
						mIconHigherSmall.getWidth() / 2,
						mIconHigherSmall.getHeight() / 2);
				mIconLowerSmall.layout(mIconLowerSmall.getLeft(),mIconLowerSmall.getTop()+mDesc/2, 
																   mIconLowerSmall.getRight(), mIconLowerSmall.getBottom()+mDesc/2);
				mIconLowerSmall.setImageMatrix(mLowerSmallMatrix);

				//higher small icon reduce
				mHigherSmallMatrix.setScale(mScaleXReduce, mScaleYReduce,
						mIconHigherSmall.getWidth() / 2,
						mIconHigherSmall.getHeight() / 2);
				mIconHigherSmall.layout(mIconHigherSmall.getLeft(),mIconHigherSmall.getTop()+mDesc/2, 
																	mIconHigherSmall.getRight(), mIconHigherSmall.getBottom()+mDesc/2);
				mIconHigherSmall.setImageMatrix(mHigherSmallMatrix);	

				//higher mudium icon reduce
				mHigherMudiumMatrix.setScale(mScaleXReduce, mScaleYReduce,
						mIconHigherMudium.getWidth() / 2,
						mIconHigherMudium.getHeight() / 2);
				mIconHigherMudium.layout(mIconHigherMudium.getLeft(),mIconHigherMudium.getTop()+mDesc/2, 
																		  mIconHigherMudium.getRight(), mIconHigherMudium.getBottom()+mDesc/2);
				mIconHigherMudium.setImageMatrix(mHigherMudiumMatrix);
			}
		}
	}

	/**
	 *  touch 处理完成，替换图片， 并初始化image滑动的位置
	 */
	private void replaceImage() {
		// init enlarge and reduce ratio
		mScaleX = mScaleXEnlarge = mScaleXReduce= 1;
		mScaleY =mScaleYEnlarge= mScaleYReduce = 1;
		mDesc = 0;

		// init large image position
		mLargeScaleMatrix = new Matrix();
		mIconLarge.setImageMatrix(mLargeScaleMatrix);
		mIconLarge.layout(mIconLarge.getLeft(),
				mIconLargeTop, mIconLarge.getRight(), mIconLargeBottom);

		// init large image position
		mHigherSmallMatrix = new Matrix();
		mIconHigherSmall.setImageMatrix(mHigherSmallMatrix);
		mIconHigherSmall.layout(mIconHigherSmall.getLeft(),
				mIconHigherSmallTop, mIconHigherSmall.getRight(), mIconHigherSmallBottom);

		// init higher mudium image position
		mHigherMudiumMatrix = new Matrix();
		mIconHigherMudium.setImageMatrix(mHigherMudiumMatrix);
		mIconHigherMudium.layout(mIconHigherMudium.getLeft(),
				mIconHigherMudiumTop, mIconHigherMudium.getRight(), mIconHigherMudiumBottom);

		// init lower mudium image position
		mLowerMudiumMatrx = new Matrix();
		mIconLowerMudium.setImageMatrix(mLowerMudiumMatrx);
		mIconLowerMudium.layout(mIconLowerMudium.getLeft(),
				mIconLowerMudiumTop, mIconLowerMudium.getRight(), mIconLowerMudiumBottom);

		// init lower small image position
		mLowerSmallMatrix = new Matrix();
		mIconLowerSmall.setImageMatrix(mLowerSmallMatrix);
		mIconLowerSmall.layout(mIconLowerSmall.getLeft(),
				mIconLowerSmallTop, mIconLowerSmall.getRight(), mIconLowerSmallBottom);

		// replace image
		mIconLarge.setImageResource(flags[mLargeIndex]);
		mIconDesc.setText(mIconDescs[mLargeIndex]);
		mIconHigherSmall.setImageResource(flags1[mHigherSmallIndex]);
		mIconHigherMudium.setImageResource(flags4[mHigherMudiumIndex]);
		mIconLowerSmall.setImageResource(flags3[mLowerSmallIndex]);
		mIconLowerMudium.setImageResource(flags4[mLowerMudiumIndex]);
	}

}
