package com.example.c_mode_launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class wheelLayout extends LinearLayout{
	
	private Camera mCamera = new Camera();
	private final Matrix mMatrix = new Matrix();
	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

	public wheelLayout(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}
	
    private void initView(Context context) {
		// TODO Auto-generated method stub
        mPaint.setAntiAlias(true);
        this.setChildrenDrawingOrderEnabled(true);
	}

	private Bitmap getChildDrawingCache(final View child) {
        Bitmap bitmap = child.getDrawingCache();
        if (bitmap == null) {
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }
        return bitmap;
    }
    
    private void prepareMatrix(final Matrix outMatrix, int distanceY, int r) {
        //clip the distance
        final int d = Math.min(r, Math.abs(distanceY));
        //use circle formula
        final float translateZ = (float) Math.sqrt((r * r) - (d * d));
        Log.d("zhangyi", "translateZ is"+translateZ+" height is"+r);
        mCamera.save();
        mCamera.translate(0, 0, r-translateZ);
        mCamera.getMatrix(outMatrix);
        mCamera.restore();
    }
	
	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		Log.d("zhangyi", "drawChild...............");
        Bitmap bitmap = getChildDrawingCache(child);
        // (top,left) is the pixel position of the child inside the list
        final int top = child.getTop();
        final int left = child.getLeft();
        // center point of child
        final int childCenterY = child.getHeight() / 2;
        final int childCenterX = child.getWidth() / 2;
        //center of list
        final int parentCenterY = getHeight() / 2;
        final int parentCenterX = getWidth() / 2;
        //center point of child relative to list
        final int absChildCenterY = child.getTop() + childCenterY;
        final int absChildCenterX = child.getLeft() + childCenterX;
        //distance of child center to the list center
        final int distanceY = parentCenterY - absChildCenterY;

        final int distanceX = parentCenterX - absChildCenterX;

        prepareMatrix(mMatrix, distanceY, getHeight() / 2);

        mMatrix.preTranslate(-childCenterX, -childCenterY);
        mMatrix.postTranslate(childCenterX, childCenterY);
        mMatrix.postTranslate(left, top);

        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        return false;
	}

}
