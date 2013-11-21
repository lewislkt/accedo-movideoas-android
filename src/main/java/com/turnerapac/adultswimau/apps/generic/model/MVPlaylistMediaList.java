package com.turnerapac.adultswimau.apps.generic.model;

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class MVPlaylistMediaList implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1943734522511063835L;
	@ElementList(name="list",inline=true,required=false)
	private List<MVPlaylistMedia> media;

	public List<MVPlaylistMedia> getMedia() {
		return media;
	}

	public void setMedia(List<MVPlaylistMedia> media) {
		this.media = media;
	}

}