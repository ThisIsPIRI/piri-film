package com.thisispiri.film;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;
import com.thisispiri.dialogs.DialogListener;

/**An {@code Activity} for showing a {@code Dialog} from {@code Notification}s. It is closed after getting the result from the {@code Dialog}.
 * Use translucent themes to make it transparent.*/
public class DialogActivity extends FragmentActivity implements DialogListener {
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			DialogFragment fragment = ((DialogFragment) ((Class) getIntent().getSerializableExtra("dialogFragmentClass")).newInstance());
			fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
		}
		catch(InstantiationException e) {
			Toast.makeText(this, "Failed to instantiate DialogFragment for the action", Toast.LENGTH_SHORT).show(); finish();
		}
		catch(IllegalAccessException e) {
			Toast.makeText(this, "Failed to access the DialogFragment class.", Toast.LENGTH_SHORT).show(); finish();
		}
	}

	@Override public <T>void giveResult(final T result, final Bundle bundle) {finish();}
}
