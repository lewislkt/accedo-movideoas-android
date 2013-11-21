package com.turnerapac.adultswimau.apps.generic.service;

import android.app.Application;

import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileInputStreamObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.xml.SimpleSerializerObjectPersisterFactory;
import com.octo.android.robospice.persistence.string.InFileStringObjectPersister;

public class XmlSpiceService extends GoogleHttpClientSpiceService {

	@Override
	public CacheManager createCacheManager(Application application) throws CacheCreationException {
		CacheManager cacheManager = new CacheManager();
		InFileStringObjectPersister inFileStringObjectPersister = new InFileStringObjectPersister(application);
		InFileInputStreamObjectPersister inFileInputStreamObjectPersister = new InFileInputStreamObjectPersister(
				application);
		/*
		 * XML Cache Persistor used from spring module for enabling caching.
		 */
		SimpleSerializerObjectPersisterFactory xmlObjectPersisterFactory = new SimpleSerializerObjectPersisterFactory(application);
		
		xmlObjectPersisterFactory.setAsyncSaveEnabled(true);
		inFileStringObjectPersister.setAsyncSaveEnabled(true);
		inFileInputStreamObjectPersister.setAsyncSaveEnabled(true);
		
		cacheManager.addPersister(xmlObjectPersisterFactory);
		cacheManager.addPersister(inFileStringObjectPersister);
		cacheManager.addPersister(inFileInputStreamObjectPersister);
		
		return cacheManager;
	}

}
