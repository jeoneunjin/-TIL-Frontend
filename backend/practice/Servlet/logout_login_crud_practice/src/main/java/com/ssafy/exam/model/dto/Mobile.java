package com.ssafy.exam.model.dto;

import java.io.Serializable;

public class Mobile implements Serializable {
	private String code;
	private String name;
	private Integer price;
	private String vendor;
	private String color;
	
	public Mobile() {
		// TODO Auto-generated constructor stub
	}

	public Mobile(String code, String name, Integer price, String vendor, String color) {
		super();
		this.code = code;
		this.name = name;
		this.price = price;
		this.vendor = vendor;
		this.color = color;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Mobile [code=" + code + ", name=" + name + ", price=" + price + ", vendor=" + vendor + ", color="
				+ color + "]";
	}
	
	
	
	
	
	

}
