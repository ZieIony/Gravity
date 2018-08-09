package tk.zielony.gravity.settings;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import tk.zielony.gravity.GravityApplication;
import tk.zielony.gravity.R;

public class SettingsActivity extends Activity {
    GravityApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        application = (GravityApplication) getApplication();

        final String[] settings = new String[Setting.values().length];
        for (int i = 0; i < Setting.values().length; i++) {
            settings[i] = Setting.values()[i].toString();
        }

        final ListView listView = findViewById(R.id.settings_list);
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, settings));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for (int i = 0; i < Setting.values().length; i++) {
            listView.setItemChecked(i, Settings.get(Setting.values()[i]));
        }

        listView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> Settings.set(Setting.values()[arg2],
                listView.isItemChecked(arg2)));
    }
}
