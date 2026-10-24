package com.multipolar.uiteller.backend;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.google.common.io.ByteStreams;

public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {
	private Logger logger = LogManager.getLogger("access-third-log");
	
	// @Autowired
	// private HttpRequestLogRepository httpRequestLogRepository;
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		ClientHttpResponse response = execution.execute(request, body);

		String requestBody = "";
		// todo change hardcoded to multipart-form (do not print multipart-form body)
		if (StringUtils.containsIgnoreCase(request.getURI().toString(), "siskohat/bank/setoranawal"))
			requestBody = "[multipart-form body]";
		else
			requestBody = new String(body, Charset.forName("UTF-8"));

		String output = String.format("request method: %s, request URI: %s, request headers: %s, request body: %s, response status code: %s, response headers: %s, response body: %s", 
				request.getMethod(),
	            request.getURI(),
	            request.getHeaders(),
	            requestBody,
	            response.getStatusCode(),
	            response.getHeaders(),
	            new String(ByteStreams.toByteArray(response.getBody()), Charset.forName("UTF-8")));
        logger.info(output);
		
        /*
        HttpRequestLog log = new HttpRequestLog();
        log.setReqMethod(request.getMethod().toString());
        log.setReqUri(request.getURI().toString());
        log.setReqHeaders(request.getHeaders().toString());
        log.setReqBody(new String(body, Charset.forName("UTF-8")));
        log.setResStatus(response.getStatusCode().toString());
        log.setResHeaders(response.getHeaders().toString());
        log.setResBody(new String(ByteStreams.toByteArray(response.getBody()), Charset.forName("UTF-8")));
        httpRequestLogRepository.save(log);
        */
 
        return response;
	}

}
