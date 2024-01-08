package com.accounting.rest.multitenant.tenant.entity;

//ContactEntity.java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ContactEntity {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String name;
 private String email;
 private String contact;
public ContactEntity() {
	super();
	// TODO Auto-generated constructor stub
}
public ContactEntity(Long id, String name, String email, String contact) {
	super();
	this.id = id;
	this.name = name;
	this.email = email;
	this.contact = contact;
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getContact() {
	return contact;
}
public void setContact(String contact) {
	this.contact = contact;
}

 // Constructors, getters, setters...
 
}
