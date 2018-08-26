package com.thisispiri.film;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**The service used to put a simple color over the screen.
 * Supply {@link FilmService#ACTION_TURN_ON}, {@link FilmService#ACTION_TURN_OFF}, {@link FilmService#ACTION_TOGGLE}, {@link FilmService#ACTION_NO_TOGGLE} as you need. The default is {@link FilmService#ACTION_NO_TOGGLE}
 * Put "color" {@code int} extra in the {@code Intent} when calling {@code startService()} to change the color of the overlay.*/
public class FilmService extends Service {
	public final static String ACTION_TURN_ON = "TURN_ON", ACTION_TURN_OFF = "TURN_OFF", ACTION_TOGGLE = "TOGGLE", ACTION_NO_TOGGLE = "NO_TOGGLE";
	public final static String CHANNEL_ID = "PIRI Film";
	private final static int MAIN_REQUEST_CODE = 0, COLOR_REQUEST_CODE = 1, DISABLE_REQUEST_CODE = 2, NOTIFICATION_ID = 1;
	private SharedPreferences preferences;
	private WindowManager windowManager;
	private NotificationManager notificationManager;
	private NotificationCompat.Builder builder;
	private View film;
	private boolean enabled = false;
	/**This {@code Service} doesn't support binding.
	 * @return {@code null}*/
	@Override public IBinder onBind(final Intent intent) {return null;}
	@Override public void onCreate() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		notificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
		if(Build.VERSION.SDK_INT >= 26) { //If Oreo or newer version is installed, create a NotificationChannel before making Notifications.
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.controlPanel), NotificationManager.IMPORTANCE_DEFAULT);
			channel.enableLights(false);
			channel.enableVibration(false);
			notificationManager.createNotificationChannel(channel);
		}
		builder = new NotificationCompat.Builder(this, CHANNEL_ID);
		startForeground(NOTIFICATION_ID, builder.setSmallIcon(R.drawable.notification).setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.overlayActive))
				.setContentIntent(PendingIntent.getActivity(this, MAIN_REQUEST_CODE, new Intent(this, SettingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
				.addAction(R.drawable.brush, getString(R.string.color), PendingIntent.getActivity(this, COLOR_REQUEST_CODE, new Intent(this, DialogActivity.class).putExtra("dialogFragmentClass", FilmArgbDialogFragment.class), PendingIntent.FLAG_UPDATE_CURRENT))
				.addAction(R.drawable.power, getString(R.string.toggle), PendingIntent.getService(this, DISABLE_REQUEST_CODE, new Intent(this, FilmService.class).setAction(ACTION_TOGGLE), PendingIntent.FLAG_UPDATE_CURRENT)).build());
		film = new View(this);
		film.setBackgroundColor(preferences.getInt(getString(R.string.colorKey), 0x33000000));
		//TODO: Make this work under SDK 23
		windowManager.addView(film, new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
				Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FORMAT_CHANGED));
	}
	/**Adds the {@code film} to {@code windowManager} if it hasn't been added. Changes the color to "color" int extra of {@code intent}.*/
	@Override public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if(intent != null) {
			if(intent.getAction() != null) {
				switch (intent.getAction()) {
					case ACTION_TURN_ON:
						toggleFilm(true);
						break;
					case ACTION_TURN_OFF:
						toggleFilm(false);
						break;
					case ACTION_TOGGLE:
						toggleFilm(!enabled);
					//case ACTION_NO_TOGGLE:default:
				}
			}
			film.setBackgroundColor(intent.getIntExtra(getString(R.string.colorKey), preferences.getInt("color", 0x33000000)));
		}
		else { //null Intent by the system
			film.setBackgroundColor(preferences.getInt(getString(R.string.colorKey), 0x33000000));
			toggleFilm(true);
		}
		return START_STICKY;
	}
	/**Removes {@code film} from {@code windowManager} if it has been added and sets "enabled" preference to false.*/
	public void onDestroy() {
		windowManager.removeView(film);
		preferences.edit().putBoolean(getString(R.string.enabledKey), false).apply();
	}
	/**Toggles whether the film is shown or not. Does nothing if {@code enable} is true/false and the film is already enabled/disabled.
	 * Also updates the content text shown in the control panel Notification.
	 * @param enable true if enabling the film. false if disabling.*/
	private void toggleFilm(final boolean enable) {
		if(enable && !enabled) {
			if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) { //The permission is automatically granted at API levels under 23.
				film.setVisibility(View.VISIBLE);
				enabled = true;
				builder.setContentText(getString(R.string.overlayActive));
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
			else { //Take the user to the screen where he can grant the overlay permission
				Toast.makeText(this, R.string.permissionNeeded, Toast.LENGTH_SHORT).show();
				startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
			}
		}
		else if(enabled) {
			film.setVisibility(View.GONE);
			enabled = false;
			builder.setContentText(getString(R.string.overlayDisabled));
			notificationManager.notify(NOTIFICATION_ID, builder.build());
		}
	}
}
