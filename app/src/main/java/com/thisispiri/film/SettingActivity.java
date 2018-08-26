package com.thisispiri.film;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**Houses {@link SettingFragment}.*/
public class SettingActivity extends AppCompatActivity {
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
	}
}
