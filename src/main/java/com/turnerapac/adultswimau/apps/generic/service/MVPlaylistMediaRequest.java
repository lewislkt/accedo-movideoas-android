package com.turnerapac.adultswimau.apps.generic.service;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMediaList;

public class MVPlaylistMediaRequest extends GoogleHttpClientSpiceRequest< MVPlaylistMediaList > {

	String token;
	String playlistId;
	int mPage = -1;
	int mPageSize = Constants.SEARCH_PAGE_SIZE;
	
    public MVPlaylistMediaRequest(String token,String playlistId) {
        super( MVPlaylistMediaList.class );
        this.token=token;
        this.playlistId=playlistId;
    }
    
    public MVPlaylistMediaRequest(String token,String playlistId, int page) {
        super( MVPlaylistMediaList.class );
        this.token=token;
        this.playlistId=playlistId;
        this.mPage = page;
    }

    @Override
    public MVPlaylistMediaList loadDataFromNetwork() throws Exception {
    	HttpRequest request ;
    	if(mPage == -1)
    		request = getHttpRequestFactory()
                .buildGetRequest( new GenericUrl(Constants.BASE_URL+"/playlist/"+playlistId+"/media?token="+token) );
    	else
    		request = getHttpRequestFactory()
            .buildGetRequest( new GenericUrl(Constants.BASE_URL+"/playlist/"+playlistId+"/media?page="+mPage+"&pageSize="+mPageSize+"&token="+token) );
    	Serializer serializer = new Persister();
		return serializer.read(getResultType() ,  request.execute().getContent()); 
    }
}
