package main.java.com.storagecombine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Epoch {
	private static final String TIME_FORMAT = "EEE MMM d HH:mm:ss zzz yyyy";

	public static long getNow() {
		return System.currentTimeMillis()/1000;
	}

	public static Date toDate(long epoch) {
		return new Date(epoch * 1000);
	}

	public static long toEpoch(String s) {
		SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT);

		try {
			Date d = df.parse(s);
			return d.getTime() / 1000;
		} catch (Exception e) {
			return 0;
		}
	}

	public static String toString(long epoch) {
		SimpleDateFormat d = new SimpleDateFormat(TIME_FORMAT);
		return d.format(toDate(epoch));
	}
}
