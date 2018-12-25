package com.thisispiri.film;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

/**Provides means to turn on/off the overlay or change its color.*/
public class SettingFragment extends PreferenceFragment {
	private final Preference.OnPreferenceChangeListener changeListener = new FilmListener();
	private Intent filmIntent;
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		filmIntent = new Intent(getActivity(), FilmService.class);
		findPreference(getString(R.string.enabledKey)).setOnPreferenceChangeListener(changeListener);
		findPreference(getString(R.string.colorKey)).setOnPreferenceChangeListener(changeListener);
	}
	/**Listens for changes in the preference screen.*/
	private class FilmListener implements Preference.OnPreferenceChangeListener {
		public boolean onPreferenceChange(final Preference preference, final Object value) {
			String key = preference.getKey();
			if(key.equals(getString(R.string.enabledKey))) {
				if ((boolean) value) {
					if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(getActivity().getApplicationContext())) { //The permission is automatically granted at API levels under 23.
						getActivity().startService(filmIntent.setAction(FilmService.ACTION_TURN_ON));
					}
					else {
						Toast.makeText(getActivity(), "The permission is needed for the application to work.", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
						return false;
					}
				}
				else getActivity().stopService(filmIntent);
			}
			else if(key.equals(getString(R.string.colorKey))) {
				filmIntent.putExtra("color", (int) value);
				if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("enabled", false))
					getActivity().startService(filmIntent.setAction(FilmService.ACTION_NO_TOGGLE));
			}
			return true;
		}
	}
}
