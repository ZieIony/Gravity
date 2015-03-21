package tk.zielony.gravity.game;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Explosion {
	class Spark {
		float x, y, velx, vely, size, alpha = 255;

		public Spark(float x, float y, float velx, float vely, float size) {
			this.x = x;
			this.y = y;
			this.velx = velx;
			this.vely = vely;
			this.size = size;
		}
	}

	static Paint paint = new Paint();
	float x, y, size, time = 0, spawnTime = 0, totalTime = 0.5f;
	ArrayList<Spark> sparks = new ArrayList<Spark>();
	final static float spawnInterval = 0.05f;

	public Explosion(float x2, float y2, float radius) {
		x = x2;
		y = y2;
		size = radius;
	}

	public void animate(float dt) {
		if (time < totalTime) {
			time += dt;
			spawnTime += dt;

			while (spawnTime  > 0) {
				spawnTime-=spawnInterval;
				sparks.add(new Spark(x, y,
						(float) (Math.random() * 50 - 25), (float) (Math
								.random() * 50- 25), size/2));
			}
		}

		for (Spark s : sparks) {
			s.x += s.velx * dt;
			s.y += s.vely * dt;
			s.alpha -= 255* dt;
		}
	}

	public void draw(Canvas canvas) {
		for (int i = 0; i < sparks.size();) {
			Spark s = sparks.get(i);
			if (s.alpha <= 0) {
				sparks.remove(s);
				continue;
			}
			paint.setColor(0xffaf00);
			paint.setAlpha((int) s.alpha);
			canvas.drawCircle(s.x, s.y, s.size, paint);
			i++;
		}
	}

	public boolean isFinished() {
		return time>totalTime&&sparks.size() == 0;
	}
}
