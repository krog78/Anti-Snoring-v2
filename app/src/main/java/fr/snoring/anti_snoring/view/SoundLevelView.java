package fr.snoring.anti_snoring.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import fr.snoring.anti_snoring.R;

public class SoundLevelView extends View {
	private Drawable mGreen;
	private Drawable mRed;
	private Paint mBackgroundPaint;
	private Paint mLinePaint;

	private int mHeight;
	private int mWidth;

	private int mThreshold = 0;
	private int mVol = 0;

	public SoundLevelView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mGreen = context.getResources().getDrawable(R.drawable.greenbar);
		mRed = context.getResources().getDrawable(R.drawable.redbar);
		mHeight = mGreen.getIntrinsicHeight();
		setMinimumHeight(mHeight);
		mWidth = mGreen.getIntrinsicWidth();
		setMinimumWidth(mWidth * 11);

		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(Color.BLACK);

		mLinePaint = new Paint();
		mLinePaint.setColor(Color.WHITE);
	}

	public void setLevel(int volume, int threshold) {
		if (volume == mVol && threshold == mThreshold)
			return;
		mVol = volume;
		mThreshold = threshold;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawPaint(mBackgroundPaint);
		float pos = mThreshold * mWidth;
		canvas.drawLine(pos, 0, pos, mHeight, mLinePaint);
		for (int i = 0; i <= mVol; i++) {
			Drawable bar;
			if (i < mThreshold)
				bar = mGreen;
			else
				bar = mRed;

			bar.setBounds(i * mWidth, 0, (i + 1) * mWidth, mHeight);
			bar.draw(canvas);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		widthMeasureSpec = mWidth * 12;
		heightMeasureSpec = mHeight;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}