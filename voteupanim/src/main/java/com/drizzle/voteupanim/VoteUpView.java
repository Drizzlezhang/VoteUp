package com.drizzle.voteupanim;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * Created by drizzle on 16/1/24.
 */
public class VoteUpView extends View {
	//paints
	private Paint backPaint;
	private Paint ripplePaint;
	private Paint defaultTextPaint;
	private Rect defaultTextRect;
	private Paint doneTextPaint;
	private Rect doneTextRect;

	//text
	private String defaultText;
	private String doneText;
	private int defaultTextColor;
	private int doneTextColor;
	private int textSize;

	//circle
	private int defaultColor;
	private int rippleColor;
	private int rippleRadius;

	//status
	private boolean isVoted;
	/**
	 * 必须reset,否则不断add导致资源耗尽
	 */
	private Path ripplePath;

	private int progress = 0;

	public VoteUpView(Context context) {
		this(context, null);
	}

	public VoteUpView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VoteUpView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VoteUpView, 0, 0);
		defaultText = array.getString(R.styleable.VoteUpView_default_text);
		doneText = array.getString(R.styleable.VoteUpView_done_text);
		defaultColor = array.getColor(R.styleable.VoteUpView_default_color, Color.BLUE);
		rippleColor = array.getColor(R.styleable.VoteUpView_ripple_color, Color.RED);
		defaultTextColor = array.getColor(R.styleable.VoteUpView_default_text_color, Color.BLACK);
		doneTextColor = array.getColor(R.styleable.VoteUpView_done_text_color, Color.BLACK);
		textSize = array.getDimensionPixelSize(R.styleable.VoteUpView_text_size,
			(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, getResources().getDisplayMetrics()));
		rippleRadius = array.getDimensionPixelSize(R.styleable.VoteUpView_ripple_radius,
			(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
		array.recycle();
		initPaints();
	}

	private void initPaints() {
		backPaint = new Paint();
		backPaint.setAntiAlias(true);
		backPaint.setStyle(Paint.Style.FILL);

		ripplePaint = new Paint();
		ripplePaint.setAntiAlias(true);
		ripplePaint.setStyle(Paint.Style.FILL);

		defaultTextPaint = new Paint();
		defaultTextPaint.setAntiAlias(true);
		defaultTextPaint.setStyle(Paint.Style.FILL);
		defaultTextRect = new Rect();

		doneTextPaint = new Paint();
		doneTextPaint.setAntiAlias(true);
		doneTextPaint.setStyle(Paint.Style.FILL);
		doneTextRect = new Rect();

		ripplePath = new Path();
	}

	public void setDefaultColor(int defaultColor) {
		this.defaultColor = defaultColor;
		ripplePath.reset();
		postInvalidate();
	}

	public void setRippleColor(int rippleColor) {
		this.rippleColor = rippleColor;
		ripplePath.reset();
		postInvalidate();
	}

	public void setRippleRadius(int rippleRadius) {
		ripplePath.reset();
		this.rippleRadius = rippleRadius;
		postInvalidate();
	}

	public void setDefaultText(String defaultText) {
		ripplePath.reset();
		this.defaultText = defaultText;
		postInvalidate();
	}

	public void setDoneText(String doneText) {
		ripplePath.reset();
		this.doneText = doneText;
		postInvalidate();
	}

	public void setDefaultTextColor(int defaultTextColor) {
		this.defaultTextColor = defaultTextColor;
		ripplePath.reset();
		postInvalidate();
	}

	public void setDoneTextColor(int doneTextColor) {
		this.doneTextColor = doneTextColor;
		ripplePath.reset();
		postInvalidate();
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
		ripplePath.reset();
		postInvalidate();
	}

	private void setProgress(int progress) {
		this.progress = progress;
		if (onVotingLinstener != null) {
			onVotingLinstener.OnVoting(progress);
		}
		ripplePath.reset();
		postInvalidate();
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width, height;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = getPaddingLeft() + getPaddingRight() + rippleRadius * 2;
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getPaddingTop() + getPaddingBottom() + rippleRadius * 2;
		}
		setMeasuredDimension(width, height);
	}

	@Override protected void onDraw(Canvas canvas) {
		//将canvas剪裁成一个圆形
		ripplePath.addCircle(getPaddingLeft() + rippleRadius, getPaddingTop() + rippleRadius, rippleRadius,
			Path.Direction.CW);
		canvas.clipPath(ripplePath);
		//画背景圆形
		backPaint.setColor(defaultColor);
		canvas.drawCircle(getPaddingLeft() + rippleRadius, getPaddingTop() + rippleRadius, rippleRadius, backPaint);
		ripplePaint.setColor(rippleColor);
		canvas.drawCircle(getPaddingLeft() + rippleRadius, getPaddingTop() + rippleRadius,
			rippleRadius * progress / 100, ripplePaint);
		//画文字
		//画default文字
		if (defaultText == null) {
			defaultText = "";
		}
		defaultTextPaint.setColor(defaultTextColor);
		defaultTextPaint.setTextSize(textSize);
		defaultTextPaint.getTextBounds(defaultText, 0, defaultText.length(), defaultTextRect);
		defaultTextPaint.setAlpha(155 * (100 - progress) / 100 + 100);
		canvas.drawText(defaultText, getPaddingLeft() + rippleRadius - defaultTextRect.width() / 2,
			getPaddingTop() + rippleRadius + defaultTextRect.height() / 2
				- (rippleRadius + defaultTextRect.height() / 2) * progress / 100, defaultTextPaint);
		//画done文字
		if (doneText == null) {
			doneText = "";
		}
		doneTextPaint.setColor(doneTextColor);
		doneTextPaint.setTextSize(textSize);
		doneTextPaint.getTextBounds(doneText, 0, doneText.length(), doneTextRect);
		doneTextPaint.setAlpha((255 - 100) * progress / 100 + 100);
		canvas.drawText(doneText, getPaddingLeft() + rippleRadius - doneTextRect.width() / 2,
			getPaddingTop() + rippleRadius + doneTextRect.height()
				- (rippleRadius + doneTextRect.height() / 2) * progress / 100 + rippleRadius, doneTextPaint);
	}

	private void playAnim(int start, int end) {
		RippelAnimation rippelAnimation = new RippelAnimation(start, end);
		rippelAnimation.setDuration(200);
		rippelAnimation.setInterpolator(new DecelerateInterpolator());
		this.startAnimation(rippelAnimation);
	}

	public void voteUp() {
		if (!isVoted) {
			playAnim(0, 100);
			isVoted = true;
		} else {
			return;
		}
	}

	public void voteDown() {
		if (isVoted) {
			playAnim(100, 0);
			isVoted = false;
		} else {
			return;
		}
	}

	private class RippelAnimation extends Animation {
		private int startProgress;
		private int endProgress;

		public RippelAnimation(int startProgress, int endProgress) {
			this.startProgress = startProgress;
			this.endProgress = endProgress;
		}

		@Override protected void applyTransformation(float interpolatedTime, Transformation t) {
			int progress = (int) (startProgress + ((endProgress - startProgress) * interpolatedTime));
			setProgress(progress);
			requestLayout();
		}
	}

	private OnVotingLinstener onVotingLinstener = null;

	public void setOnVotingLinstener(OnVotingLinstener onVotingLinstener) {
		this.onVotingLinstener = onVotingLinstener;
	}
}
