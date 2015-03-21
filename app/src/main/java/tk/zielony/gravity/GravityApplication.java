package tk.zielony.gravity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.zielony.gravity.game.AchievementType;
import tk.zielony.gravity.settings.Settings;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GravityApplication extends Application {
	SharedPreferences achievementPreferences;
	List<AchievementType> achievements = new ArrayList<AchievementType>();

	@Override
	public void onCreate() {
		super.onCreate();
		achievementPreferences = getSharedPreferences("achievements",
				Context.MODE_PRIVATE);
		for (AchievementType a : AchievementType.values()) {
			if (achievementPreferences.contains(a.toString()))
				achievements.add(a);
		}
		Settings.load(getApplicationContext());
	}

	public void addAchievement(AchievementType type) {
		achievements.add(type);
		Editor editor = achievementPreferences.edit();
		editor.putBoolean(type.toString(), true);
		editor.commit();
	}
}
