package tk.zielony.gravity.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class LongpressTutorial extends Tutorial {

    private Paint paint = new Paint();
    private long prevTime;
    private double x;
    private double y;

    public LongpressTutorial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LongpressTutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LongpressTutorial(Context context) {
        super(context);
        init();
    }

    public void init() {
        setText("Longpress to add a larger star");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float time = (float) (System.currentTimeMillis() % 5000 * Math.PI * 2 / 5000.0f);
        paint.setColor(0xffffff);
        paint.setAlpha((int) (63 * Math.sin(time) + 64));
        int size = 30;
        if (prevTime != System.currentTimeMillis() / 5000) {
            prevTime = System.currentTimeMillis() / 5000;
            x = (Math.random() - 0.5f) * getWidth() / 2;
            y = (Math.random() - 0.5f) * getHeight() / 2;
        }
        canvas.drawCircle((float) (getWidth() / 2 + x),
                (float) (getHeight() / 2 + y), size, paint);
        invalidate();
    }

}
