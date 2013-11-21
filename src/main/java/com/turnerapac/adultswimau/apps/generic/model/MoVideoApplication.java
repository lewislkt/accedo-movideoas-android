package com.turnerapac.adultswimau.apps.generic.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="session", strict=false)
public class MoVideoApplication {

	@Element(name="token")
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
