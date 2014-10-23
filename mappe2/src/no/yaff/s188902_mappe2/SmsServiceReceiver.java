package no.yaff.s188902_mappe2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "I BroadcastReceiver", Toast.LENGTH_SHORT).show();
		System.out.println("Hei! Jeg er inne i smsServiceReveiver sin onReceive!");
		Intent i = new Intent(context,PeriodicService.class);
		context.startService(i);
		
		//TODO
		//Lese seg opp på AlarmManager, BR og pendingIntent
		//BroadcastReceiver mottar at maskin slås på
		//•  BroadcastReceiver starter service som henter klokkeslett fra 
		//Preferences og setter en PendingIntent som AlarmManager skal uføre 
		//på det gitte klokkeslettet.
		//•  AlarmManager starter en service som sender SMS-er.

	}

}
