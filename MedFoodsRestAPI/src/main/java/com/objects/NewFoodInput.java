package com.objects;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewFoodInput {
	
	private static transient Gson gsonFull;
	
	@SerializedName("name")
	@Expose 
	private String name;
	
	@SerializedName("description")
	@Expose 
	private String description;
	
	@SerializedName("disclaimer")
	@Expose 
	private String disclaimer;
	
	@SerializedName("consumptionMode")
	@Expose 
	private String consumptionMode;
	
	@SerializedName("result")
	@Expose 
	private String result;
	
	@SerializedName("condition")
	@Expose 
	private String condition;
	
	@SerializedName("symptom")
	@Expose 
	private String symptom;
	
	@SerializedName("dosage")
	@Expose 
	private JSONObject dosage;
	
	@SerializedName("references")
	@Expose 
	private JSONObject references;

	public String getName() {
		return name;
	}
	public void setProfileId(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public String getConsumptionMode() {
		return consumptionMode;
	}
	public void setConsumptionMode(String consumptionMode) {
		this.consumptionMode = consumptionMode;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getSymptom() {
		return symptom;
	}
	public void setSymptom(String symptom) {
		this.symptom = symptom;
	}
	
}