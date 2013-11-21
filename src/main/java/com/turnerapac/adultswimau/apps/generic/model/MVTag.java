package com.turnerapac.adultswimau.apps.generic.model;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
@Root
public class MVTag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6110223421896708114L;
	@Element(name="ns",required=false)
	private String ns;
	@Element(name="predicate",required=false)
	private String predicate;
	@Element(name="value",required=false)
	private String value;
	@Element(name="tag",required=false)
	private String tag;
	public String getNs() {
		return ns;
	}
	public void setNs(String ns) {
		this.ns = ns;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
