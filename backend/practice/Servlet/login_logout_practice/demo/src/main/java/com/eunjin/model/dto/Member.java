package main.java.com.eunjin.model.dto;

import java.io.Serializable;
import java.util.*;

public class Member implements Serializable {
    private int mno;
    private String name;
    private String email;
    private String password;
    private String role;

    public Member() {
    }
   
    public Member(String name, String email, String password) {
        this(0, name, email, password,"USER");
    }

    public Member(int mno, String name, String email, String password, String role) {
        this.mno = mno;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

	public int getMno() {
        return mno;
    }

    public void setMno(int mno) {
        this.mno = mno;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Member [mno=" + mno + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role + "]";
    }
    
}
