package tk.zielony.gravity.settings;

import tk.zielony.gravity.GravityApplication;
import tk.zielony.gravity.R;
import tk.zielony.gravity.R.id;
import tk.zielony.gravity.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

		final ListView listView = (ListView) findViewById(R.id.settings_list);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, settings));
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		for (int i = 0; i < Setting.values().length; i++) {
			listView.setItemChecked(i, Settings.get(Setting.values()[i]));
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Settings.set(Setting.values()[arg2],
						listView.isItemChecked(arg2));
			}
		});
	}
}
