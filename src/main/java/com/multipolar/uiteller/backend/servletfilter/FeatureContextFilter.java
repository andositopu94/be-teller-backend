package com.multipolar.uiteller.backend.servletfilter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class FeatureContextFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		String feature = getFeatureFromRequest(httpServletRequest.getRequestURI());
		
	    if (feature != null) {
	        FeatureContextHolder.getContext().setFeatureName(feature);
	    }

	    String instansiRequestParam = getInstansiRequestParam(request);
	    if (StringUtils.isNotEmpty(instansiRequestParam))
	    	FeatureContextHolder.getContext().setInstansiParameter(instansiRequestParam);
	    	
	    chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// do nothing
	}
	
	private String getFeatureFromRequest(String requestUri) {
		String result = StringUtils.substringBetween(requestUri, "/apiv2/", "?");
		if (result == null)
			result = StringUtils.substringBetween(requestUri, "/apiv2/", "/");

		if (result == null)
			result = StringUtils.substringAfter(requestUri, "/apiv2/");

		if (result == "")
			result = StringUtils.substringBetween(requestUri, "/apiv3/", "?");

		if (result == null)
			result = StringUtils.substringBetween(requestUri, "/apiv3/", "/");

		if (result == null)
			result = StringUtils.substringAfter(requestUri, "/apiv3/");

		if (result == "")
			result = StringUtils.substringBetween(requestUri, "/apiv4/", "?");

		if (result == null)
			result = StringUtils.substringBetween(requestUri, "/apiv4/", "/");

		if (result == null)
			result = StringUtils.substringAfter(requestUri, "/apiv4/");

		if (result == "")
			result = StringUtils.substringBetween(requestUri, "/apiv5/", "?");

		if (result == null)
			result = StringUtils.substringBetween(requestUri, "/apiv5/", "/");

		if (result == null)
			result = StringUtils.substringAfter(requestUri, "/apiv5/");

		if (result == "")
			result = StringUtils.substringBetween(requestUri, "/apiv6/", "?");

		if (result == null)
			result = StringUtils.substringBetween(requestUri, "/apiv6/", "/");

		if (result == null)
			result = StringUtils.substringAfter(requestUri, "/apiv6/");

		return result;
	}

	private String getInstansiRequestParam(ServletRequest request) {
		String result = null;
		if (StringUtils.isNotEmpty(request.getParameter("instansi")))
			result = request.getParameter("instansi");

		return result;
	}

}
