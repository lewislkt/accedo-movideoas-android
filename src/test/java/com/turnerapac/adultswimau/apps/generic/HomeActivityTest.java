package com.turnerapac.adultswimau.apps.generic;


import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowView;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.TestHttpResponse;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.apache.http.protocol.HttpContext;

import com.turnerapac.adultswimau.apps.generic.FeaturedFragment;
import com.turnerapac.adultswimau.apps.generic.HomeActivity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;


@RunWith(RobolectricTestRunner.class)
public class HomeActivityTest extends ShadowView{

	//Instance of ActionBar
	ActionBar actionBar = null;
	
	//Variable for Tab Count 
	int tabCount = 0;
	
	//Instance of HomeActivity
	HomeActivity mHomeActivity;
	
	//Instance of FeaturedFragment
	FeaturedFragment mFeatured;
	
	private DefaultRequestDirector requestDirector;
	private ConnectionKeepAliveStrategy connectionKeepAliveStrategy;
	
	@Before
	public void setUp() throws Exception
	{
		
		//Initialising HomeActivity object
		mHomeActivity = new HomeActivity();
		
		//Initialising actionBar with HomeActivity
		//actionBar = mHomeActivity.getActionBar();
		
		//Initialising Featured Fragment Object
		mFeatured = new FeaturedFragment();
		
		FakeHttpLayer fakeHttpLayer = Robolectric.getFakeHttpLayer();
	    Assert.assertFalse(fakeHttpLayer.hasPendingResponses());
	    Assert.assertFalse(fakeHttpLayer.hasRequestInfos());
	    Assert.assertFalse(fakeHttpLayer.hasResponseRules());
	    Assert.assertNull(fakeHttpLayer.getDefaultResponse());

	    connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
	    	
	      public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
	        return 0;
	      }
	    };
	    requestDirector = new DefaultRequestDirector(null, null, null, connectionKeepAliveStrategy, null, null, null, null, null, null, null, null);
	    
	    Robolectric.addPendingHttpResponse(new TestHttpResponse(200, "a happy response body"));
		
	}
	
	@Test
	public void shouldNotBeEmpty() throws Exception
	{
		 
		startFragment(mFeatured);
	}
	

		public static void startFragment( Fragment fragment )
		{
		    FragmentActivity activity = Robolectric.buildActivity( FragmentActivity.class )
		                                           .create()
		                                           .start()
		                                           .resume()
		                                           .get();

		    FragmentManager fragmentManager = activity.getFragmentManager();
		    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		    fragmentTransaction.add( fragment, null );
		    fragmentTransaction.commit();
		}

}
