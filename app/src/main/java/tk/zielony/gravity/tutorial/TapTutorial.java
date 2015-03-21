package tk.zielony.gravity.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class TapTutorial extends Tutorial {

	private Paint paint = new Paint();
	private double x;
	private double y;
	private long prevTime;

	public TapTutorial(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TapTutorial(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TapTutorial(Context context) {
		super(context);
		init();
	}

	public void init() {
		setText("Tap to add a star");
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		float time = (float) (System.currentTimeMillis() % 1000 * Math.PI * 2 / 1000.0f);
		paint.setColor(0xffffff);
		paint.setAlpha((int) (63 * Math.sin(time-Math.PI/2) + 64));
		int size = 30;
		if (prevTime != System.currentTimeMillis() / 1000) {
			prevTime = System.currentTimeMillis() / 1000;
			x = (Math.random() - 0.5f) * getWidth() / 2;
			y = (Math.random() - 0.5f) * getHeight() / 2;
		}
		canvas.drawCircle((float) (getWidth() / 2 + x),
				(float) (getHeight() / 2 + y), size, paint);
		invalidate();
	}

}
