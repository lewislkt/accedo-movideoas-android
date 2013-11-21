package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.turnerapac.adultswimau.apps.generic.PlaylistArrayAdapter;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;


@RunWith(RobolectricTestRunner.class)
public class AdapterTest {
	
	private PlaylistArrayAdapter playListArrayAdapter;
	private ArrayList<MVPlaylistMedia> playlist;
	private MVPlaylistMedia playListMedia;
	
	@Before
	public void setUpEnvironment() throws Exception
	{
		playListMedia= new MVPlaylistMedia();
		playlist = new ArrayList<MVPlaylistMedia>();
		prepareArrayAdapterValues();
		playListArrayAdapter = new PlaylistArrayAdapter(Robolectric.getShadowApplication().getApplicationContext(), 0, playlist);
		
	}
	
	@Test
	public  void shouldBeSame() throws Exception
	{
		Assert.assertEquals(playListArrayAdapter.getCount(), 1);
	}
	
	@Test
	public void shouldPass() throws Exception
	{
		for(int i=2; i <= 1002; i++)
		{
		MVPlaylistMedia newObject = new MVPlaylistMedia();
		newObject.setTitle("TestTitle");
		newObject.setDescription("Lorem Ipsum ");
		playlist.add(newObject);
		Assert.assertEquals(playListArrayAdapter.getCount(), i);
		}
		
	}
	
	@Test
	public void shouldBeSameContent() throws Exception
	{
		
			Assert.assertEquals(playListArrayAdapter.getItem(0).getTitle(), "TestTitle");
			Assert.assertEquals(playListArrayAdapter.getItem(0).getDescription(), "Lorem Ipsum ");
	}

	private void prepareArrayAdapterValues() {
		// TODO Auto-generated method stub
		playListMedia.setTitle("TestTitle");
		playListMedia.setDescription("Lorem Ipsum ");
		playlist.add(playListMedia);
	}
	
	@Test
	public void shouldBeSameView() throws Exception
	{
		View convertView = new View(Robolectric.application);
		ViewGroup parent = new RelativeLayout(Robolectric.application);
		
	}

}
