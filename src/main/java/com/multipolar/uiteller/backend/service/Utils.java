package com.multipolar.uiteller.backend.service;

import java.util.Calendar;
import java.util.Date;

public class Utils {
	public static Date getFirstDateOfYear() {
		Calendar cld = Calendar.getInstance();
		cld.set(Calendar.DAY_OF_YEAR,1); 
		return cld.getTime();
	}
}
