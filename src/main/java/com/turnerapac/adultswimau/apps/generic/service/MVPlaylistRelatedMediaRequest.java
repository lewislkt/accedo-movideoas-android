package com.turnerapac.adultswimau.apps.generic.service;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaSearch;

public class MVPlaylistRelatedMediaRequest extends GoogleHttpClientSpiceRequest< MVMediaSearch > {

	String token;
	String mediaId;
    public MVPlaylistRelatedMediaRequest(String token,String mediaId) {
        super( MVMediaSearch.class );
        this.token=token;
        this.mediaId=mediaId;
    }

    @Override
    public MVMediaSearch loadDataFromNetwork() throws Exception {
    	HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl(Constants.BASE_URL+"/media/"+mediaId+"/related?token="+token) );
        Serializer serializer = new Persister();
		return serializer.read(getResultType() ,  request.execute().getContent()); 
    }
}
