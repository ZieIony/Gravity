package tk.zielony.gravity.game;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Laser {
	int color;
	float x, y, velx, vely;
	static Paint paint = new Paint();
	public static ArrayList<Laser> toRemove = new ArrayList<Laser>();

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
