package no.yaff.s188902_mappe2;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class PeriodicService extends Service {
	SharedPreferences sp;
	PendingIntent pintent; 
	AlarmManager alarm;
	final Context context = this;
	int hour;
	int minute;
	public int onStartCommand(Intent intent, int flags, int startId){
		 sp = getSharedPreferences(Settings.SPNAME, Context.MODE_MULTI_PROCESS);
		 if(sp.getBoolean(Settings.SMSSERVICE, false)){
			hour = sp.getInt(Settings.SMSHOUR, -1);
			minute = sp.getInt(Settings.SMSMINUTE, -1);
			
			if(hour != -1 && minute != -1){
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, minute);
				cal.set(Calendar.SECOND, 0);
				
				Intent i = new Intent(this, SmsService.class);
		        PendingIntent pintent = PendingIntent.getService(this, 0, i, 0);
		        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		        
		        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),  AlarmManager.INTERVAL_DAY, pintent);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onDestroy(){
		//super.onDestroy();
		
		Intent i = new Intent(context, SmsService.class);
		context.stopService(i);
		pintent = PendingIntent.getService(context, 0, i, 0);
		alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
	}
}
