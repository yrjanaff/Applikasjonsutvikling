package no.yaff.s188902_mappe2;

import java.util.Calendar;

public class Contact {
	int id, tel;
	String firstName, lastName;
	Calendar birthdate;
	
	public Contact(int id, String firstName, String lastName, int tel, Calendar birthdate) {
		super();
		this.id = id;
		this.tel = tel;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}

	public Contact(String firstName, String lastName, int tel, Calendar birthdate) {
		super();
		this.tel = tel;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}
	
	public Contact(String firstName, String lastName){
		//For when the list i empty
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Contact() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTel() {
		return tel;
	}

	public void setTel(int tel) {
		this.tel = tel;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getName() {
		return firstName + " " + lastName;
	}

	public void setName(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}
	public String toString(){
		return firstName + " " + lastName;
	}
}

