package no.yaff.s188902_mappe2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsService extends Service {
	
	DBHandler db;
	Calendar today;
	SharedPreferences sp;
	PendingIntent pi;
	List<Contact> contactList;
	ArrayList<Integer> sentContacts;
	SmsManager sms;

	public int onStartCommand(Intent intent, int flags, int startId){
		Toast.makeText(this, "Dette er SMSService!", Toast.LENGTH_SHORT).show();
		
		System.out.println("Hei! Jeg er inne i SmsService sin onStartCommand!");
		
		today = Calendar.getInstance();
		sms = SmsManager.getDefault();
		db = new DBHandler(this);
		sp = getSharedPreferences(Settings.SPNAME, Context.MODE_MULTI_PROCESS);
		pi = PendingIntent.getActivity(this, 0, new Intent(this, SmsService.class), 0);
		contactList = db.findAllContacts();
		sentContacts = new ArrayList<Integer>();
		
		traverseContactsAndSendSms();
		if(sentContacts.size() > 0){
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Intent i = new Intent(this, Result.class);
			i.putIntegerArrayListExtra("sentContacts", sentContacts);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
			
			Notification noti = new Notification.Builder(this).setContentTitle(getString(R.string.noti_title))
					.setContentText(getString(R.string.noti_text)).setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(pIntent).build();
			noti.flags |= Notification.FLAG_AUTO_CANCEL; notificationManager.notify(0, noti);
		}
		return Service.START_NOT_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void traverseContactsAndSendSms(){
		String msg = sp.getString(Settings.SMSTXT, "");
		boolean name = msg.contains("(name)");
		for(int i = 0; i < contactList.size(); i++){
			Contact con = contactList.get(i);
			Calendar cal = con.getBirthdate();
			
			if((cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) && 
					(cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))){
				if(name){ 
					//bytter ut (name) med kontaktens fornavn
					msg = msg.replace("(name)", con.getFirstName());
				}
				sms.sendTextMessage(con.getTel()+"", null, msg, pi, null);
				
				sentContacts.add(con.getId());
				Log.d("SmsService", "SMS ble sendt til: " + con.getFirstName() + " " + con.getLastName() + " Meldingen var: " + msg);
			}
		}
	}
	
}
