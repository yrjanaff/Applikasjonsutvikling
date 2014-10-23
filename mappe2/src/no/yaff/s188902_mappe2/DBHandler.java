package no.yaff.s188902_mappe2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper{
	
	static String TABLE_CONTACTS = "Contacts";
	static String KEY_ID = "_ID";
	static String KEY_FIRSTNAME = "Firstname";
	static String KEY_LASTNAME = "Lastname";
	static String KEY_BIRTHDAY = "Birthday";
	static String KEY_BIRTHMONTH = "Birthmonth";
	static String KEY_BIRTHYEAR = "Birthyear";
	static String KEY_TEL = "Telephone";
	static int DATABASE_VERSION = 6;
	
	static String DATABASE_NAME = "PhoneContacts";

	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String LAG_TABELL = "CREATE TABLE "
				+ TABLE_CONTACTS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_FIRSTNAME
				+ " TEXT," + KEY_LASTNAME + " TEXT," + 
				KEY_TEL + " INTEGER," + KEY_BIRTHDAY + " INTEGER," + 
				KEY_BIRTHMONTH + " INTEGER," + KEY_BIRTHYEAR + " INTEGER" +")";
		db.execSQL(LAG_TABELL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(db);
	}
	
	public void addContact(Contact contact){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FIRSTNAME, contact.getFirstName());
		values.put(KEY_LASTNAME, contact.getLastName());
		
		Calendar cal = contact.getBirthdate();
		values.put(KEY_BIRTHDAY, cal.get(Calendar.DAY_OF_MONTH));
		values.put(KEY_BIRTHMONTH, cal.get(Calendar.MONTH));
		values.put(KEY_BIRTHYEAR, cal.get(Calendar.YEAR));
		values.put(KEY_TEL, contact.getTel());
		db.insert(TABLE_CONTACTS, null, values);
		db.close();	
	}
	
	public List<Contact> findAllContacts(){
		List<Contact> ContactList = new ArrayList<Contact>();
		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " 
				+ KEY_FIRSTNAME + "," + KEY_LASTNAME + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()){
			do{
				Contact Contact = new Contact();
				Contact.setId(Integer.parseInt(cursor.getString(0)));
				Contact.setFirstName(cursor.getString(1));
				Contact.setLastName(cursor.getString(2));
				Contact.setTel(Integer.parseInt(cursor.getString(3)));
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(cursor.getString(4)));
				cal.set(Calendar.MONTH, Integer.parseInt(cursor.getString(5)));
				cal.set(Calendar.YEAR, Integer.parseInt(cursor.getString(6)));
				Contact.setBirthdate(cal);
				ContactList.add(Contact);
			}
			while(cursor.moveToNext());
		}
		db.close();
		return ContactList;
	}
	
	public Contact findContact(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_TEL, KEY_BIRTHDAY, KEY_BIRTHMONTH, KEY_BIRTHYEAR},
				KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(cursor.getString(4)));
		cal.set(Calendar.MONTH, Integer.parseInt(cursor.getString(5)));
		cal.set(Calendar.YEAR, Integer.parseInt(cursor.getString(6)));
		
		Contact Contact = new Contact(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), cal);
		return Contact;
	}
	
	public void deleteContact(Contact Contact){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS, KEY_ID + "=?", 
				new String[]{String.valueOf(Contact.getId())});
		db.close();
	}
	
	public int updateContact(Contact contact){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FIRSTNAME, contact.getFirstName());
		values.put(KEY_LASTNAME, contact.getLastName());
		values.put(KEY_TEL, contact.getTel());
		Calendar cal = contact.getBirthdate();
		values.put(KEY_BIRTHDAY, cal.get(Calendar.DAY_OF_MONTH));
		values.put(KEY_BIRTHMONTH, cal.get(Calendar.MONTH));
		values.put(KEY_BIRTHYEAR, cal.get(Calendar.YEAR));
		int ans = db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[]{
				String.valueOf(contact.getId())
		});
		db.close();
		return ans;
	}
	
	public int countContacts(){
		String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int cnt = cursor.getCount();
		cursor.close();
		db.close();
		return cnt;
	}

}

