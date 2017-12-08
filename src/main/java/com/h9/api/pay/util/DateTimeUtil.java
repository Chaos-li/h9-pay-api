package com.h9.api.pay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

	SimpleDateFormat yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

	public static String dateToyyyyMMddHHmmss(Date date) {
		return yyyyMMddHHmmss.format(date);
	}


}
