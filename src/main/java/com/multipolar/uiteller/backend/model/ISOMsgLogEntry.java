package com.multipolar.uiteller.backend.model;

import java.util.Date;

import org.jpos.iso.ISOMsg;

import lombok.Data;

@Data
public class ISOMsgLogEntry {
	private ISOMsg message;
	private Date date;
	
	public ISOMsgLogEntry() {}
	
	public ISOMsgLogEntry(ISOMsg msg, Date date) {
		this.message = msg;
		this.date = date;
	}
}
