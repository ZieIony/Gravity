package tk.zielony.gravity.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tk.zielony.gravity.settings.Setting;
import tk.zielony.gravity.settings.Settings;

public class GamePanel extends View implements SensorEventListener {
    private static final int MIN_Y = -2;
    private static final int MIN_X = -2;
    private static final int MAX_X = 2;
    private static final int MAX_Y = 2;
    Paint paint = new Paint();
    List<Planet> planets = new ArrayList<>();
    List<BlackHole> holes = new ArrayList<>();
    List<Star> stars = new ArrayList<>();
    List<SpaceShip> spaceShips = new ArrayList<>();
    List<Explosion> explosions = new ArrayList<>();
    private long prevAdd = System.currentTimeMillis();
    private PointF prevPos = new PointF(), prevPos2 = new PointF();
    private float flash = 0;
    float scale = 1;
    final float MIN_SCALE = 0.2f, MAX_SCALE = 2.0f;
    float rotation = 0;
    Matrix matrix = new Matrix();
    Random random = new Random();
    List<Laser> lasers = new ArrayList<>();

    public GamePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GamePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GamePanel(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        for (int i = 0; i < 100; i++) {
            stars.add(new Star((float) Math.random(), (float) Math.random(),
                    (float) Math.random() / 2));
        }
        spaceShips.add(new SpaceShip(10, 10, 0,0, Color.GREEN, getResources()));
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorManager sensorManager;
    double rawAx, rawAy; // these are the acceleration in x,y and z axis
    private float prevDist;
    private boolean tapStarted = false;
    private AchievementListener achievementListener;
    private long prevTime;
    private long frameTime;
    private int frames;
    private long startTime = System.currentTimeMillis();
    private long actions;
    private float fps;
    private float apm;
    private float[] points = new float[2];
    public static float smoothness = 1;

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            rawAx = event.values[0] / 10;
            rawAy = -event.values[1] / 10;
            if (event.values[2] < -9)
                fireAchievementEvent(Achievement.UPSIDE_DOWN);
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        canvas.drawColor(Color.rgb((int) Math.max(0, flash * 256),
                (int) Math.max(0, flash * 256), (int) Math.max(0, flash * 256)));
        flash -= 0.1;

        canvas.save();
        canvas.setMatrix(matrix);

        doPhysics();

        paint.setColor(0x7fffffff);
        paint.setStyle(Style.STROKE);
        canvas.drawRect(MIN_X * getWidth(), MIN_Y * getHeight(), MAX_X
                * getWidth(), MAX_Y * getHeight(), paint);
        paint.setStyle(Style.FILL_AND_STROKE);

        paint.setAlpha(255);
        for (int x = MIN_X; x < MAX_X; x++) {
            for (int y = MIN_Y; y < MAX_Y; y++) {
                for (Star s : stars) {
                    if (scale * s.z < 0.15)
                        continue;
                    paint.setColor(((int) (s.z * 0xff) << 24) + 0xffffff);
                    canvas.drawCircle(getWidth() * (s.x + x), getHeight()
                            * (s.y + y), 4 * s.z, paint);
                }
            }
        }

        for (Planet p : planets) {
            p.draw(canvas);
        }

        for (BlackHole h : holes) {
            h.draw(canvas);
        }

        paint.setAlpha(255);
        paint.setColor(Color.WHITE);
        for (SpaceShip s : spaceShips) {
            s.draw(canvas);
        }

        for (Laser l : lasers) {
            l.draw(canvas);
        }

        for (Explosion e : explosions) {
            e.draw(canvas);
        }

        canvas.restore();

        if (Settings.get(Setting.LABELS)) {
            for (BlackHole h : holes) {
                drawLabel(canvas, scale, h);
            }

            for (Planet p : planets) {
                drawLabel(canvas, scale, p);
            }
        }

        if (prevTime != 0) {
            frames++;
            if (frames == 10) {
                fps = 10000.0f / frameTime;
                if (fps < 10)
                    fireAchievementEvent(Achievement.SLOW_JAVA);
                frameTime = 0;
            }
            frames %= 10;
            frameTime += System.currentTimeMillis() - prevTime;
        }
        smoothness = Math.min(
                Math.max(
                        1000.0f / Math.max(1, System.currentTimeMillis()
                                - prevTime), 1) / 60, 1);
        prevTime = System.currentTimeMillis();

        long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (timeElapsed > 0)
            apm = actions / (timeElapsed / 60.0f);

        if (timeElapsed > 20 * 60)
            fireAchievementEvent(Achievement.GOOD_GAME);

        if (timeElapsed > 60 && apm < 5)
            fireAchievementEvent(Achievement.LOW_APM);

        if (timeElapsed > 60 && apm > 1000)
            fireAchievementEvent(Achievement.HIGH_APM);

        paint.setColor(0x7fffffff);
        final float margin = 10, scaleLength = 100, serif = 5;
        canvas.drawLine(margin, margin, margin + scaleLength, margin, paint);
        canvas.drawLine(margin, margin, margin, margin + serif, paint);
        canvas.drawLine(margin + scaleLength, margin, margin + scaleLength,
                margin + serif, paint);
        canvas.drawLine(margin + serif / 2.0f, margin + serif / 2, margin
                + (scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE)
                * (scaleLength - serif) + serif / 2, margin + serif / 2, paint);

        /*
         * paint.setColor(Color.WHITE); paint.setAlpha(255);
         * canvas.drawText("apm: " + apm + "\nfps: " + fps + "\ntime: " +
         * timeElapsed, 50, 50, paint);
         */

        invalidate();
    }

    private void drawLabel(Canvas canvas, float currentScale, SpaceObj p) {
        points[0] = p.x;
        points[1] = p.y;
        matrix.mapPoints(points);

        if (points[0] > 0 && points[0] < getWidth() && points[1] > 0
                && points[1] < getHeight() && p.radius * currentScale > 3) {
            float x2 = (points[0] + getWidth()) / 2;
            float y2 = (points[1] + getHeight()) / 2;
            float len = (float) Math.sqrt(Math.pow(x2 - points[0], 2)
                    + Math.pow(y2 - points[1], 2));
            float x = points[0] + (x2 - points[0]) / len * p.radius
                    * currentScale * 2;
            float y = points[1] + (y2 - points[1]) / len * p.radius
                    * currentScale * 2;

            String label = p.toString();
            paint.setTextSize(15);
            float strLen = paint.measureText(label), margin = 5;
            paint.setColor(0x7fffffff);
            canvas.drawLine(x, y, x2, y2, paint);
            canvas.drawLine(x2, y2, x2 + strLen, y2, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(label, x2, y2 - margin, paint);
        }
    }

    float lerp(float val1, float val2, float part) {
        return val1 * part + val2 * (1 - part);
    }

    int lerp(int val1, int val2, float part) {
        return (int) (val1 * part + val2 * (1 - part));
    }

    private void doPhysics() {
        Planet.toRemove.clear();
        Planet.toAdd.clear();
        SpaceShip.Companion.getToRemove().clear();
        Laser.toRemove.clear();

        float dt = (System.currentTimeMillis() - prevTime) / 1000.0f;

        float ax = (float) (rawAx * Math.cos(-rotation) - rawAy
                * Math.sin(-rotation));
        float ay = (float) (rawAy * Math.cos(-rotation) + rawAx
                * Math.sin(-rotation));

        for (Star s : stars) {
            s.animate(ax, ay);
        }
        for (Planet p : planets) {
            p.animate(ax, ay);
        }

        for (Planet p : planets) {
            if (p.mass > 8000 && Math.random() < 0.01) {
                if (p.mass > 15000 && Settings.get(Setting.BLACK_HOLES)) {
                    Planet.toRemove.add(p);
                    flash = 1;
                    holes.add(new BlackHole(p.x, p.y, p.mass));
                } else if (Settings.get(Setting.UNSTABLE_STARS)) {
                    Planet.toRemove.add(p);
                    flash = 1;
                    int total = (int) (Math.random() * 4 + 4);
                    for (int i = 0; i < total; i++) {
                        Planet p3 = new Planet(p.x, p.y, p.mass / total,
                                p.color);
                        p3.velx = 10;
                        p3.vely = 0;
                        float angle = (float) (i / (float) total * 2 * Math.PI);
                        float px = (float) (p3.velx * Math.cos(angle) - p3.vely
                                * Math.sin(angle));
                        float py = (float) (p3.velx * Math.sin(angle) + p3.vely
                                * Math.cos(angle));
                        p3.velx = (float) (px / Math.sqrt(px * px + py * py) * p.radius);
                        p3.vely = (float) (py / Math.sqrt(px * px + py * py) * p.radius);
                        p3.x += p3.velx;
                        p3.y += p3.vely;
                        p3.velx = p3.velx / 2 + p.velx;
                        p3.vely = p3.vely / 2 + p.vely;
                        Planet.toAdd.add(p3);
                    }
                }
            }

            for (Planet p2 : planets) {
                if (p == p2)
                    continue;
                float d = dist(p.x, p.y, p2.x, p2.y);
                float dirx = (p2.x - p.x) / d;
                float diry = (p2.y - p.y) / d;
                p.forcex += dirx * p.mass * p2.mass / d / d;
                p.forcey += diry * p.mass * p2.mass / d / d;
            }

            for (BlackHole h : holes) {
                float d = dist(p.x, p.y, h.x, h.y);
                float dirx = (h.x - p.x) / d;
                float diry = (h.y - p.y) / d;
                p.forcex += dirx * p.mass * h.mass / d / d;
                p.forcey += diry * p.mass * h.mass / d / d;

                if (d < 10) {
                    Planet.toRemove.add(p);
                    h.setMass(h.mass + p.mass);
                }
            }

            if (p.radius < 1)
                Planet.toRemove.add(p);
        }

        for (int i = 0; i < planets.size(); i++) {
            for (int j = i + 1; j < planets.size(); j++) {
                Planet p = planets.get(i);
                Planet p2 = planets.get(j);

                float d = dist(p.x, p.y, p2.x, p2.y);

                if (d <= p.radius + p2.radius) {
                    if (p.mass > 2 * p2.mass) {
                        float part = p.mass / (p.mass + p2.mass);
                        p.velx = lerp(p.velx, p2.velx, part);
                        p.vely = lerp(p.velx, p2.velx, part);
                        p.color = Color.rgb(
                                lerp(Color.red(p.color), Color.red(p2.color),
                                        part),
                                lerp(Color.green(p.color),
                                        Color.green(p2.color), part),
                                lerp(Color.blue(p.color), Color.blue(p2.color),
                                        part));
                        p.color2 = Color.rgb(
                                lerp(Color.red(p.color2), Color.red(p2.color2),
                                        part),
                                lerp(Color.green(p.color2),
                                        Color.green(p2.color2), part),
                                lerp(Color.blue(p.color2),
                                        Color.blue(p2.color2), part));
                        p.setMass(p.mass + p2.mass);
                        Planet.toRemove.add(p2);
                    } else if (Settings.get(Setting.COLLISIONS)) {
                        float deltax = p.x - p2.x;
                        float deltay = p.y - p2.y;
                        float delta = (float) Math.sqrt(deltax * deltax
                                + deltay * deltay);
                        // minimum translation distance to push balls apart
                        // after intersecting
                        float mtdx = deltax * (p.radius + p2.radius - delta)
                                / delta;
                        float mtdy = deltay * (p.radius + p2.radius - delta)
                                / delta;

                        // resolve intersection --
                        // inverse mass quantities
                        float im1 = 1 / p.mass;
                        float im2 = 1 / p2.mass;

                        // push-pull them apart based off their mass
                        p.x += mtdx * (im1 / (im1 + im2));
                        p.y += mtdy * (im1 / (im1 + im2));
                        p2.x -= mtdx * (im2 / (im1 + im2));
                        p2.y -= mtdy * (im2 / (im1 + im2));

                        // impact speed
                        float vx = p.velx - p2.velx;
                        float vy = p.vely - p2.vely;
                        float length = (float) Math.sqrt(mtdx * mtdx + mtdy
                                * mtdy);
                        float normalizedx = mtdx / length;
                        float normalizedy = mtdy / length;
                        float vn = vx * normalizedx + vy * normalizedy;

                        // sphere intersecting but moving away from each other
                        // already
                        if (vn > 0.0f) {
                        } else {

                            // collision impulse
                            float imp = (-2 * vn) / (im1 + im2);
                            float impulsex = normalizedx * imp;
                            float impulsey = normalizedy * imp;

                            // change in momentum
                            p.velx += impulsex * im1;
                            p.vely += impulsey * im1;
                            p2.velx -= impulsex * im2;
                            p2.vely -= impulsey * im2;
                        }
                        /*
                         * float dot = p.velx * dir.x + p.vely * dir.y; p.velx =
                         * -2 * dot * dir.x + p.velx; // TODO: fix collisions
                         * p.vely = -2 * dot * dir.y + p.vely;
                         */
                    }
                }
            }
        }

        for (int i = 0; i < spaceShips.size(); i++) {
            SpaceShip s = spaceShips.get(i);
            s.animate();
            if (spaceShips.size() > 1 && Math.random() < 0.02f) {
                SpaceShip s2 = spaceShips.get((i + 1) % spaceShips.size());
                float x = s2.getX() - s.getX();
                float y = s2.getY() - s.getY();
                float d = (float) Math.sqrt(x * x + y * y);
                PointF p = rotate(x / d * 1000, y / d * 1000,
                        (float) (Math.random() * Math.PI / 45));
                lasers.add(new Laser(s.getX(), s.getY(), p.x, p.y, s.getColor()));
            }
            if (s.getX() < MIN_X * getWidth() || s.getX() > MAX_X * getWidth()
                    || s.getY() < MIN_Y * getHeight() || s.getY() > MAX_Y * getHeight())
                SpaceShip.Companion.getToRemove().add(s);
        }

        for (Explosion e : explosions)
            e.animate(dt);

        for (Laser l : lasers) {
            l.animate(dt);
            for (Planet p : planets) {
                if (dist(p.x, p.y, l.x, l.y) < p.radius) {
                    Laser.toRemove.add(l);
                    Planet.toRemove.add(p);
                    explosions.add(new Explosion(p.x, p.y, p.radius));
                    break;
                }
            }
            float shipRadius = 5;
            for (SpaceShip s : spaceShips) {
                if (dist(s.getX(), s.getY(), l.x, l.y) < shipRadius) {
                    Laser.toRemove.add(l);
                    SpaceShip.Companion.getToRemove().add(s);
                    explosions.add(new Explosion(s.getX(), s.getY(), shipRadius));
                    break;
                }
            }
            if (l.x < MIN_X * getWidth() || l.x > MAX_X * getWidth()
                    || l.y < MIN_Y * getHeight() || l.y > MAX_Y * getHeight())
                Laser.toRemove.add(l);
        }

        for (BlackHole h : holes) {
            h.animate(ax, ay);
        }

        for (Planet p : planets) {
            if (Math.random() < 0.0001f)
                spaceShips.add(new SpaceShip(p.x, p.y, p.velx, p.vely, p.color, getResources()));

            if (p.x == Float.NaN) // TODO: fix it later
                Planet.toRemove.add(p);
            if (p.x < MIN_X * getWidth() || p.x > MAX_X * getWidth()
                    || p.y < MIN_Y * getHeight() || p.y > MAX_Y * getHeight())
                Planet.toRemove.add(p);
        }

        planets.addAll(Planet.toAdd);
        planets.removeAll(Planet.toRemove);
        lasers.removeAll(Laser.toRemove);
        spaceShips.removeAll(SpaceShip.Companion.getToRemove());

        for (int i = 0; i < explosions.size(); ) {
            if (explosions.get(i).isFinished()) {
                explosions.remove(i);
                continue;
            }
            i++;
        }
    }

    private float dist(float x, float y, float x2, float y2) {
        return (float) Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
    }

    public boolean dispatchTouchEvent(android.view.MotionEvent event) {
        synchronized (this) {
            actions++;
            if (event.getPointerCount() == 1) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tapStarted = true;
                    prevAdd = System.currentTimeMillis();
                    prevPos.x = event.getX();
                    prevPos.y = event.getY();
                    prevPos2.x = event.getX();
                    prevPos2.y = event.getY();
                }
                if (tapStarted && event.getAction() == MotionEvent.ACTION_UP) {
                    int color = Color.rgb((int) (Math.random() * 128 + 128),
                            (int) (Math.random() * 128 + 128),
                            (int) (Math.random() * 128 + 128));
                    Matrix inverse = new Matrix();
                    matrix.invert(inverse);
                    points[0] = event.getX();
                    points[1] = event.getY();
                    inverse.mapPoints(points);
                    Planet p = new Planet(points[0], points[1], 1, color);
                    p.setRadius(Math.max(2,
                            (System.currentTimeMillis() - prevAdd) / 100.0f));
                    // p.ring = Math.random()>0.8;

                    float r = (float) Math.random();
                    if (r < 0.01) {
                        p.color = 0xffFF7C00;
                        p.color2 = 0xffFFD60C;
                        p.label = "The Sun " + random.nextInt(50);
                        p.setRadius(15);
                        fireAchievementEvent(Achievement.CREATE_SUN);
                    } else if (r < 0.02) {
                        p.color = 0xff559B27;
                        p.color2 = 0xff00A5FF;
                        p.label = "The Earth " + random.nextInt(50);
                        p.setRadius(7);
                        fireAchievementEvent(Achievement.CREATE_EARTH);
                    } else if (r < 0.03) {
                        p.color = 0xffFF3330;
                        p.color2 = 0xffFF3330;
                        p.label = "Mars " + random.nextInt(50);
                        p.setRadius(7);
                        fireAchievementEvent(Achievement.CREATE_MARS);
                    } else if (r < 0.04) {
                        p.color = 0xffFFE189;
                        p.color2 = 0xffFFE189;
                        p.label = "Saturn " + random.nextInt(50);
                        p.setRadius(12);
                        p.ring = true;
                        fireAchievementEvent(Achievement.CREATE_SATURN);
                    }

                    points[0] = prevPos2.x;
                    points[1] = prevPos2.y;
                    inverse.mapPoints(points);
                    p.velx = (p.x - points[0]) / 2.0f;
                    p.vely = (p.y - points[1]) / 2.0f;

                    planets.add(p);
                    tapStarted = false;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    prevPos2.x = prevPos.x;
                    prevPos2.y = prevPos.y;
                    prevPos.x = event.getX();
                    prevPos.y = event.getY();
                }
                prevDist = 0;

            } else if (event.getPointerCount() == 2) {
                tapStarted = false;
                float d = dist(event.getX(0), event.getY(0), event.getX(1),
                        event.getY(1));
                float pivotX = (event.getX(0) + event.getX(1)) / 2;
                float pivotY = (event.getY(0) + event.getY(1)) / 2;
                float prevPivotX = (prevPos.x + prevPos2.x) / 2;
                float prevPivotY = (prevPos.y + prevPos2.y) / 2;
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float newScale = scale * d / prevDist;
                    newScale = Math.max(MIN_SCALE,
                            Math.min(newScale, MAX_SCALE));
                    float scaleFactor = newScale / scale;
                    scale = newScale;

                    matrix.postScale(scaleFactor, scaleFactor, pivotX, pivotY);
                    float prevAngle = (float) Math.atan2(
                            prevPos.x - prevPos2.x, prevPos.y - prevPos2.y);
                    float angle = (float) Math.atan2(
                            event.getX(0) - event.getX(1), event.getY(0)
                                    - event.getY(1));
                    rotation += prevAngle - angle;
                    matrix.postRotate(
                            (float) ((prevAngle - angle) * 180.0f / Math.PI),
                            pivotX, pivotY);

                    matrix.postTranslate(-prevPivotX + pivotX, -prevPivotY
                            + pivotY);
                }
                prevPos.x = event.getX(0);
                prevPos.y = event.getY(0);
                prevPos2.x = event.getX(1);
                prevPos2.y = event.getY(1);
                prevDist = d;
            } else {
                prevDist = 0;
            }
            return true;
        }
    }

    private void fireAchievementEvent(Achievement type) {
        if (achievementListener != null) {
            achievementListener.onAchievement(type);
        }
    }

    public void setAchievementListener(AchievementListener achievementListener2) {
        this.achievementListener = achievementListener2;
    }

    public void save(File file) {
    }

    public void load(File file) throws IOException {
        holes.clear();
        planets.clear();
        spaceShips.clear();
        lasers.clear();
        DataInputStream stream = new DataInputStream(new BufferedInputStream(
                new FileInputStream(file)));
        int holesCount = stream.readInt();
        for (int i = 0; i < holesCount; i++) {
            float x = stream.readFloat();
            float y = stream.readFloat();
            float mass = stream.readFloat();
            holes.add(new BlackHole(x, y, mass));
        }
        int planetsCount = stream.readInt();
        for (int i = 0; i < planetsCount; i++) {
            float x = stream.readFloat();
            float y = stream.readFloat();
            float mass = stream.readFloat();
            int color = stream.readInt();
            int color2 = stream.readInt();
            float velx = stream.readFloat();
            float vely = stream.readFloat();
            Planet p = new Planet(x, y, mass, color);
            p.color2 = color2;
            p.velx = velx;
            p.vely = vely;
        }
    }

    public float getScale() {
        return scale;
    }

    public int getStars() {
        return planets.size();
    }

    public void scaleTo(float scale) {
        matrix.postScale(scale / this.scale, scale / this.scale,
                getWidth() / 2, getHeight() / 2);
        this.scale = scale;
    }

    public PointF rotate(float x, float y, float angle) {
        float sn = (float) Math.sin(angle);
        float cs = (float) Math.cos(angle);
        float px = x * cs - y * sn;
        float py = x * sn + y * cs;
        return new PointF(px, py);
    }
}
