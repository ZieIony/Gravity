package tk.zielony.gravity.game;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class SpaceShip {
	public float x, y;
	public PointF vel = new PointF();
	static Paint paint = new Paint();
	public int color = colors[random.nextInt(colors.length)];
	static int[] colors = new int[]{Color.RED,Color.GREEN,0xff007fff,0xffffaf00};
	public static Random random = new Random();
	public static ArrayList<SpaceShip> toRemove = new ArrayList<SpaceShip>();
	
	public SpaceShip(float x2, float y2, float velx, float vely) {
		x = x2;
		y = y2;
		vel.x = Math.max(-2, Math.min(velx,2));
		vel.y = Math.max(-2, Math.min(vely,2));
	}

	public void animate(){
		vel.x+=Math.random()*2-1;
		vel.x = Math.max(-2, Math.min(vel.x,2));
		vel.y+=Math.random()*2-1;
		vel.y = Math.max(-2, Math.min(vel.y,2));
		
		x+=vel.x;
		y+=vel.y;
	}

	public void draw(Canvas canvas) {
		paint.setColor(0x7fffffff);
		canvas.drawRect(x - 5, y - 5, x + 5, y + 5, paint);
	}
}
