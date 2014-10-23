package no.yaff.hangmanoblig1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class Hangman extends Activity {

	final Button[] buttons = new Button[29];
	String pickedWord;
	private final String gS = "gameStarted", lL = "letterList",
			uL = "usedList", pW = "pickedWord", tr = "tries", rc = "rightCnt";
	String[] words;
	boolean curLangEng = false, lost, gameStarted;
	int pickedNumber, wordLength, tries, rightCnt;
	int[] usedList;
	int[] hangImages = new int[7];
	List<String> letterList = new ArrayList<>();
	Bitmap[] hangImg = new Bitmap[7];
	Random rand;
	LinearLayout linearlayout;
	ImageView hangView;
	TextView[] textBox;
	SharedPreferences sharedPrefs, langPrefs;
	Editor edit;
	final Context context = this;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hangman);
		gameStarted = false;

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		langPrefs = getSharedPreferences("language", MODE_PRIVATE);
		if (langPrefs.getString("languageToLoad", "en").equalsIgnoreCase("en"))
			curLangEng = true;

		edit = sharedPrefs.edit();

		buttonManager();
		rand = new Random();

		Resources res = getResources();
		words = res.getStringArray(R.array.words);
		usedList = new int[words.length];

		for (int i = 0; i < usedList.length; i++)
			usedList[i] = -1;

		putHangImages();
		hangView = (ImageView) findViewById(R.id.hang_View);

		if (sharedPrefs.getBoolean(gS, false))
			startFromSave();
		else
			start();
		gameStarted = true;
		lost = false;

	}

	protected void onPause() {
		super.onPause();
		if (gameStarted)
			saveGame();
	}

	// henter ut lagrede verdier fra sharedPrefs og setter opp spillet
	private void startFromSave() {
		lost = false;
		tries = sharedPrefs.getInt(tr, 6);
		pickedWord = sharedPrefs.getString(pW, words[0]);
		wordLength = pickedWord.length();
		rightCnt = sharedPrefs.getInt(rc, 0);
		String letterListString = sharedPrefs.getString(lL, "");
		StringTokenizer st = new StringTokenizer(letterListString, ",");
		for (int i = 0; i < letterListString.length() / 2; i++) {
			letterList.add(st.nextToken().toString());
		}

		String usedListString = sharedPrefs.getString(uL, "");
		StringTokenizer stoken = new StringTokenizer(usedListString, ",");
		for (int i = 0; i < letterListString.length(); i++) {
			usedList[i] = Integer.parseInt(stoken.nextToken());
		}

		textBox = new TextView[wordLength];
		hangView.destroyDrawingCache();
		hangView.setImageBitmap(hangImg[tries]);
		linearlayout = (LinearLayout) findViewById(R.id.TextBoxLayout);
		// sletter gamle textboxer:
		linearlayout.removeAllViewsInLayout();

		String rightLetters = "";
		String buttonText = "", letter = "";
		keyboardSwitch(true);

		for (int i = 0; i < letterList.size(); i++) {
			letter = letterList.get(i).toString();
			if (pickedWord.contains(letter)) {
				if (!rightLetters.contains(letter)) {
					rightLetters += letter;
				}
			}
			for (int j = 0; j < buttons.length; j++) {
				buttonText = buttons[j].getText().toString();
				if (buttonText.equalsIgnoreCase(letter)) {
					buttons[j].setEnabled(false);
					break;

				}
			}
		}

		// Legger til nye textboxer
		for (int i = 0; i < wordLength; i++) {
			textBox[i] = new TextView(this);
			textBox[i].setTextSize(30);
			textBox[i].setText("_");
			for (int j = 0; j < rightLetters.length(); j++) {
				if (pickedWord.charAt(i) == rightLetters.charAt(j))
					textBox[i].setText("" + rightLetters.charAt(j));
			}
			textBox[i].setPadding(0, 0, 5, 0);
			textBox[i].setTextColor(Color.WHITE);
			linearlayout.addView(textBox[i]);
		}
		if (curLangEng)
			disableNorKeys();
	}

	// Lagrer variabler i sharedPrefs ved pause
	private void saveGame() {
		lost = false;
		edit.putBoolean(gS, true);

		StringBuilder letterListBuilder = new StringBuilder();
		for (int i = 0; i < letterList.size(); i++) {
			letterListBuilder.append(letterList.get(i)).append(",");
		}

		edit.putString(lL, letterListBuilder.toString());

		StringBuilder usedListBuilder = new StringBuilder();
		for (int i = 0; i < usedList.length; i++) {
			usedListBuilder.append(usedList[i]).append(",");
		}
		edit.putString(uL, usedListBuilder.toString());
		edit.putString(pW, pickedWord);
		edit.putInt(tr, tries);
		edit.putInt(rc, rightCnt);
		edit.commit();
	}

	// Vanlig start av spillet
	private void start() {
		deleteSavedStuff();
		tries = 6;
		rightCnt = 0;
		hangView.setImageBitmap(hangImg[tries]);

		if (usedList.length != 0) {
			pickedNumber = pickNumber();
		}

		pickedWord = words[pickedNumber];

		wordLength = pickedWord.length();
		textBox = new TextView[wordLength];

		linearlayout = (LinearLayout) findViewById(R.id.TextBoxLayout);
		// sletter gamle textboxer:
		linearlayout.removeAllViewsInLayout();

		// Legger til nye textboxer
		for (int i = 0; i < wordLength; i++) {
			textBox[i] = new TextView(this);
			textBox[i].setTextSize(30);
			textBox[i].setText("_");
			textBox[i].setPadding(0, 0, 5, 0);
			textBox[i].setTextColor(Color.WHITE);
			linearlayout.addView(textBox[i]);
		}
		keyboardSwitch(true);

		if (curLangEng)
			disableNorKeys();
	}

	// Velger et random nummer som ikke er blitt brukt før, til å velge ord
	private int pickNumber() {
		int number = rand.nextInt(words.length);
		boolean rightNumber = false, found = false;

		while (!rightNumber) {
			for (int i = 0; i < usedList.length; i++) {
				if (number == usedList[i]) {
					found = true;
					break;
				}
			}
			if (found) {
				if (usedList[usedList.length - 1] != -1) {
					deleteSavedStuff();
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(getString(R.string.warning));
					builder.setMessage(getString(R.string.noWords));
					builder.setPositiveButton(getString(R.string.ok_string),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									setResult(0);
									finish();
								}
							});
					builder.show();
				} else {
					number = rand.nextInt(words.length);
					found = false;
				}
			} else {
				rightNumber = true; // Stopper whileløkken
				for (int i = 0; i < usedList.length; i++) {
					if (usedList[i] == -1) {
						usedList[i] = number;
						break;
					}
				}
				break;
			}
		}
		return number;
	}

	// Sjekker om den gitte bokstaven er i ordet. Ved feil bokstav settes nytt
	// hangman-bilde, ved riktig bokstav settes bokstaven inn i ordet.
	// Når spilleren vinner eller taper blir dette informert om ved dialogbokser
	public void setText(String letter) {
		letterList.add(letter);
		boolean found = false;
		char charLetter = letter.charAt(0);
		for (int i = 0; i < wordLength; i++) {
			if (pickedWord.charAt(i) == charLetter) {
				textBox[i].setText(letter);
				found = true;
				rightCnt++;
			}
		}
		if (rightCnt >= wordLength) {
			keyboardSwitch(false);
			int wins = 0;
			if (sharedPrefs.contains("winCnt")) {
				wins = sharedPrefs.getInt("winCnt", -1);
			}
			edit.putInt("winCnt", ++wins);
			edit.commit();

			StringBuilder st = new StringBuilder();
			for (int i = 0; i < usedList.length; i++)
				st.append(usedList[i]).append(",");

			if (usedList[usedList.length - 1] == -1) {
				String word = pickedWord;
				deleteSavedStuff();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.congrats));
				builder.setMessage(getString(R.string.u_won) + " " + word
						+ ".\n" + getString(R.string.play_again));
				builder.setPositiveButton(getString(R.string.ok_string),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								start();
							}
						});
				builder.setNegativeButton(getString(R.string.action_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								setResult(0);
								finish();
							}
						});
				builder.setNeutralButton(getString(R.string.stat_string),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								lost = true;
								deleteSavedStuff();
								Intent intent = new Intent(context, Stats.class);
								startActivity(intent);
							}
						});
				builder.show();
			} else {
				String word = pickedWord;
				deleteSavedStuff();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.warning));
				builder.setMessage(getString(R.string.u_won) + " " + word
						+ ". " + getString(R.string.noWords));
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								setResult(0);
								recreate();
							}
						});
				builder.setNeutralButton(getString(R.string.stat_string),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								lost = true;
								deleteSavedStuff();
								Intent intent = new Intent(context, Stats.class);
								startActivity(intent);
							}
						});
				builder.setNegativeButton(getString(R.string.action_close),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								setResult(0);
								recreate();
								finish();
							}
						});
				builder.show();
			}
		}

		if (!found) {
			if (--tries > 0)
				hangView.setImageBitmap(hangImg[tries]);
			if (tries == 0) {
				hangView.setImageBitmap(hangImg[tries]);
				keyboardSwitch(false);
				int losses = 0;
				if (sharedPrefs.contains("lossCnt")) {
					losses = sharedPrefs.getInt("lossCnt", -1);
				}
				edit.putInt("lossCnt", ++losses);
				edit.commit();

				if (usedList[usedList.length - 1] == -1) {
					lost = true;
					String word = pickedWord;
					deleteSavedStuff();
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(getString(R.string.warning));
					builder.setMessage(getString(R.string.u_lost) + " " + word
							+ ".\n" + getString(R.string.play_again));
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									start();
								}
							});
					builder.setNegativeButton(
							getString(R.string.action_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									setResult(0);
									finish();
								}
							});
					builder.setNeutralButton(getString(R.string.stat_string),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									lost = true;
									deleteSavedStuff();
									Intent intent = new Intent(context,
											Stats.class);
									startActivity(intent);
								}
							});
					builder.show();
				} else {
					lost = true;
					String word = pickedWord;
					deleteSavedStuff();
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(getString(R.string.warning));
					builder.setMessage(getString(R.string.u_lost) + " " + word
							+ ". " + getString(R.string.noWords));
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									setResult(0);
									recreate();
								}
							});
					builder.setNeutralButton(getString(R.string.stat_string),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									lost = true;
									deleteSavedStuff();
									Intent intent = new Intent(context,
											Stats.class);
									startActivity(intent);
								}
							});
					builder.setNegativeButton(getString(R.string.action_close),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									setResult(0);
									recreate();
									finish();
								}
							});
					builder.show();
				}
			}
		}
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
		switch (item.getItemId()) {
		case R.id.action_close:
			setResult(1);
			deleteSavedStuff();
			recreate();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		deleteSavedStuff();
		recreate();
	}

	// Legger tastaturknappene inn i knappliste og setter på lytter
	private void buttonManager() {
		buttons[0] = (Button) findViewById(R.id.buttonQ);
		buttons[1] = (Button) findViewById(R.id.buttonW);
		buttons[2] = (Button) findViewById(R.id.buttonE);
		buttons[3] = (Button) findViewById(R.id.buttonR);
		buttons[4] = (Button) findViewById(R.id.buttonT);
		buttons[5] = (Button) findViewById(R.id.buttonY);
		buttons[6] = (Button) findViewById(R.id.buttonU);
		buttons[7] = (Button) findViewById(R.id.buttonI);
		buttons[8] = (Button) findViewById(R.id.buttonO);
		buttons[9] = (Button) findViewById(R.id.buttonP);
		buttons[10] = (Button) findViewById(R.id.buttonAa);
		buttons[11] = (Button) findViewById(R.id.buttonA);
		buttons[12] = (Button) findViewById(R.id.buttonS);
		buttons[13] = (Button) findViewById(R.id.buttonD);
		buttons[14] = (Button) findViewById(R.id.buttonF);
		buttons[15] = (Button) findViewById(R.id.buttonG);
		buttons[16] = (Button) findViewById(R.id.buttonH);
		buttons[17] = (Button) findViewById(R.id.buttonJ);
		buttons[18] = (Button) findViewById(R.id.buttonK);
		buttons[19] = (Button) findViewById(R.id.buttonL);
		buttons[20] = (Button) findViewById(R.id.buttonOe);
		buttons[21] = (Button) findViewById(R.id.buttonAe);
		buttons[22] = (Button) findViewById(R.id.buttonZ);
		buttons[23] = (Button) findViewById(R.id.buttonX);
		buttons[24] = (Button) findViewById(R.id.buttonC);
		buttons[25] = (Button) findViewById(R.id.buttonV);
		buttons[26] = (Button) findViewById(R.id.buttonB);
		buttons[27] = (Button) findViewById(R.id.buttonN);
		buttons[28] = (Button) findViewById(R.id.buttonM);

		if (curLangEng) {
			buttons[10].setText("");
			buttons[20].setText("");
			buttons[21].setText("");
		}

		MyOnClickListener clickListener = new MyOnClickListener();
		for (Button btn : buttons) {
			if (btn != null)
				btn.setOnClickListener(clickListener);
		}
	}

	// disabler norske knapper ved engelsk språk
	private void disableNorKeys() {
		buttons[10].setEnabled(false);
		buttons[20].setEnabled(false);
		buttons[21].setEnabled(false);
	}

	// Metode for å disable eller enable alle knapper samtidig
	private void keyboardSwitch(boolean onOff) {
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] != null)
				buttons[i].setEnabled(onOff);
		}
	}

	// Lytterklasse som sender knappens bokstav til setText-metoden
	private class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			try {
				Button btn = (Button) v;
				setText(btn.getText().toString());
				btn.setEnabled(false);
			} catch (Exception e) {
				System.out.println("Noe gikk galt. v er ikke Button: "
						+ e.toString());
			}
		}

	}

	// sletter og resetter diverse variabler som ikke lenger skal lagres,
	// f.eks ved tap eller vinn.
	private void deleteSavedStuff() {
		if (lost) {
			pickedNumber = pickNumber();
			pickedWord = words[pickedNumber];
			edit.putString(pW, pickedWord);
			lost = false;
		}
		edit.putString(lL, "");
		edit.putString(uL, "");
		edit.putString(pW, "");
		edit.putBoolean(gS, false);
		edit.remove(lL);
		edit.remove(uL);
		// edit.remove(pW);
		edit.remove(tr);
		edit.remove(rc);
		edit.commit();
		letterList.clear();
		tries = 6;
		rightCnt = 0;
	}

	// Henter hangmanbildene og gjør de om til bitmap for å spare plass på
	// heapen
	private void putHangImages() {
		hangImages[6] = R.drawable.hang_base;
		hangImages[5] = R.drawable.hang_head;
		hangImages[4] = R.drawable.hang_body;
		hangImages[3] = R.drawable.hang_one_arm;
		hangImages[2] = R.drawable.hang_two_arms;
		hangImages[1] = R.drawable.hang_one_leg;
		hangImages[0] = R.drawable.hang_full;
		for (int i = 0; i < hangImages.length; i++) {
			hangImg[i] = decodeSampledBitmapFromResource(getResources(),
					hangImages[i], 150, 150);
		}
	}

	// Sletter bilder og setter hangmanbildene til å resirkuleres for
	// aa spare plass paa heapen
	public void onDestroy() {
		super.onDestroy();

		hangView.setImageDrawable(null);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setBackground(null);
			if (i < 7) {
				hangImg[i].recycle();
			}
		}
		System.gc();
		super.onDestroy();
	}

	// Denne metoden er hentet fra nettet
	// (http://developer.android.com/training/displaying-bitmaps/load-bitmap.html)
	// Den hjelper til med resize av bitmap for å spare plass på heapen
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
