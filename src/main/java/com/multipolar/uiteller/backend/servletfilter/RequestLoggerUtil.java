package com.multipolar.uiteller.backend.servletfilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.ActivityLog;
import com.multipolar.uiteller.backend.repository.ActivityLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;

@Component
public class RequestLoggerUtil {
	private static final Logger logAccess = LogManager.getLogger("access-log");
	
	@Autowired
	private ActivityLogRepository activityRepository;
	
	public void log(HttpServletRequest request, HttpServletResponse response) {
    	ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
    	String body = "";
    	if (wrapper != null) {
    		byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                try {
                    body = new String(buf, 0, length, wrapper.getCharacterEncoding());
                }
                catch (UnsupportedEncodingException ex) {
                    // NOOP
                }
            }
    	}
    	
    	Enumeration<String> reqHeaders = request.getHeaderNames();
    	Collection<String> resHeaders = response.getHeaderNames();
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append("\n");
    	builder.append("REQUEST: ");
    	String query = request.getQueryString();
    	String queryString = StringUtils.isEmpty(query) ? "" : "?" + query;
    	builder.append("Requet URL: ");
    	builder.append(request.getRequestURI() + queryString);
    	builder.append("\n");
    	builder.append("Request Method: ");
    	builder.append(request.getMethod());
    	builder.append("\n");
    	builder.append("Remote Address: ");
    	builder.append(request.getRemoteAddr());
    	builder.append("\n");
    	builder.append("Request Headers: ");
    	builder.append("\n");
    	while (reqHeaders.hasMoreElements()) {
    		String headerName = reqHeaders.nextElement();
    		String headerValue = request.getHeader(headerName);
    		String value = StringUtils.left(headerValue, 100) + (StringUtils.length(headerValue) > 30 ? "..." : "");
    		builder.append("\t");
    		builder.append(headerName);
    		builder.append(" : ");
    		builder.append(value);
    		builder.append("\n");
    	}
    	builder.append("BODY");
    	builder.append(body);
    	builder.append("\n");    	
    	
    	builder.append("RESPONSE: ");
    	builder.append("Response Status: ");
    	builder.append(response.getStatus());
    	builder.append("\n");
    	builder.append("Response Headers: ");
    	builder.append("\n");
    	for (String resHeader : resHeaders) {
    		String headerValue = response.getHeader(resHeader);
    		String value = StringUtils.left(headerValue, 100) + (StringUtils.length(headerValue) > 30 ? "..." : "");
    		builder.append("\t");
    		builder.append(resHeader);
    		builder.append(" : ");
    		builder.append(value);
    		builder.append("\n");
    	}
    	builder.append("BODY");
    	builder.append("\n");
    	String resp = getResponsePayload(response);
    	ObjectMapper mapper = new ObjectMapper();
		try {
			Object json = mapper.readValue(resp, Object.class);
			builder.append(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
		} catch (IOException e) {
			builder.append(resp);
		}	   	
    	builder.append("\n");
    	    	
    	logAccess.info(builder.toString());
    }

    private String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {

            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                }
                catch (UnsupportedEncodingException ex) {
                    // NOOP
                }
            }
        }
        return "[unknown]";
    }
    
    public void logActivity(HttpServletRequest request, HttpServletResponse response, String userid) {
    	ActivityLog log = new ActivityLog();
    	log.setUserId(userid);
    	log.setIpAddress(request.getRemoteAddr());
    	log.setRequestUrl(request.getRequestURI());
    	log.setRequestId(response.getHeader(Constants.REQUEST_ID_HEADER));
    	
    	activityRepository.save(log);    	
    }
}
