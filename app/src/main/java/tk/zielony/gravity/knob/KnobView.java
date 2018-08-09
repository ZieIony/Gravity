package tk.zielony.gravity.knob;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class KnobView extends View {
    private OnKnobStateChanged onKnoStateChanged;
    private Paint paint;
    private float y;
    private float x;
    private int bigRadius;
    private int smallRadius;
    private boolean down;

    public KnobView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
    }

    public KnobView(Context context) {
        super(context);
        init();
    }

    public KnobView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        smallRadius = Math.min(getWidth(), getHeight()) / 10;
        bigRadius = Math.min(getWidth(), getHeight()) / 3;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        paint.setColor(0x7fffffff);
        canvas.drawCircle(getWidth() / 2 + x / 2, getHeight() / 2 + y / 2,
                smallRadius, paint);
        canvas.drawCircle(getWidth() / 2 + x, getHeight() / 2 + y, bigRadius,
                paint);
        if (!down) {
            x = 2 * x / 3;
            y = 2 * y / 3;
            invalidate();
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE
                || event.getAction() == MotionEvent.ACTION_DOWN) {
            down = true;
            x = event.getX() - getWidth() / 2;
            x = Math.max(-getWidth() / 2 + bigRadius,
                    Math.min(x, getWidth() / 2 - bigRadius));
            y = event.getY() - getHeight() / 2;
            y = Math.max(-getHeight() / 2 + bigRadius,
                    Math.min(y, getHeight() / 2 - bigRadius));
            fireKnobStateChanged(event.getAction(), event.getX() - getWidth()
                    / 2, event.getY() - getHeight() / 2);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {
            down = false;
            fireKnobStateChanged(MotionEvent.ACTION_UP, getWidth() / 2,
                    getHeight() / 2);
        }
        invalidate();
        return true;
    }

    private void fireKnobStateChanged(int action, float x, float y) {
        if (onKnoStateChanged != null)
            onKnoStateChanged.onKnobStateChanged(action, x, y);
    }

    public void setOnKnobStateChanged(OnKnobStateChanged onKnoStateChanged) {
        this.onKnoStateChanged = onKnoStateChanged;
    }

}
