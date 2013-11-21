package com.turnerapac.adultswimau.apps.generic.service;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVApplication;

public class MVAppInitRequest extends GoogleHttpClientSpiceRequest<MVApplication> {

	String appAlias, appKey;

	public MVAppInitRequest(String applicationAlias, String applicationKey) {
		super(MVApplication.class);
		this.appAlias = applicationAlias;
		this.appKey = applicationKey;
	}

	@Override
    public MVApplication loadDataFromNetwork() throws Exception {
    	HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( Constants.BASE_URL+"/session?applicationalias="+appAlias+"&key="+appKey ) );
        Serializer serializer = new Persister();
		return serializer.read(getResultType() ,  request.execute().getContent());
        
    }
}
