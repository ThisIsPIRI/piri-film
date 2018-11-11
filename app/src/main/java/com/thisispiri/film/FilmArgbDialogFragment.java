package com.thisispiri.film;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.app.AlertDialog;
import android.view.KeyEvent;
import com.thisispiri.dialogs.ArgbDialogFragment;
import com.thisispiri.dialogs.DialogListener;

/**Application-specific {@code ArgbDialogFragment}. Saves the result in the default {@code SharedPreference} and starts {@code FilmService} when the positive button is pressed.
 * Passes null to {@code DialogListener.giveResult()}*/
public class FilmArgbDialogFragment extends ArgbDialogFragment {
	DialogListener listener;
	@Override @NonNull public Dialog onCreateDialog(final Bundle savedInstanceState) {
		AlertDialog.Builder builder = setUpContent();
		setColor(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("color", 0x33000000));
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int id) {
				PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("color", getColor()).apply();
				getActivity().startService(new Intent(getActivity(), FilmService.class).putExtra("color", getColor()));
				listener.giveResult(null, getArguments());
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int id) {
				listener.giveResult(null, getArguments());
			}
		});
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override public boolean onKey(final DialogInterface dialog, final int keyCode, final KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) listener.giveResult(null, getArguments()); //return to local if the user cancels connection
				return false;
			}
		});
		return dialog;
	}
	@Override public void onAttach(final Context context) {
		super.onAttach(context);
		listener = (DialogListener) context;
	}
}
