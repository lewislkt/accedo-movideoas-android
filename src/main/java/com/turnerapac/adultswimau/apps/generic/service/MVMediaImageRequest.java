package com.turnerapac.adultswimau.apps.generic.service;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaImageList;

public class MVMediaImageRequest extends GoogleHttpClientSpiceRequest< MVMediaImageList > {

	private String mToken;
	private String mMediaId;
    public MVMediaImageRequest(String token,String mediaId,String searchCriteria) {
        super( MVMediaImageList.class );
        this.mToken=token;
        this.mMediaId=mediaId;
    }

    @Override
    public MVMediaImageList loadDataFromNetwork() throws Exception {
    	HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl(Constants.BASE_URL+"/media/"+mMediaId+"images?token="+mToken ) );
        Serializer serializer = new Persister();
		return serializer.read(getResultType() ,  request.execute().getContent());
    }
}