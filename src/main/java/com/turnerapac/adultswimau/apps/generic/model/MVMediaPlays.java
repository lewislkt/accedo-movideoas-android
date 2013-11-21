package com.turnerapac.adultswimau.apps.generic.model;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="mediaPlays", strict=false)
public class MVMediaPlays implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6474095930679999447L;
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	@Element(name="day",required=false)
	private String day;
	@Element(name="month",required=false)
	private String month;
	@Element(name="week",required=false)
	private String week;
	@Element(name="total",required=false)
	private String total;

}
