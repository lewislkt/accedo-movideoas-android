package com.turnerapac.adultswimau.apps.generic.service;

import java.net.URLEncoder;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVPlayListSearch;

public class MVPlayListSearchRequest extends
		GoogleHttpClientSpiceRequest<MVPlayListSearch> {

	String token;

	int pages;

	int PageSize = Constants.SEARCH_PAGE_SIZE;

	String id = Constants.EMPTY_DATA;

	String name = Constants.EMPTY_DATA;

	public MVPlayListSearchRequest(String token, String id,
			String name, int pages) {
		super(MVPlayListSearch.class);
		this.token = token;
		this.id = id;
		this.name = name;
		this.pages = pages;
	}

	@Override
	public MVPlayListSearch loadDataFromNetwork() throws Exception {
		String search = "";
		if (!name.equals(Constants.EMPTY_DATA))
			search = search + name;
		String encodedurl = URLEncoder.encode(search, "utf-8");
		String url = Constants.BASE_URL + "/playlist/search?token=" + token+"&title="
				+ encodedurl+"&id="+id;
		HttpRequest request = getHttpRequestFactory().buildGetRequest(
				new GenericUrl(url));
		Serializer serializer = new Persister();
		return serializer.read(getResultType(), request.execute().getContent());

	}
}
