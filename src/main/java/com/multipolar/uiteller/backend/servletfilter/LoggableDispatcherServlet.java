package com.multipolar.uiteller.backend.servletfilter;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jpos.iso.ISOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.SecurityConstants;

public class LoggableDispatcherServlet extends DispatcherServlet {

	private static final long serialVersionUID = -611298403904430003L;
	
	private RequestLoggerUtil loggerUtil;
	
	public LoggableDispatcherServlet(RequestLoggerUtil loggerUtil) {
		this.loggerUtil = loggerUtil;
	}

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.addHeader(Constants.REQUEST_ID_HEADER, String.valueOf(System.currentTimeMillis()));
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
//        HandlerExecutionChain handler = getHandler(request);

        try {
            super.doDispatch(request, response);
        } catch (BusinessException bx) {
        	throw new BusinessException(bx.getMessage());
        } catch (ConnectException ex) {
        	throw new BusinessException("Backend server tidak terkoneksi ke Switching. Hubungi administrator");
        } catch (ISOException ex) {
        	ex.printStackTrace();
        	throw new BusinessException("Terjadi kesalahan saat mengirim pesan atau menerima pesan dari switching. Hubungi administrator");
        } catch (Exception ex) {
        	ex.printStackTrace();
        	throw new BusinessException("Terjadi kesalahan pada system yang belum terdefinisi. Hubungi System administrator");
        } finally {
            loggerUtil.log(request, response);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getPrincipal().toString() : SecurityConstants.ANONYMOUS_USER;
            loggerUtil.logActivity(request, response, username);
            updateResponse(response);
        }
    }

    

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
            WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }
}
