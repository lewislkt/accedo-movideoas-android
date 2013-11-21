package com.turnerapac.adultswimau.apps.generic;

import org.robolectric.bytecode.ClassInfo;
import org.robolectric.bytecode.Setup;

public class EnhancedSetup extends Setup {

    @Override
    public boolean isFromAndroidSdk(ClassInfo classInfo) {
        return super.isFromAndroidSdk(classInfo) || classInfo.getName().startsWith("com.google.ads.")
                || classInfo.getName().startsWith("com.google.analytics.");
    }

}
