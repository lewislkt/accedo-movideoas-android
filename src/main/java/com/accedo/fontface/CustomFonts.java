package com.accedo.fontface;

import android.content.Context;
import android.graphics.Typeface;

import com.turnerapac.adultswimau.apps.generic.Constants;

public class CustomFonts {
	
	//Declaring font variables
	Typeface mHeadingFont;
	
	//Variable for Context
	Context mContext;
	
	public CustomFonts(Context mContext)
	{
		this.mContext = mContext;
	}
	
	public Typeface getHeadingfont()
	{
		mHeadingFont = Typeface.createFromAsset(mContext.getAssets(), Constants.headingFontFaceUri);
		return mHeadingFont;
	}
	

}
