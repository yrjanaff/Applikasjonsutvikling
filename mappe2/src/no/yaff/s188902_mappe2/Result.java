package no.yaff.s188902_mappe2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Result extends Activity {

	ArrayList<Integer> sentContacts;
	TextView list;
	DBHandler db;
	
	protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 getActionBar().setTitle(getString(R.string.app_name));
	     setContentView(R.layout.result);
	     
	     Intent intent = getIntent();
	     sentContacts = intent.getIntegerArrayListExtra("sentContacts");
	     db = new DBHandler(this);
	     
	     list = (TextView)findViewById(R.id.smsContactList);
	     try{
	    	 StringBuilder contacts = new StringBuilder();
	    	 for(int i = 0; i < sentContacts.size(); i++){
	    		 contacts.append("- ");
	    		 contacts.append(db.findContact(sentContacts.get(i)).toString());
	    		 contacts.append("\n");
	    	 }
	    	 list.setText(contacts.toString());
	     }
	     catch(Exception ex){
	    	 list.setText(getText(R.string.sms_info));
	     } 
	}
}
