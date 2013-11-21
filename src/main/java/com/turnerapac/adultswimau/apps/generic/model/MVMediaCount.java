package com.turnerapac.adultswimau.apps.generic.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "ItemCounter", strict = false)
public class MVMediaCount {

	@Element(name = "totalItems", required = false)
	private String totalItems;

	public String getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(String totalItems) {
		this.totalItems = totalItems;
	}

	
}
