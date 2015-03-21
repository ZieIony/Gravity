package tk.zielony.gravity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AchievementsActivity extends Activity {
	GravityApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_achievements);

		application = (GravityApplication) getApplication();
		String[] achievementStrings = new String[application.achievements
				.size()];
		for (int i = 0; i < application.achievements.size(); i++) {
			achievementStrings[i] = application.achievements.get(i).toString();
		}

		ListView listView = (ListView) findViewById(R.id.achievements_list);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, achievementStrings));
	}

}
