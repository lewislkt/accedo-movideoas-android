package com.turnerapac.adultswimau.apps.generic;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import com.turnerapac.adultswimau.apps.generic.AboutActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.TextView;

@RunWith(RobolectricTestRunner.class)
public class AboutActivityTest {
	
	AboutActivity mAboutActivity;
	TextView mAboutUsHeadingText,mVersionText,mAboutUsContent;
	String mAboutUsHeading = "";
	
	@Before
	public void setUp() throws Exception
	{
		mAboutActivity = new AboutActivity();
		@SuppressWarnings("rawtypes")
		ActivityController activity = Robolectric.buildActivity(AboutActivity.class);
		activity.create(null);
		mAboutActivity.onCreate(null);
		mAboutUsHeadingText = (TextView)mAboutActivity.findViewById(R.id.txt_app_name);
		mAboutUsHeading = mAboutUsHeadingText.getText().toString();
		
		mVersionText = (TextView)mAboutActivity.findViewById(R.id.txt_version);
		
		mAboutUsContent =(TextView)mAboutActivity.findViewById(R.id.txt_description);
		
	}
	
	@Test
	public void shouldHaveHappySmiles() throws Exception {
		String appName = new AboutActivity().getResources().getString(R.string.app_name);
		assertThat(appName, equalTo("adult swim au"));
	}
	
	@Test
	public void shoulNotBeNull() throws Exception
	{
		Assert.assertNotNull(mVersionText.getText().toString());
		Assert.assertNotNull(mAboutUsContent.getText().toString());
	}
	
	@Test
	public void shouldBeSame() throws Exception
	{
		Assert.assertEquals(mAboutUsHeading,"[adult swim]");
		Assert.assertEquals(mVersionText.getText().toString(), getVersionName());
	}
	
	public String getVersionName()
	{
		PackageInfo mInfo = null;
		try
		{
			PackageManager mPackageManager = mAboutActivity.getPackageManager();
			mInfo = mPackageManager.getPackageInfo(mAboutActivity.getPackageName(), 0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		
		return "Version " + mInfo.versionName;
	}

}
