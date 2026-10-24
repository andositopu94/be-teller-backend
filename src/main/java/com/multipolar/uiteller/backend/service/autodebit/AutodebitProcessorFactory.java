package com.multipolar.uiteller.backend.service.autodebit;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutodebitProcessorFactory {
	private final List<AutoDebitAware> autoDebitProcessors;
	private static Logger logger = LoggerFactory.getLogger(AutodebitProcessorFactory.class);

    @Autowired
    public AutodebitProcessorFactory(List<AutoDebitAware> autoDebitProcessors) {
        this.autoDebitProcessors = autoDebitProcessors;
    }

    public AutoDebitAware getAutoDebitProcessor(String s) {
    	AutoDebitAware defaultProcessor = null;
    	for (AutoDebitAware processor : autoDebitProcessors) {
    		if (processor.supports(s))
    			return processor;
    		
    		if (processor.getClass().equals(DefaultAutodebitProcessor.class))
    			defaultProcessor = processor;
    	}
    	
    	if (defaultProcessor == null) 
    		logger.error("No handler found for this type feature, application will not run properly. Feature: " + s);
    	
    	// here, no hander support for the requested feature, return default implementation
    	return defaultProcessor;
    }
}
