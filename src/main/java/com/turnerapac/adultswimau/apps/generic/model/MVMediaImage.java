package com.turnerapac.adultswimau.apps.generic.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "media", strict = false)
public class MVMediaImage {

	@Element(name = "url", required = false)
	private String url;

	@Element(name = "height", required = false)
	private String height;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	@Element(name = "width", required = false)
	private String width;
}
