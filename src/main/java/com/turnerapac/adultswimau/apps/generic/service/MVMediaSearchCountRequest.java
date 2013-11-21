package com.turnerapac.adultswimau.apps.generic.service;

import java.net.URLEncoder;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaCount;

public class MVMediaSearchCountRequest extends GoogleHttpClientSpiceRequest<MVMediaCount> {

	String token;

	int pages;

	int PageSize = Constants.SEARCH_PAGE_SIZE;

	String searchCriteria = Constants.EMPTY_DATA;

	String keywords = Constants.EMPTY_DATA;

	public MVMediaSearchCountRequest(String token, String keywords, String searchCriteria, int pages) {
		super(MVMediaCount.class);
		this.token = token;
		this.searchCriteria = searchCriteria;
		this.keywords = keywords;
		this.pages = pages;
	}

	@Override
	public MVMediaCount loadDataFromNetwork() throws Exception {
		String search = "";
		if (!keywords.equals(Constants.EMPTY_DATA))
			search = "&keywords=" + URLEncoder.encode(keywords, "utf-8");;
		if (!searchCriteria.equals(Constants.EMPTY_DATA))
			search = search + searchCriteria;
		String url = Constants.BASE_URL+"/media/search?token="+token+search+"&page="+pages+"&pageSize="+PageSize; 
		HttpRequest request = getHttpRequestFactory().buildGetRequest( new GenericUrl( url ) );
		Serializer serializer = new Persister();
		return serializer.read(getResultType() ,  request.execute().getContent());
       
	}
}
