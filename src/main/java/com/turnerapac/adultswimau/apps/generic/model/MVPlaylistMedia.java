package com.turnerapac.adultswimau.apps.generic.model;

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="media", strict=false)
public class MVPlaylistMedia implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8956605780069108217L;
	@Element(name="id",required=false)
	private String id;
	@Element(name="title",required=false)
	private String title;
	@Element(name="description",required=false)
	private String description;
	@Element(name="duration",required=false)
	private String duration;
	@Element(name="defaultImage",required=false)
	private MVDefaultImage defaultImage;
	@Element(name="mediaPlays",required=false)
	private MVMediaPlays mediaPlays;
	@Element(name="imagePath",required=false)
	private String imagePath;
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public MVMediaPlays getMediaPlays() {
		return mediaPlays;
	}
	public void setMediaPlays(MVMediaPlays mediaPlays) {
		this.mediaPlays = mediaPlays;
	}
	@ElementList(name="tags", required=false)
	private List<MVTag> tags;
	public List<MVTag> getTags() {
		return tags;
	}
	public void setTags(List<MVTag> tags) {
		this.tags = tags;
	}
	public MVDefaultImage getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(MVDefaultImage defaultImage) {
		this.defaultImage = defaultImage;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
