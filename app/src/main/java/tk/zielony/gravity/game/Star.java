package tk.zielony.gravity.game;

import tk.zielony.gravity.settings.Setting;
import tk.zielony.gravity.settings.Settings;

class Star {
    float x, y, z;

    public Star(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void animate(float ax, float ay) {
        if (Settings.get(Setting.ACCELEROMETER)) {
            x -= ax * z / 1000;
            x++;
            x %= 1;
            y -= ay * z / 1000;
            y++;
            y %= 1;
        }

    }
}