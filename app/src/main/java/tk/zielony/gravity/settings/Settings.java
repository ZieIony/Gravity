package tk.zielony.gravity.settings;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {
	static SharedPreferences settingsPreferences;
	static Map<Setting, Boolean> settings = new HashMap<Setting, Boolean>();

	public static void set(Setting settings2, boolean itemChecked) {
		Editor edit = settingsPreferences.edit();
		edit.putBoolean(settings2.name(), itemChecked);
		edit.commit();
		settings.put(settings2, itemChecked);
	}

	public static void load(Context context) {
		settingsPreferences = context.getSharedPreferences("settings",
				Context.MODE_PRIVATE);
		for (Setting s : Setting.values()) {
			settings.put(s, settingsPreferences.getBoolean(s.name(), true));
		}
	}

	public static boolean get(Setting s) {
		return settings.get(s);
	}
}
