package tk.zielony.gravity;

import tk.zielony.gravity.settings.SettingsActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MenuActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		findViewById(R.id.game_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MenuActivity.this,
								GameActivity.class);
						startActivity(intent);
					}
				});

		findViewById(R.id.achievements_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MenuActivity.this,
								AchievementsActivity.class);
						startActivity(intent);
					}
				});

		findViewById(R.id.settings_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MenuActivity.this,
								SettingsActivity.class);
						startActivity(intent);
					}
				});
	}
}
