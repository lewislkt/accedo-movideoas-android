package com.turnerapac.adultswimau.apps.generic.model;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class MVMediaSearch {
	
	@Element(name="currentPage",required=false)
	private Integer currentPage;
	@Element(name="totalPages",required=false)
	private Integer totalPages;
	@Element(name="totalItems",required=false)
	private Integer totalItems;
	@Element(name="itemsPerPage",required=false)
	private Integer itemsPerPage;
	@ElementList(name="list",inline=false,required=false)
	private List<MVPlaylistMedia> media;
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public Integer getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}
	public Integer getItemsPerPage() {
		return itemsPerPage;
	}
	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	public List<MVPlaylistMedia> getMedia() {
		return media;
	}
	public void setMedia(List<MVPlaylistMedia> media) {
		this.media = media;
	}

}
