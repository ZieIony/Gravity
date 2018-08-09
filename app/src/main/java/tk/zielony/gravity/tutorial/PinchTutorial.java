package tk.zielony.gravity.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class PinchTutorial extends Tutorial {

    private Paint paint = new Paint();

    public PinchTutorial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PinchTutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinchTutorial(Context context) {
        super(context);
        init();
    }

    public void init() {
        setText("Pinch to zoom");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float time = (float) (System.currentTimeMillis() % 5000 * Math.PI * 2 / 5000.0f);
        paint.setColor(0x7fffffff);
        int dist = getWidth() / 7;
        int size = 30;
        canvas.drawCircle((float) (-Math.sin(time) * dist + getWidth() / 2
                + dist + size), (float) (Math.sin(time) * dist + getHeight()
                / 2 - dist - size), size, paint);
        canvas.drawCircle((float) (-Math.sin(-time) * dist + getWidth() / 2
                - dist - size), (float) (Math.sin(-time) * dist + getHeight()
                / 2 + dist + size), size, paint);
        invalidate();
    }

}
