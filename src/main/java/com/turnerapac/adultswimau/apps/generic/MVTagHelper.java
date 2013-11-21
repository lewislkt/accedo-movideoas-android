package com.turnerapac.adultswimau.apps.generic;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.testflightapp.lib.TestFlight;
import com.turnerapac.adultswimau.apps.generic.globalsearch.SearchTabListAdapter;
import com.turnerapac.adultswimau.apps.generic.model.MVTag;

/*
 * This class is used to get tag value for a particular ns and predicate
 */
public class MVTagHelper {

	public static final String NS_SHOW = "show";

	public static final String PREDICATE_NAME = "name";

	public static final String NS_EPISODE = "episode";

	public static final String PREDICATE_NUMBER = "number";

	public static final String PREDICATE_SEASON = "season";

	public static final String NS_CLASSIFICATION = "classification";

	public static final String PREDICATE_RATING = "rating";

	public static final String NS_PLAYLIST = "playlist";

	public static final String PREDICATE_PATH = "path";

	public static String getValue(List<MVTag> tags, String ns, String predicate) {

		for (MVTag mvTag : tags) {
			try {
				if (mvTag.getNs().equals(ns)
						&& mvTag.getPredicate().equals(predicate))
					return mvTag.getValue();
			} catch (NullPointerException ex) {
				org.slf4j.Logger mLog;
				mLog = LoggerFactory.getLogger(SearchTabListAdapter.class);
				mLog.info("Tag Null", ex);
				TestFlight.log("tag null for ns " + ns + " predicate "
						+ predicate);
			}
		}
		return "";
	}
}
