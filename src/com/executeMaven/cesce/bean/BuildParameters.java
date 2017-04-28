package com.executeMaven.cesce.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildParameters {
	Map<String, Object> atributes;
	private List<String> mavenProperties;
	private String mavenProfiles;
	
	public BuildParameters(){
		atributes = new HashMap<String, Object>();
		mavenProperties = new ArrayList<String>();
		mavenProfiles = null;
	}

	public List<String> getMavenProperties() {
		return mavenProperties;
	}

	public void setMavenProperties(List<String> mavenProperties) {
		this.mavenProperties = mavenProperties;
	}

	public String getMavenProfiles() {
		return mavenProfiles;
	}

	public void setMavenProfiles(String mavenProfiles) {
		this.mavenProfiles = mavenProfiles;
	}

	public Map<String, Object> getAtributes() {
		return atributes;
	}

	public void setAtributes(Map<String, Object> atributes) {
		this.atributes = atributes;
	}
	
	public void addAtribute(String key, Object value) {
		this.atributes.put(key, value);
	}
	
}
