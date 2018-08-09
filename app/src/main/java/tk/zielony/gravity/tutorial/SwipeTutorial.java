package tk.zielony.gravity.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class SwipeTutorial extends Tutorial {

    private Paint paint = new Paint();

    public SwipeTutorial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SwipeTutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeTutorial(Context context) {
        super(context);
        init();
    }

    public void init() {
        setText("Swipe to throw a star");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float time = (float) (System.currentTimeMillis() % 2000 * Math.PI * 2 / 2000.0f);
        paint.setColor(0xffffff);
        paint.setAlpha((int) (63 * Math.sin(time) + 64));
        int dist = getWidth() / 4;
        int size = 30;
        canvas.drawCircle((float) (-Math.cos(time) * dist + getWidth() / 2),
                (float) (-Math.abs(Math.cos(time)) * dist + getHeight() / 2),
                size, paint);
        invalidate();
    }

}
