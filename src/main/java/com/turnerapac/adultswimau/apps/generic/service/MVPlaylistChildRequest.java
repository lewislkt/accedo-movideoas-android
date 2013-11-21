package com.turnerapac.adultswimau.apps.generic.service;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChildList;

public class MVPlaylistChildRequest extends GoogleHttpClientSpiceRequest< MVPlaylistChildList > {

	String token;
	String playlistId;
	int pageSize = 0;
	
    public MVPlaylistChildRequest(String token,String playlistId) {
        super( MVPlaylistChildList.class );
        this.token=token;
        this.playlistId=playlistId;
    }
    
    public MVPlaylistChildRequest(String token,String playlistId, int pageSize) {
        super( MVPlaylistChildList.class );
        this.token=token;
        this.playlistId=playlistId;
        this.pageSize = pageSize;
    }

    @Override
    public MVPlaylistChildList loadDataFromNetwork() throws Exception {
    	HttpRequest request = null;
    	if(pageSize != 0)
    		request = getHttpRequestFactory()
                .buildGetRequest( new GenericUrl(Constants.BASE_URL+"/playlist/"+playlistId
                		+"/playlists?pageSize="+pageSize+"&token="+token ) );
    	else
    		request = getHttpRequestFactory()
            .buildGetRequest( new GenericUrl(Constants.BASE_URL+"/playlist/"+playlistId
            		+"/playlists?token="+token ) );
        Serializer serializer = new Persister();
		return serializer.read(getResultType() ,  request.execute().getContent());    }
}
