package no.yaff.s188902_mappe2;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditContact extends Activity {

	EditText firstnameText, lastnameText, phoneText;
	ImageButton editButton, deleteButton;
	DatePicker birthPicker;
	Contact contact;
	DBHandler db;
	
	String firstname, lastname;
	Calendar birthdate = Calendar.getInstance();
	int phone, id;
	Context context = this;

	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getActionBar().setTitle(getString(R.string.edit));
	     setContentView(R.layout.activity_edit_contact);
	     
	     Intent thisIntent = getIntent();
	     id = thisIntent.getIntExtra("Contact", -1);
	     db = new DBHandler(context);
	     
	     if(id != -1){
	    	 contact = db.findContact(id);
	     }
	     
	     else
	     {
	    	 Toast.makeText(context, getString(R.string.not_found), Toast.LENGTH_SHORT).show();
	    	 finish();
	     }
	     
	     setup();
	     
	     editButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		firstname = firstnameText.getText().toString();
        		lastname = lastnameText.getText().toString();
        		birthdate.set(Calendar.DAY_OF_MONTH, birthPicker.getDayOfMonth());
        		birthdate.set(Calendar.MONTH, birthPicker.getMonth());
        		birthdate.set(Calendar.YEAR, birthPicker.getYear());
        		
        		//TODO:
        		// - test på dato
        		if((firstname.length() > 0 || lastname.length() > 0) && phoneText.getText().toString().length() == 8){
        			phone = Integer.parseInt(phoneText.getText().toString());
        			contact.setFirstName(firstname);
        			contact.setLastName(lastname);
        			contact.setTel(phone);
        			contact.setBirthdate(birthdate);
        			
        			db.updateContact(contact);
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
	     deleteButton.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(getString(R.string.delete));
				builder.setMessage(getString(R.string.delete_warning) + " " + contact.getFirstName() + " " + contact.getLastName() + "?");
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								db.deleteContact(contact);
								setResult(0);
								finish();
							}
						});
				builder.setNegativeButton(getString(R.string.no), null);
				builder.show();
	    	}
	     });
	     
	 }
	 
	private void setup(){
		firstnameText = (EditText)findViewById(R.id.firstnameEditText);
	     lastnameText = (EditText)findViewById(R.id.lastnameEditText);
	     phoneText = (EditText)findViewById(R.id.phoneEditText);
	     birthPicker = (DatePicker)findViewById(R.id.dateEditPicker);
	     editButton = (ImageButton)findViewById(R.id.editButton);
	     deleteButton = (ImageButton)findViewById(R.id.deleteButton);
	     
	     firstnameText.setText(contact.getFirstName());
	     lastnameText.setText(contact.getLastName());
	     phoneText.setText(contact.getTel() + "");
	     birthPicker.updateDate(contact.getBirthdate().get(Calendar.YEAR), contact.getBirthdate().get(Calendar.MONTH), 
	    		 contact.getBirthdate().get(Calendar.DAY_OF_MONTH));
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
