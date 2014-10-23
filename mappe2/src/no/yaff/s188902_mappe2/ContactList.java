package no.yaff.s188902_mappe2;

import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactList extends Activity{
	
	DBHandler db;
	ListView listView;
	List<Contact> allContacts;
	Context context = this;
	ArrayAdapter<Contact> adapter;
	
	protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setLanguage();
		 getActionBar().setTitle(getString(R.string.app_name));
	     setContentView(R.layout.activity_contact_list);
	     startService();
	     
		db = new DBHandler(context);
		
		allContacts = db.findAllContacts();
		
		listView = (ListView)findViewById(R.id.listview);
		if(allContacts.isEmpty()){
			Contact info = new Contact("Listen er tom", "");
			allContacts.add(info);
		}
		adapter = new ArrayAdapter<>(this, R.layout.listrow, allContacts);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){
		@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				Contact contact = adapter.getItem(arg2);
				Intent intent = new Intent(context, EditContact.class);
				intent.putExtra("Contact", contact.getId());
				startActivityForResult(intent, 0);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3){
				final Contact contact = adapter.getItem(arg2);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(getString(R.string.delete));
				builder.setMessage(getString(R.string.delete_warning) + " " + contact.getFirstName() + " " + contact.getLastName() + "?");
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								db.deleteContact(contact);
								updateData();
							}
						});
				builder.setNegativeButton(getString(R.string.no), null);
				builder.show();
				return true;
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1)
			finish();
		else if(resultCode == 10){
			setLanguage();
			recreate();
		}
		else{
			updateData();
		}
	}
	
	private void setLanguage(){
		SharedPreferences languagepref = getSharedPreferences(Settings.LANGKEY, Context.MODE_MULTI_PROCESS);
		String language = languagepref.getString(Settings.LOADLANG, Settings.ENG);
		Locale locale = new Locale(language);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}
	
	public void updateData() {
	    adapter.clear(); 
	    List<Contact> allContacts = db.findAllContacts();
	    if (!allContacts.isEmpty()){
	        for (Contact contact : allContacts) {
	            adapter.insert(contact, adapter.getCount());
	        }
	    }else{
	    	Contact info = new Contact("Listen er tom", "");
			allContacts.add(info);
			adapter.insert(info, adapter.getCount());
	    }
	    adapter.notifyDataSetChanged();
	}
	
	  public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	  }
	  
	  public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	        if (id == R.id.action_settings) {
	            Intent intent = new Intent(context, Settings.class);
	            startActivityForResult(intent, 0);
	            return true;
	        }
	        
	        if(id == R.id.action_add){
	        	Intent intent = new Intent(context, NewContact.class);
				startActivityForResult(intent, 0);
				return true;
	        }
	        if(id == R.id.action_close){
	        	finish();
	        }
	        return super.onOptionsItemSelected(item);
	 }
	  
	  public void startService(){
		  Intent intent = new Intent(this, PeriodicService.class);
		  this.startService(intent);
	  }
}
