package com.ssafy.test.model.dto;


public class User {
	private String id;
	private String pw;
	private String name;
	private String birthdate;
	private String phone;
	private String email;
	private String regDate;
	
	
	
	public User(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public User(String id, String pw, String name, String birthdate, String phone, String email, String regDate) {
		super();
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.birthdate = birthdate;
		this.phone = phone;
		this.email = email;
		this.regDate = regDate;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", pw=" + pw + ", name=" + name + ", birthdate=" + birthdate + ", phone=" + phone
				+ ", email=" + email + ", regDate=" + regDate + "]";
	}

	
}
