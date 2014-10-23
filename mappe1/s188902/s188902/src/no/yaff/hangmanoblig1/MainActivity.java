package no.yaff.hangmanoblig1;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	Button startButton, statButton, altButton, rulesButton;
	SharedPreferences sharedPrefs;
	final Context context = this;
	ImageView imgView;
	Bitmap cover;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLanguage();
		setContentView(R.layout.activity_main);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		imgView = (ImageView) findViewById(R.id.imageView1);
		cover = decodeSampledBitmapFromResource(getResources(),
				R.drawable.hang_full, 150, 150);
		imgView.setImageBitmap(cover);
		addListenerOnButton();
	}

	protected void onResume() {
		super.onResume();
		setLanguage();
	}

	// Henter lagret språk fra sharedPrefs og laster dette
	private void setLanguage() {
		SharedPreferences languagepref = getSharedPreferences("language",
				MODE_PRIVATE);
		String language = languagepref.getString("languageToLoad", "en");
		Locale locale = new Locale(language);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	// initialiserer og setter lytter på knapper
	public void addListenerOnButton() {
		// final Context context = this;

		startButton = (Button) findViewById(R.id.startButton);
		statButton = (Button) findViewById(R.id.statButton);
		altButton = (Button) findViewById(R.id.altButton);
		rulesButton = (Button) findViewById(R.id.rulesButton);

		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, Hangman.class);
				startActivityForResult(intent, 0);
			}
		});

		statButton.setOnClickListener(new OnClickListener() {
			public void onClick(View args0) {
				Intent intent = new Intent(context, Stats.class);
				startActivity(intent);
			}
		});

		altButton.setOnClickListener(new OnClickListener() {
			public void onClick(View args0) {
				Intent intent = new Intent(context, AltMenu.class);
				startActivityForResult(intent, 0);
			}
		});

		rulesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View args0) {
				Intent intent = new Intent(context, Rules.class);
				startActivity(intent);
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1)
			finish();
		else
			recreate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// resirkulerer bilder for å spare plass på heapen
	public void onDestroy() {
		super.onDestroy();
		startButton.destroyDrawingCache();
		statButton.destroyDrawingCache();
		altButton.destroyDrawingCache();
		rulesButton.destroyDrawingCache();
		cover.recycle();
	}

	// Denne metoden er hentet fra nettet
	// (http://developer.android.com/training/displaying-bitmaps/load-bitmap.html)
	// Hjelper til med å dekode bitmap for å spare plass på heapen
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	// Denne metoden er hentet fra nettet
	// (http://developer.android.com/training/displaying-bitmaps/load-bitmap.html)
	// Hjelper til med å dekode bitmap for å spare plass på heapen
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

}
