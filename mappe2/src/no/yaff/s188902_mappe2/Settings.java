package no.yaff.s188902_mappe2;

import java.util.Calendar;
import java.util.Locale;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

public class Settings extends Activity {
	
	ImageButton flagButton;
	Switch smsSwitch;
	EditText smsText;
	TimePicker smsTime;
	Button save;
	Context context = this;
	Calendar timeForSms = Calendar.getInstance();
	SharedPreferences sp_sms, sp_lang;
	Editor edit_sms, edit_lang;
	boolean langChange = false;
	
	final public static String SMSTXT = "smsTxt";
	final public static String SMSHOUR = "smsHour";
	final public static String SMSMINUTE = "smsMinute";
	final public static String SMSSERVICE = "smsService";
	final public static String SPNAME = "smsPrefs";
	final public static String NOR = "nb";
	final public static String ENG = "en";
	final public static String LANGKEY = "lang_key";
	final public static String LOADLANG = "languageToLoad";
	
	protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 getActionBar().setTitle(getString(R.string.action_settings));
	     setContentView(R.layout.activity_settings);
	     
	     //TODO bytte spåk
	     sp_sms = getSharedPreferences(SPNAME, Context.MODE_MULTI_PROCESS);
	     sp_lang = getSharedPreferences(LANGKEY, Context.MODE_MULTI_PROCESS);
	     edit_sms = sp_sms.edit();
	     edit_lang = sp_lang.edit();
	     
	     smsSwitch = (Switch)findViewById(R.id.switch1);
	     smsText = (EditText)findViewById(R.id.smsText);
	     smsTime = (TimePicker)findViewById(R.id.smsTimePicker);
	     save = (Button)findViewById(R.id.saveButton);
	     flagButton = (ImageButton)findViewById(R.id.flagButton);
	    
	     smsTime.setIs24HourView(true);
	     
	     loadPrefs();
	     
	     smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    	        if (isChecked) {
	    	        	checkChanged(true);
	    	        } else {
	    	        	checkChanged(false);
	    	        }
	    	    }
	     });
	     
	     save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(smsText.getText().toString().length() > 0){
					final int smsHour = smsTime.getCurrentHour();
					final int smsMinute = smsTime.getCurrentMinute();
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle(getString(R.string.birth_sms));
					builder.setMessage(getString(R.string.sms_warning) + " \"" + 
							smsText.getText().toString() + "\" "+ 
							getString(R.string.sms_warning2) + " " + smsHour + ":" + smsMinute);
					builder.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									savePrefs(smsText.getText().toString(), smsHour, smsMinute, true);
									Intent i = new Intent(context,PeriodicService.class);
									//context.stopService(i);
									context.startService(i);
								}
							});
					builder.setNegativeButton(getString(R.string.cancel), null);
					builder.show();
				}
				else{
					Toast.makeText(context, R.string.sms_text_warning, Toast.LENGTH_SHORT).show();
				}
			}
	     });

			flagButton.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					String curLang = sp_lang.getString(LOADLANG, ENG);
					if (curLang.equalsIgnoreCase(ENG))
						setLanguage(NOR);
					else
						setLanguage(ENG);
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

			edit_lang.putString(LOADLANG, lang);
			edit_lang.commit();

			recreate();
			smsSwitch.setText(getString(R.string.birth_sms));
			setResult(10);
			//finish();
			Toast.makeText(context, R.string.new_lang, Toast.LENGTH_SHORT).show();
			langChange = true;
		}
	
	private void checkChanged(boolean checked){
		if(checked){
			Toast.makeText(context, "Switch enabled", Toast.LENGTH_SHORT).show();
			enable(checked);
		}
		else{
			//scrollView.setVisibility(ScrollView.GONE);
			enable(checked);
			edit_sms.putBoolean(SMSSERVICE, false);
			Toast.makeText(context, "Switch disabled", Toast.LENGTH_SHORT).show();
			 AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		      PendingIntent pSmsIntent = PendingIntent.getService(this, 0, new Intent(this, SmsService.class), 0);
		     alarmManager.cancel(pSmsIntent);
			//context.stopService(i);
		}
		edit_sms.apply();
	}
	
	private void loadPrefs(){
		if(sp_sms.getBoolean(SMSSERVICE, false)){
			Log.d("loadPrefs", "true! SMSSERVICE var true!");
	    	 //scrollView.setVisibility(ScrollView.VISIBLE);
			enable(true);
	    	 smsSwitch.setChecked(true);
	    	 String msgTxt = sp_sms.getString(SMSTXT, null);
	    	 int hour = sp_sms.getInt(SMSHOUR, -1);
	    	 int min = sp_sms.getInt(SMSMINUTE, -1);
	    		
	    	 if(msgTxt != null)
	    		 smsText.setText(msgTxt);
	    	 if(hour != -1 && min != -1){
	    		 smsTime.setCurrentHour(hour);
	    		 smsTime.setCurrentMinute(min);
	    	 }
	     }
	     else{
	    	 Log.d("loadPrefs", "false! SMSSERVICE var false!");
	    	 //scrollView.setVisibility(ScrollView.GONE);
	    	 enable(false);
	    	 smsSwitch.setChecked(false);
	     }
	}
	
	private void enable(boolean enable){
		smsTime.setEnabled(enable);
   	 	smsText.setEnabled(enable);
   	 	save.setEnabled(enable);
	}
	
	private void savePrefs(String txt, int hour, int minute, boolean smsPrefsOn){
		System.out.println("txt: " + txt + " hour: " + hour + " min: " + minute);
		edit_sms.putString(SMSTXT, txt);
		edit_sms.putInt(SMSHOUR, hour);
		edit_sms.putInt(SMSMINUTE, minute);
		edit_sms.putBoolean(SMSSERVICE, smsPrefsOn);
		edit_sms.commit(); //VIKTIG!!!
		
		System.out.println("savePrefs! hour: " + sp_sms.getInt(SMSHOUR, -1) + " min: " + sp_sms.getInt(SMSMINUTE, -1));
	}
	
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
	 
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		
        getMenuInflater().inflate(R.menu.other, menu);
        return true;
  }
}
