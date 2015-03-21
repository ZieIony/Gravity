package tk.zielony.gravity;

import tk.zielony.gravity.game.AchievementType;

class Achievement {
	String title, description;

	AchievementType type;

	public Achievement(AchievementType type, String string, String string2) {
		this.type = type;
		title = string;
		description = string2;
	}
}