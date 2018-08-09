package tk.zielony.gravity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tk.zielony.gravity.settings.SettingsActivity;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.game_button).setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this,
                    GameActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.achievements_button).setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this,
                    AchievementsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this,
                    SettingsActivity.class);
            startActivity(intent);
        });
    }
}
