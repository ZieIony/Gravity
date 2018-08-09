package tk.zielony.gravity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.List;

import tk.zielony.gravity.game.Achievement;
import tk.zielony.gravity.settings.Settings;

public class GravityApplication extends Application {
    SharedPreferences achievementPreferences;
    List<Achievement> achievements = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        achievementPreferences = getSharedPreferences("achievements",
                Context.MODE_PRIVATE);
        for (Achievement a : Achievement.values()) {
            if (achievementPreferences.contains(a.toString()))
                achievements.add(a);
        }
        Settings.load(getApplicationContext());
    }

    public void addAchievement(Achievement type) {
        achievements.add(type);
        Editor editor = achievementPreferences.edit();
        editor.putBoolean(type.toString(), true);
        editor.commit();
    }
}
