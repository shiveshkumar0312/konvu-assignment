package com.konvu.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleLogger {
    private static final Logger logger = LoggerFactory.getLogger(SampleLogger.class);

    public void logInfo(){
        logger.info("message 123 args");
    }
}
