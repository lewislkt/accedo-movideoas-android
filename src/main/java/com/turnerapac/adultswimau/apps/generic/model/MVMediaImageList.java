package com.turnerapac.adultswimau.apps.generic.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class MVMediaImageList {
	@ElementList(name="list",inline=true,required=false)
	private List<MVMediaImage> image;

	public List<MVMediaImage> getImage() {
		return image;
	}

	public void setImage(List<MVMediaImage> image) {
		this.image = image;
	}

}
