package com.thisispiri.film;

import android.app.Application;
import android.content.Intent;
import android.preference.PreferenceManager;

/**The {@code Application} class. Starts {@code FilmService} if it is enabled at creation.*/
public class FilmApplication extends Application {
	@Override public void onCreate() {
		super.onCreate();
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enabled", false)) startService(new Intent(this, FilmService.class).setAction(FilmService.ACTION_TURN_ON));
	}
}
