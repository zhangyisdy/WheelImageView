package com.example.c_mode_launcher;

import java.util.Timer;
import java.util.TimerTask;

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
import android.os.Handler;
import android.text.format.Time;
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
	private float mScaleX = 1;
	private float mScaleY = 1;
	private float mScaleXEnlarge = 1;
	private float mScaleYEnlarge = 1;
	private float mScaleXReduce = 1;
	private float mScaleYReduce = 1;
	private int mDesc = 0;
	
	// 旋转控制系数
	/* x扩展比率发生图片替换 */
	private final static float  ROTATION_RATIO =  0.17f;
	/* 图片 X 放大比例 */
	private final static float ENLARGE_X = 0.005f;
	/* 中间图片 Y 缩小比例 */
	private final static float REDUCE_MIDDLE_Y = 0.0175f;
	/* 图片 Y 放大比例 */
	private final static float ENLARGE_Y = 0.025f;
	/* 上下小图片 Y 缩x小比例 */
	private final static float REDUCE_EDGE_Y = 0.01f;
 
	private final int MAX_FING_BOUND = 50;
	
	/* 时间任务旋转参数 */
	private MyTimerTask mTask;
	private Timer mTimer = new Timer();
	private boolean mStartWheel = true;
	private boolean mTouchUp = false;
	private final static float TIME_TASK_WHEEL_RATIO = 1.07f;
	
	@SuppressLint("HandlerLeak")
	private Handler updateHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(mStartWheel){
				wheelImage(sum);
			}
		};
	};

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

			if (Math.abs(distance) < MAX_FING_BOUND && !mTouchUp) {
				wheelImage(distance);
			} else if (Math.abs(distance) > MAX_FING_BOUND) {
				int i = 8;
				if (distance < 0) {
					while (distance < 0) {
						wheelImage(distance);
						distance = (distance + i);
						i = i * 2;
					}
				} else {
					while (distance > 0) {
						wheelImage(distance);
						distance = (distance - i);
						i = i * 2;
					}
				}
			}
			
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
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = (int) event.getX();
				mDownY = (int) event.getY();
				setIconOriginPositon();
				replaceImage();
				mTouchUp = false;
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
				
				/* 当旋转到中间位置时 开始时间任务，自动旋转 */
				if(mScaleXEnlarge >= TIME_TASK_WHEEL_RATIO){
					if (mTask != null) {
						mTask.cancel();
						mTask = null;
					}
					mTask = new MyTimerTask(updateHandler );
					mStartWheel = true;
					mTimer.schedule(mTask, 0, 10);
				}else{
					if (mTask != null) {
						mTask.cancel();
						mTask = null;
					}
					replaceImage();
				}
				
				mTouchUp = true;
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

	/**
	 *  set icon home position
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
	 * wheel image
	 * @param move distance and direction 
	 * 					move > 0 向下滑动 move<0 向上滑动
	 */
	int sum = 0;
	private void wheelImage(int move) {
		sum += mDesc;
		if(move > 0){
			mDesc +=1;	//向下滑动
		}else{
			mDesc -= 1;	//向上滑动
		}
		if(mScaleX > (1-ROTATION_RATIO)){
			mScaleX -= ENLARGE_X;
			mScaleY -= REDUCE_MIDDLE_Y;
			//large icon reduce
			mLargeScaleMatrix.setScale((float)(mScaleX*0.95), mScaleY,
																	mIconLarge.getWidth() / 2,
																	mIconLarge.getHeight() / 2);

			mIconLarge.layout(mIconLarge.getLeft(),
														mIconLarge.getTop()+mDesc/4,
														mIconLarge.getRight(),
														mIconLarge.getBottom()+mDesc/4);
			mIconLarge.setImageMatrix(mLargeScaleMatrix);
		}

		if(move > 0){
			// 向下滑动
			if(mScaleXEnlarge < (1+ROTATION_RATIO)){
					mScaleXEnlarge += ENLARGE_X;
					mScaleYEnlarge += ENLARGE_Y;
					mScaleXReduce -= ENLARGE_X;
					mScaleYReduce -= REDUCE_EDGE_Y;

				// higher small icon enlarge
				mHigherSmallMatrix.setScale(mScaleXEnlarge, mScaleYEnlarge,
						mIconHigherSmall.getWidth() / 2,
						mIconHigherSmall.getHeight() / 2);
				mIconHigherSmall.layout(mIconHigherSmall.getLeft(),mIconHigherSmall.getTop()+mDesc/8, 
																	mIconHigherSmall.getRight(), mIconHigherSmall.getBottom()+mDesc/8);
				mIconHigherSmall.setImageMatrix(mHigherSmallMatrix);

				// higher medium icon enlarge
				mHigherMudiumMatrix.setScale(mScaleXEnlarge, (float)(mScaleYEnlarge*1.3),
																				 mIconHigherMudium.getWidth() / 2,
																				 mIconHigherMudium.getHeight() / 2-Math.abs(mDesc));
				mIconHigherMudium.layout(mIconHigherMudium.getLeft(),mIconHigherMudium.getTop()+(int)(mDesc/4.5), 
																		  mIconHigherMudium.getRight(), mIconHigherMudium.getBottom()+(int)(mDesc/3));
				mIconHigherMudium.setImageMatrix(mHigherMudiumMatrix);

				//lower small icon reduce
				mLowerSmallMatrix.setScale(mScaleXReduce, mScaleYReduce,
																		  mIconLowerSmall.getWidth() / 2,
																		  mIconLowerSmall.getHeight() / 2);
				mIconLowerSmall.layout(mIconLowerSmall.getLeft(),mIconLowerSmall.getTop()+mDesc/8, 
																	mIconLowerSmall.getRight(), mIconLowerSmall.getBottom()+mDesc/8);
				mIconLowerSmall.setImageMatrix(mLowerSmallMatrix);

				//lower medium icon reduce
				mLowerMudiumMatrx.setScale(mScaleXReduce, mScaleYReduce,
																				mIconLowerMudium.getWidth() / 2,
																				mIconLowerMudium.getHeight() / 2);
				mIconLowerMudium.layout(mIconLowerMudium.getLeft(),mIconLowerMudium.getTop()+mDesc/8,
																		  mIconLowerMudium.getRight(), mIconLowerMudium.getBottom()+mDesc/8);
				mIconLowerMudium.setImageMatrix(mLowerMudiumMatrx);
			}else{
				subIndex();
				replaceImage();
			}
		}else{
			// 向上滑动
			if(mScaleXEnlarge <  (1+ROTATION_RATIO)){
				mScaleXEnlarge += ENLARGE_X;
				mScaleYEnlarge += ENLARGE_Y;
				mScaleXReduce -= ENLARGE_X;
				mScaleYReduce -= REDUCE_EDGE_Y;

				// lower medium icon enlarge
				mLowerMudiumMatrx.setScale(mScaleXEnlarge, (float)(mScaleYEnlarge*1.3),
						mIconLowerMudium.getWidth() / 2,
						mIconLowerMudium.getHeight() / 2-2*Math.abs(mDesc));
				mIconLowerMudium.layout(mIconLowerMudium.getLeft(),mIconLowerMudium.getTop()+(int)(mDesc/3.1), 
																		  mIconLowerMudium.getRight(), mIconLowerMudium.getBottom()+(mDesc/4));
				mIconLowerMudium.setImageMatrix(mLowerMudiumMatrx);

				//lower small icon enlarge
				mLowerSmallMatrix.setScale(mScaleXEnlarge, mScaleYEnlarge,
						mIconHigherSmall.getWidth() / 2,
						mIconHigherSmall.getHeight() / 2);
				mIconLowerSmall.layout(mIconLowerSmall.getLeft(),mIconLowerSmall.getTop()+mDesc/8, 
																   mIconLowerSmall.getRight(), mIconLowerSmall.getBottom()+mDesc/8);
				mIconLowerSmall.setImageMatrix(mLowerSmallMatrix);

				//higher small icon reduce
				mHigherSmallMatrix.setScale(mScaleXReduce, mScaleYReduce,
						mIconHigherSmall.getWidth() / 2,
						mIconHigherSmall.getHeight() / 2);
				mIconHigherSmall.layout(mIconHigherSmall.getLeft(),mIconHigherSmall.getTop()+mDesc/8, 
																	mIconHigherSmall.getRight(), mIconHigherSmall.getBottom()+mDesc/8);
				mIconHigherSmall.setImageMatrix(mHigherSmallMatrix);	

				//higher medium icon reduce
				mHigherMudiumMatrix.setScale(mScaleXReduce, mScaleYReduce,
						mIconHigherMudium.getWidth() / 2,
						mIconHigherMudium.getHeight() / 2);
				mIconHigherMudium.layout(mIconHigherMudium.getLeft(),mIconHigherMudium.getTop()+mDesc/8, 
																		  mIconHigherMudium.getRight(), mIconHigherMudium.getBottom()+mDesc/8);
				mIconHigherMudium.setImageMatrix(mHigherMudiumMatrix);
			}else{
				addIndex();
				replaceImage();
			}
		}
	}

	/**
	 *  touch 处理完成，替换图片， 并初始化image滑动的位置
	 */
	private void replaceImage() {
		
		/* 初始化时间控制器 */
		sum = 0;
		mStartWheel = false;
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}

		// init enlarge and reduce ratio
		mScaleX = mScaleXEnlarge = mScaleXReduce= 1;
		mScaleY = mScaleYEnlarge = mScaleYReduce = 1;
		mDesc = 0;

		// init large image position
		mLargeScaleMatrix = new Matrix();
		mIconLarge.setImageMatrix(mLargeScaleMatrix);
		mIconLarge.layout(mIconLarge.getLeft(),mIconLargeTop, mIconLarge.getRight(), mIconLargeBottom);

		// init large image position
		mHigherSmallMatrix = new Matrix();
		mIconHigherSmall.setImageMatrix(mHigherSmallMatrix);
		mIconHigherSmall.layout(mIconHigherSmall.getLeft(),mIconHigherSmallTop, mIconHigherSmall.getRight(), mIconHigherSmallBottom);

		// init higher medium image position
		mHigherMudiumMatrix = new Matrix();
		mIconHigherMudium.setImageMatrix(mHigherMudiumMatrix);
		mIconHigherMudium.layout(mIconHigherMudium.getLeft(),mIconHigherMudiumTop, mIconHigherMudium.getRight(), mIconHigherMudiumBottom);

		// init lower medium image position
		mLowerMudiumMatrx = new Matrix();
		mIconLowerMudium.setImageMatrix(mLowerMudiumMatrx);
		mIconLowerMudium.layout(mIconLowerMudium.getLeft(),mIconLowerMudiumTop, mIconLowerMudium.getRight(), mIconLowerMudiumBottom);

		// init lower small image position
		mLowerSmallMatrix = new Matrix();
		mIconLowerSmall.setImageMatrix(mLowerSmallMatrix);
		mIconLowerSmall.layout(mIconLowerSmall.getLeft(),mIconLowerSmallTop, mIconLowerSmall.getRight(), mIconLowerSmallBottom);

		// replace image
		mIconDesc.setText(mIconDescs[mLargeIndex]);
		mIconLarge.setImageResource(flags[mLargeIndex]);
		mIconHigherSmall.setImageResource(flags1[mHigherSmallIndex]);
		mIconHigherMudium.setImageResource(flags4[mHigherMudiumIndex]);
		mIconLowerSmall.setImageResource(flags3[mLowerSmallIndex]);
		mIconLowerMudium.setImageResource(flags4[mLowerMudiumIndex]);
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
		Toast.makeText(mContext, "wechat icon clicked", Toast.LENGTH_SHORT)
				.show();
	}

	private void mapIconClicked() {
				Toast.makeText(mContext, "map icon clicked", Toast.LENGTH_SHORT).show();
	}

	private void callIconClicked() {
		Toast.makeText(mContext, "call icon clicked", Toast.LENGTH_SHORT)
				.show();
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
	
	class MyTimerTask extends TimerTask {
		Handler handler;

		public MyTimerTask(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			handler.sendMessage(handler.obtainMessage());
		}
	}
}
