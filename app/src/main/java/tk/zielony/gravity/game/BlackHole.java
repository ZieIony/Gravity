package tk.zielony.gravity.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;

import tk.zielony.gravity.settings.Setting;
import tk.zielony.gravity.settings.Settings;

class BlackHole extends SpaceObj {
    private static float density = 0.1f;
    private float accx, accy, forcex, forcey, velx, vely;
    private static Paint paint = new Paint();
    private List<PointF> particles = new ArrayList<PointF>();

    public BlackHole(float x, float y, float mass) {
        this.x = x;
        this.y = y;
        setMass(mass);
    }

    public void draw(Canvas canvas) {
        float r = (float) Math.sin((System.currentTimeMillis() % 5000)
                / 5000.0f * 2 * Math.PI)
                * radius / 3 + radius;
        paint.setAlpha(255);
        RadialGradient g = new RadialGradient(x, y, r, new int[]{0x00000000,
                Color.argb(64, 156, 53, 200), 0x00000000}, null,
                Shader.TileMode.CLAMP);
        paint.setShader(g);
        canvas.drawCircle(x, y, r, paint);
        paint.setShader(null);

        if (Math.random() < 0.4) {
            PointF p = new PointF(radius * 3, 0);
            particles.add(p);
            float angle = (float) (Math.random() * 2 * Math.PI);
            float px = (float) (p.x * Math.cos(angle) - p.y * Math.sin(angle));
            float py = (float) (p.x * Math.sin(angle) + p.y * Math.cos(angle));
            p.x = px + x;
            p.y = py + y;
        }

        List<PointF> toRemove = new ArrayList<PointF>();

        paint.setColor(0x7fffffff);
        for (PointF p : particles) {
            float dx = p.x - x;
            float dy = p.y - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < 1) {
                toRemove.add(p);
                continue;
            }
            p.x -= dx / dist * 2;
            p.y -= dy / dist * 2;
            paint.setAlpha(Math.max(0,
                    Math.min((int) ((1 - dist / radius / 3) * 255), 255)));
            canvas.drawCircle(p.x, p.y, 1, paint);
        }

        particles.removeAll(toRemove);
    }

    public void setMass(float mass) {
        this.mass = mass;
        radius = (float) Math.pow(3.0f / 4.0f / Math.PI * mass / density,
                1.0f / 3.0f);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        mass = (float) (4.0 / 3.0 * Math.PI * Math.pow(radius, 3) * density);
    }

    @Override
    public String toString() {
        return "h" + hashCode() + " (" + mass + ")";
    }

    public void animate(float ax, float ay) {
        if (Settings.get(Setting.ACCELEROMETER)) {
            forcex -= ay / 2;
            forcey -= ax / 2;
        }
        accx = forcex / mass;
        accy = forcey / mass;
        velx += accx;
        vely += accy;
        x += velx;
        y += vely;
        forcex = 0;
        forcey = 0;
    }
}