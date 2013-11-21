package com.turnerapac.adultswimau.apps.generic.model;

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="playlist", strict=false)
public class MVPlaylistChild implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3743683107214854830L;
	@Element(name="id",required=false)
	private String id;
	@Element(name="title",required=false)
	private String title;
	@Element(name="defaultImage",required=false)
	private MVDefaultImage defaultImage;
	@Element(name="imagePath",required=false)
	private String imagePath;
	@ElementList(name="tags", required=false)
	private List<MVTag> tags;
	public List<MVTag> getTags() {
		return tags;
	}
	public void setTags(List<MVTag> tags) {
		this.tags = tags;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public MVDefaultImage getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(MVDefaultImage defaultImage) {
		this.defaultImage = defaultImage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
