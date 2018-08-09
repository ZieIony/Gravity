package tk.zielony.gravity.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class Laser {
    private int color;
    float x, y, velx, vely;
    private static Paint paint = new Paint();
    public static ArrayList<Laser> toRemove = new ArrayList<>();

    public Laser(float x, float y, float velx, float vely, int color) {
        this.x = x;
        this.y = y;
        this.velx = velx;
        this.vely = vely;
        this.color = color;
    }

    public void animate(float dt) {
        x += velx * dt;
        y += vely * dt;
    }

    public void draw(Canvas canvas) {
        paint.setColor(color);
        canvas.drawLine(x + velx / 200, y + vely / 200, x - velx / 200, y - vely / 200,
                paint);
    }
}
