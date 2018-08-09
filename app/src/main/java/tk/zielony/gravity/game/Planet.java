package tk.zielony.gravity.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;

import tk.zielony.gravity.settings.Setting;
import tk.zielony.gravity.settings.Settings;

class Planet extends SpaceObj {
    private static float density = 0.1f;
    int color;
    int color2;
    float forcex, forcey, velx, vely, accx, accy;
    public String label;
    private List<PointF> prevPos = new ArrayList<PointF>();
    public boolean ring;

    private static Paint paint = new Paint();
    static List<Planet> toRemove = new ArrayList<Planet>();
    static List<Planet> toAdd = new ArrayList<Planet>();

    public Planet(float x2, float y2, float mass, int c) {
        x = x2;
        y = y2;
        color = c;
        color2 = c;
        setMass(mass);
    }

    public String toString() {
        if (label == null)
            return "" + hashCode() + " (" + mass + ")";
        return label + " (" + mass + ")";
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

    public void draw(Canvas canvas) {
        paint.setColor(color2);
        paint.setAlpha((int) Math.min(127,
                Math.sqrt(velx * velx + vely * vely) * 5));
        for (int i = 0; i < prevPos.size() * GamePanel.smoothness; i++) {
            PointF pos = prevPos.get(i);
            canvas.drawCircle(pos.x, pos.y, radius * (prevPos.size() - i)
                    / (float) prevPos.size(), paint);
        }

        if (radius > 5) {
            paint.setAlpha(64);
            RadialGradient g;
            if (ring) {
                g = new RadialGradient(x, y, radius * 3, new int[]{
                        0xffffff & color2, color2, 0xffffff & color2},
                        new float[]{0.5f, 0.75f, 1.0f},
                        Shader.TileMode.CLAMP);
                paint.setShader(g);
                canvas.drawCircle(x, y, radius * 3, paint);
            } else {
                g = new RadialGradient(x, y, radius * 5, color2,
                        0xffffff & color2, Shader.TileMode.CLAMP);
                paint.setShader(g);
                canvas.drawCircle(x, y, radius * 5, paint);
            }
            paint.setShader(null);
        }
        {
            /*
             * canvas.save(); float velMag = (float) Math.sqrt(velx * velx +
             * vely vely); canvas.translate(x, y); canvas.rotate((float)
             * (Math.atan2(vely,velx) * 180 / Math.PI)); canvas.scale(velMag /
             * 30 + 1, 1);
             */
            paint.setAlpha(255);
            /*
             * RadialGradient g = new RadialGradient(x - radius / 2, y - radius
             * / 2, radius, 0xffffffff, color, Shader.TileMode.CLAMP);
             * paint.setShader(g);
             */
            paint.setColor(color);
            canvas.drawCircle(x, y, radius, paint);
            // paint.setShader(null);
            // canvas.restore();
        }
    }

    public void animate(float ax, float ay) {

        prevPos.add(0, new PointF(x, y));
        if (prevPos.size() > 5)
            prevPos.remove(prevPos.size() - 1);

        if (Settings.get(Setting.ACCELEROMETER)) {
            forcex -= ax;
            forcey -= ay;
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