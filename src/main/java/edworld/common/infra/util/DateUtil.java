package edworld.common.infra.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edworld.common.infra.boundary.TimestampCalendar;

public abstract class DateUtil {
	public static final DateFormat FORMAT_DATE = new SimpleDateFormat("dd/MM/yyyy");
	public static final DateFormat FORMAT_DATE_ISO = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat FORMAT_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	public static final DateFormat FORMAT_DATE_TIME_ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	public static final DateFormat FORMAT_TIME = new SimpleDateFormat("HH:mm");
	public static final DateFormat FORMAT_TIMESTAMP = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
	public static final DateFormat FORMAT_BIRTHDAY = new SimpleDateFormat("dd/MM");
	public static final DateFormat FORMAT_DATE_RANGE = new SimpleDateFormat("dd/MMMMM/yyyy");

	public static Calendar date(int day, int month, int year) {
		return new GregorianCalendar(year, month - 1, day);
	}

	public static Calendar dateTime(int day, int month, int year, int hour, int minute) {
		return dateTime(day, month, year, hour, minute, 0);
	}

	public static Calendar dateTime(int day, int month, int year, int hour, int minute, int second) {
		Calendar calendar = date(day, month, year);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar;
	}

	public static TimestampCalendar timestamp(long timeInMillis) {
		Calendar timestamp = new GregorianCalendar();
		timestamp.setTimeInMillis(timeInMillis);
		return new TimestampCalendar(timestamp);
	}

	public static String dateToString(Calendar date) {
		return dateToString(date, FORMAT_DATE);
	}

	public static String dateToString(Calendar date, DateFormat format) {
		if (date == null)
			return null;
		return format.format(date.getTime());
	}

	public static String dateTimeToString(Calendar dateTime) {
		if (dateTime == null)
			return null;
		return FORMAT_DATE_TIME.format(dateTime.getTime());
	}

	public static String timeToString(Calendar date) {
		return dateToString(date, FORMAT_TIME);
	}

	public static String timeStampToString(Calendar timestamp) {
		if (timestamp == null)
			return null;
		return FORMAT_TIMESTAMP.format(timestamp.getTime());
	}

	public static String timeStampToString(TimestampCalendar timestamp) {
		return timeStampToString(timestamp.getTimestamp());
	}

	public static Calendar parseDate(String date) {
		return parseDateTime(date.replaceAll("(\\d+)[º°]", "$1"), FORMAT_DATE);
	}

	public static Calendar parseDateISO(String date) {
		return parseDateTime(date, FORMAT_DATE_ISO);
	}

	public static Calendar parseDateTime(String dateTime) {
		return parseDateTime(dateTime, FORMAT_DATE_TIME);
	}

	public static Calendar parseDateTimeISO(String dateTime) {
		return parseDateTime(dateTime, FORMAT_DATE_TIME_ISO);
	}

	public static Calendar parseTimeStamp(String timeStamp) {
		return parseDateTime(timeStamp, FORMAT_TIMESTAMP);
	}

	public static Calendar parseDateTime(String dateTime, DateFormat format) {
		Calendar resultado = GregorianCalendar.getInstance();
		try {
			resultado.setTime(format.parse(dateTime));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		return resultado;
	}

	public static Calendar date(Calendar dateTime) {
		Calendar date = (Calendar) dateTime.clone();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date;
	}

	public static Calendar addDays(Calendar startDate, int days) {
		Calendar date = (Calendar) startDate.clone();
		date.add(Calendar.DAY_OF_MONTH, days);
		// ajustar após somar dias a uma data que inicia horário de verão
		if (date.get(Calendar.HOUR_OF_DAY) == 1)
			date.set(Calendar.HOUR_OF_DAY, 0);
		return date;
	}

	public static String formatDuration(long duration) {
		long hour = (duration / 3600000);
		if (hour == 0)
			return (duration / 60000) + "min " + ((duration % 60000) / 1000) + "s";
		long remaining = duration % 3600000;
		return hour + "h" + (remaining / 60000) + "min" + ((remaining % 60000) / 1000) + "s";
	}

	public static Calendar now() {
		return Calendar.getInstance();
	}

	public static Calendar today() {
		return date(now());
	}

	public static Calendar dateSameWeek(Calendar date, int weekDay) {
		return addDays(date, weekDay - weekDay(date));
	}

	public static int weekDay(Calendar date) {
		return date.get(Calendar.DAY_OF_WEEK);
	}

	public static int day(Calendar date) {
		return date.get(Calendar.DAY_OF_MONTH);
	}

	public static int month(Calendar date) {
		return date.get(Calendar.MONTH) + 1;
	}

	public static int year(Calendar date) {
		return date.get(Calendar.YEAR);
	}

	public static Calendar parseStartDate(String range) {
		return parseStartDate(range, FORMAT_DATE_RANGE);
	}

	public static Calendar parseStartDate(String range, DateFormat format) {
		String start = extractStartDate(range);
		String end = extractEndDate(range);
		int numberOfParts = start.split("/").length;
		for (int i = 0; i < numberOfParts; i++)
			end = end.replaceFirst(".*?/", "");
		return DateUtil.parseDateTime(start + "/" + end, format);
	}

	public static Calendar parseEndDate(String range) {
		return parseEndDate(range, FORMAT_DATE_RANGE);
	}

	public static Calendar parseEndDate(String range, DateFormat format) {
		return DateUtil.parseDateTime(extractEndDate(range), format);
	}

	public static String parseTime(String text) {
		String result = text.replace("h", ":");
		if (!result.contains(":"))
			result += ":";
		while (result.indexOf(":") < 2)
			result = "0" + result;
		while (result.length() < 5)
			result = result.substring(0, 3) + "0" + result.substring(3);
		return result;
	}

	private static String extractStartDate(String range) {
		return range.trim().toLowerCase().replaceAll("\\s+(a|até|e)\\s+.*", "").replace("º", "")
				.replaceAll("\\s+de\\s+", "/").replaceAll("\\s+", "/");
	}

	private static String extractEndDate(String range) {
		return range.trim().toLowerCase().replaceAll(".*\\s+(a|até|e)\\s+", "").replace("º", "")
				.replaceAll("\\s+de\\s+", "/").replaceAll("\\s+", "/");
	}
}
