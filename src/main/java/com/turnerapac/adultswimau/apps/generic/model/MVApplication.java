package com.turnerapac.adultswimau.apps.generic.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="session", strict=false)
public class MVApplication {

	@Element(name="token",required=false)
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
