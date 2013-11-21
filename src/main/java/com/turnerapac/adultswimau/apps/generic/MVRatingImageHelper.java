package com.turnerapac.adultswimau.apps.generic;

import com.turnerapac.adultswimau.apps.generic.R;

/*
 * This class returns corresponding media classification images
 */
public class MVRatingImageHelper {

	public static final String RATING_G = "G";

	public static final String RATING_M = "M";

	public static final String RATING_MA = "MA15+";

	public static final String RATING_PG = "PG";

	public static final String RATING_R = "R18+";

	public static int getValue(String classification) {
		if (classification.equals(RATING_G))
			return R.drawable.classification_g;
		if (classification.equals(RATING_M))
			return R.drawable.classification_m;
		if (classification.equals(RATING_MA))
			return R.drawable.classification_ma;
		if (classification.equals(RATING_PG))
			return R.drawable.classification_pg;
		if (classification.equals(RATING_R))
			return R.drawable.classification_r;
		else
			return 0;
	}
}
