package com.multipolar.uiteller.backend.service.iso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageConverterFactory {
	// list of all ConverterHandler services that extends AbstractMessageConverter
	private final List<MessageConverterHandler> converterHandlerServices;

	private static Logger logger = LoggerFactory.getLogger(MessageConverterFactory.class);

    @Autowired
    public MessageConverterFactory(List<MessageConverterHandler> converterHandlerServices) {
        this.converterHandlerServices = converterHandlerServices;
    }

	// all ConverterHandler being in loops to get the exact service that matches the context, for example, context: pbbInquiry will get PbbConverterHandler
    public MessageConverterHandler get(String s) {
		MessageConverterHandler defaultHandler = null;
    	for (MessageConverterHandler handler : converterHandlerServices) {
    		if (handler.supports(s))
    			return handler;
    		
    		if (handler.getClass().equals(DefaultConverterHandler.class))
    			defaultHandler = handler;
    	}

    	if (defaultHandler == null)
    		logger.error("No handler found for this type feature, application will not run properly. Feature: " + s);
    	
    	// here, if no handler support for the requested feature, return default implementation
    	return defaultHandler;
    }
}
