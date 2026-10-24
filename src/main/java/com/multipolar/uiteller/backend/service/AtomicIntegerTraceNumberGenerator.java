package com.multipolar.uiteller.backend.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.repository.TraceNumberRepository;

@Service("Atomic")
public class AtomicIntegerTraceNumberGenerator implements TraceNumberGenerator {
	
	@Autowired
	private TraceNumberRepository numberRepository;
	
	@Override
	public String getTraceNumber() {
		String retval = numberRepository.getTraceNumber().toString();
		if (StringUtils.length(retval) > 6)
			return StringUtils.substring(retval, 0, 6);
		else
			return StringUtils.leftPad(retval, 6, '0');
	}	

	@Override
	public String getReferenceNumber() {
		String retval = numberRepository.getReferenceNumber().toString();
		if (StringUtils.length(retval) > 12)
			return StringUtils.substring(retval, 0, 12);
		else
			return StringUtils.leftPad(retval, 12, '0');
	}
	
	
	/*
	private static long value;
	private static AtomicInteger traceNumber;
	static {
		value = System.currentTimeMillis() / 10000000;
		traceNumber = new AtomicInteger((int)value);
	}
	
	@Override
	public String getTraceNumber() {
		String retval = String.valueOf(traceNumber.incrementAndGet());
		if (StringUtils.length(retval) > 6)
			return StringUtils.substring(retval, 0, 6);
		else 
			return StringUtils.leftPad(retval, 6);
	}

	@Override
	public String getReferenceNumber() {
		String retval = String.valueOf(traceNumber.incrementAndGet());
		if (StringUtils.length(retval) > 6)
			return StringUtils.substring(retval, 0, 6);
		else 
			return StringUtils.leftPad(retval, 6);
	}
	*/
}
