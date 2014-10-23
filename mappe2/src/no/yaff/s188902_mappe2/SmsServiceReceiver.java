package no.yaff.s188902_mappe2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,PeriodicService.class);
		context.startService(i);
	}

}
