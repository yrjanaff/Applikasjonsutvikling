package no.yaff.s188902_mappe2;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class NewContact extends Activity {
	
	EditText firstnameText, lastnameText, phoneText;
	ImageButton addButton;
	DatePicker birthPicker;
	
	String firstname, lastname;
	Calendar birthdate = Calendar.getInstance();
	int phone;
	Context context = this;

	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getActionBar().setTitle(getString(R.string.add));
	     setContentView(R.layout.activity_new_contact);
	     
	     firstnameText = (EditText)findViewById(R.id.firstnameText);
	     lastnameText = (EditText)findViewById(R.id.lastnameText);
	     phoneText = (EditText)findViewById(R.id.phoneText);
	     birthPicker = (DatePicker)findViewById(R.id.datePicker);
	     addButton = (ImageButton)findViewById(R.id.addButton);
	     
	     addButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		firstname = firstnameText.getText().toString();
        		lastname = lastnameText.getText().toString();
        		birthdate.set(Calendar.DAY_OF_MONTH, birthPicker.getDayOfMonth());
        		birthdate.set(Calendar.MONTH, birthPicker.getMonth());
        		birthdate.set(Calendar.YEAR, birthPicker.getYear());
        		
        		//TODO:
        		// - sjekke dato
        		if((firstname.length() > 0 || lastname.length() > 0) && phoneText.getText().toString().length() == 8){
        			phone = Integer.parseInt(phoneText.getText().toString());
        			Contact contact = new Contact(firstname, lastname, phone, birthdate);
        			DBHandler db = new DBHandler(context);
        			db.addContact(contact);
        			setResult(0);
        			finish();
        		}
        		else{
        			if(firstname.length() == 0 && lastname.length() == 0)
        				Toast.makeText(context, R.string.name_error, Toast.LENGTH_SHORT).show(); 

        			else
        				Toast.makeText(context, R.string.phone_error, Toast.LENGTH_SHORT).show();
        		}
        	}
        });
	     
	 }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.other, menu);
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
