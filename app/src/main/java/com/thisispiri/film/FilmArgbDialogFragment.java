package com.thisispiri.film;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.KeyEvent;
import com.thisispiri.dialogs.ArgbDialogFragment;

/**Application-specific {@code ArgbDialogFragment}. Saves the result in the default {@code SharedPreference} and starts {@code FilmService} when the positive button is pressed.
 * Passes null to {@code DialogListener.giveResult()}*/
public class FilmArgbDialogFragment extends ArgbDialogFragment {
	@Override @NonNull public Dialog onCreateDialog(final Bundle savedInstanceState) {
		AlertDialog.Builder builder = setUpContent();
		setColor(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("color", 0x33000000));
		builder.setPositiveButton(R.string.confirm, (dialog, id) -> {
			PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("color", getColor()).apply();
			getActivity().startService(new Intent(getActivity(), FilmService.class).putExtra("color", getColor()));
			getListener().giveResult(null, getArguments());
		});
		builder.setNegativeButton(R.string.cancel, (dialog, id) -> getListener().giveResult(null, getArguments()));
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.setOnKeyListener((dialog1, keyCode, event) -> {
			if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
				getListener().giveResult(null, getArguments());
			return false;
		});
		return dialog;
	}
}
