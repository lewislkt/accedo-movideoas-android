package com.turnerapac.adultswimau.apps.generic.model;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="defaultImage", strict=false)
public class MVDefaultImage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4151553198668542483L;
	@Element(name="url",required=false)
	private String url;
	@Element(name="height",required=false)
	private String height;
	@Element(name="width",required=false)
	private String width;
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
}
