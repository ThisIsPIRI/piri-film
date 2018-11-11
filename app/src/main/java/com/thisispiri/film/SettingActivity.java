package com.thisispiri.film;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**Houses {@link SettingFragment}.*/
public class SettingActivity extends AppCompatActivity {
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
	}
}
