package com.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profiles {
	
	private static transient Gson gsonFull;

	@SerializedName("username")
	@Expose 
	private String username;
	
	@SerializedName("age_range")
	@Expose 
	private String age_range;
	
	@SerializedName("phone")
	@Expose 
	private String phone;
	
	@SerializedName("password")
	@Expose 
	private String password;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getAge_range() {
		return age_range;
	}

	public void setAge_range(String age_range) {
		this.age_range = age_range;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	static public Profiles fromJson(String json) {
		if (null == gsonFull) {
			gsonFull = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").disableHtmlEscaping().create();
		}

		return gsonFull.fromJson(json, Profiles.class);
	}

}
