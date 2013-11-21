package com.turnerapac.adultswimau.apps.generic.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class MVPlaylistChildList {

	@ElementList(name="list",inline=true)
	private List<MVPlaylistChild> playlist;

	public List<MVPlaylistChild> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(List<MVPlaylistChild> playlist) {
		this.playlist = playlist;
	}

}
