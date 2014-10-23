package no.yaff.hangmanoblig1;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AltMenu extends Activity {
	public static final int norwegian = 0;
	public static final int english = 1;
	public static final String langKey = "lang_key";

	ImageButton flag;
	SharedPreferences langPrefs;
	Menu actionBarMenu;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);
		langPrefs = getSharedPreferences("language", MODE_PRIVATE);
		addListenerOnButton();
	}

	// Initialiserer og legger til lytter på knapp
	public void addListenerOnButton() {

		flag = (ImageButton) findViewById(R.id.flag_imageButton);

		flag.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String curLang = langPrefs.getString("languageToLoad", "en");
				if (curLang.equalsIgnoreCase("en"))
					setLanguage("nb");
				else
					setLanguage("en");
			}
		});

	}

	// Endrer språk i sharedPrefs
	private void setLanguage(String lang) {
		Locale locale = new Locale(lang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		SharedPreferences.Editor editor = langPrefs.edit();
		editor.putString("languageToLoad", lang);
		editor.commit();

		String language = "Norsk";

		if (lang.equalsIgnoreCase("en"))
			language = "English";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.lang_string));
		builder.setMessage(getString(R.string.langSet) + " " + language + ". "
				+ getString(R.string.back));
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				setResult(0);
				finish();
			}
		});
		builder.setNegativeButton(R.string.action_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						recreate();
					}
				});
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		actionBarMenu = menu;
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_close) {
			setResult(1);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
