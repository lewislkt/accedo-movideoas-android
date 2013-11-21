package com.turnerapac.adultswimau.apps.generic;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.Setup;

public class EnhancedRoboTestRunner extends RobolectricTestRunner {

    public EnhancedRoboTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public Setup createSetup() {
        return new EnhancedSetup();
    }

}