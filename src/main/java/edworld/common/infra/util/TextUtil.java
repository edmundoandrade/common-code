package edworld.common.infra.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import edworld.common.core.Image;
import edworld.common.core.Link;

public abstract class TextUtil {
	public static final String REGEX_TERMS_TO_IGNORE = "[aeo]s?|d[aeo]s?|por|pel[ao]s?|com|sem|nem|em|n[ao]s?|ou";
	public static final String LINE_BREAK = System.getProperty("line.separator");

	public static String standard(String name) {
		if (name == null)
			return null;
		return removeDiacritics(name.toLowerCase()).replaceAll("[\\s/<=>;:\\.,()\\?]", "_")
				.replaceAll("^(" + REGEX_TERMS_TO_IGNORE + ")_(.*)$", "$2")
				.replaceAll("_(" + REGEX_TERMS_TO_IGNORE + ")_", "_")
				.replaceAll("^(.*)_(" + REGEX_TERMS_TO_IGNORE + ")$", "$1");
	}

	public static String removeDiacritics(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String fullTrim(String text) {
		String result = text == null ? null : text.replaceAll("(?is)(\\s|\\xA0)", " ").trim();
		return result == null || result.isEmpty() ? null : result;
	}

	public static String format(Object data) {
		if (data == null)
			return "";
		if (data instanceof Boolean)
			return (Boolean) data ? "Sim" : "Não";
		if (data instanceof BigDecimal)
			return ((BigDecimal) data).toPlainString().replace(",", "").replace(".", ",");
		if (data instanceof Calendar)
			return DateUtil.dateToString((Calendar) data);
		if (data instanceof String[])
			return format(pack((String[]) data, "", " | ", ""));
		if (data instanceof Link)
			return ((Link) data).toHTML();
		if (data instanceof Image)
			return ((Image) data).toHTML();
		return data.toString().trim();
	}

	public static String pack(String[] items) {
		if (items == null || items.length == 0)
			return null;
		String result = "|";
		for (String item : items)
			result += item + "|";
		return result;
	}

	public static String pack(String[] items, String start, String between, String end) {
		if (items == null || items.length == 0)
			return null;
		String result = "";
		String prefix = "";
		for (String item : items) {
			result += prefix + item;
			prefix = between;
		}
		return start + result + end;
	}

	public static String[] unpack(String data) {
		if (data == null)
			return null;
		String[] parts = data.split("\\|");
		return ArrayUtils.subarray(parts, 1, parts.length);
	}

	public static String quoted(String text) {
		return "'" + text.replaceAll("'", "''") + "'";
	}

	public static String unixLineEndings(String text) {
		return text.replaceAll("\r\n?", "\n");
	}

	public static String[] toLines(String text) {
		return text.split("\r\n?|\n");
	}

	public static String[] wordWrap(String text, int lineLength) {
		return wordWrap(text, lineLength, lineLength);
	}

	public static String[] wordWrap(String text, int lineLength, int firstLineLength) {
		List<String> list = new ArrayList<>();
		for (String line : TextUtil.toLines(text)) {
			String remainingText = line.trim();
			int length = firstLineLength;
			while (remainingText.length() > length) {
				int pos = remainingText.substring(0, length).lastIndexOf(' ');
				if (pos < 0)
					pos = length;
				list.add(remainingText.substring(0, pos).trim());
				remainingText = remainingText.substring(pos).trim();
				length = lineLength;
			}
			list.add(remainingText);
		}
		return list.toArray(new String[list.size()]);
	}

	public static String normalizePhonemes(String text) {
		return standard(treatNasalVowels(text)).replaceAll("s?ch", "x").replace("h", "").replaceAll("[ey]", "i")
				.replace("o", "u").replace("ci", "si").replaceAll("r([cdfglpqst])", "$1").replaceAll("n([cdst])", "$1")
				.replaceAll("m([pb])", "$1").replaceAll("z|r?ç", "s").replaceAll("sc|qu|k", "c").replaceAll("gu|j", "g")
				.replace("cc", "c").replace("ff", "f").replace("gg", "g").replace("ll", "l").replace("mm", "m")
				.replace("nn", "n").replace("pp", "p").replace("qq", "q").replace("rr", "r").replace("ss", "s")
				.replace("tt", "t").replace("ii", "i").replace("uu", "u");
	}

	private static String treatNasalVowels(String text) {
		return text.replaceAll("(?i)([aeiou])n ", "$1m ").replaceAll("(?i)^(.*[aeiou])n$", "$1m");
	}

	public static String chk(String parameter) {
		return hasContent(parameter) ? parameter.trim() : null;
	}

	public static String[] chk(String[] parameter) {
		if (hasContent(parameter)) {
			List<String> result = new ArrayList<>();
			for (String item : parameter)
				if (hasContent(item))
					result.add(item);
			return result.toArray(new String[result.size()]);
		}
		return null;
	}

	public static Integer chkInt(String parameter) {
		return hasContent(parameter) ? Integer.parseInt(parameter.trim()) : null;
	}

	public static BigDecimal chkDec(String parameter) {
		return hasContent(parameter) ? new BigDecimal(parameter.trim()) : null;
	}

	public static BigDecimal chkDec(String parameter, String thousandSeparator, String decimalSeparator) {
		return hasContent(parameter)
				? new BigDecimal(parameter.trim().replace(thousandSeparator, "").replace(decimalSeparator, ".")) : null;
	}

	public static Double chkDbl(String parameter) {
		return hasContent(parameter) ? Double.parseDouble(parameter.trim()) : null;
	}

	public static Double chkDbl(String parameter, String thousandSeparator, String decimalSeparator) {
		return hasContent(parameter)
				? Double.parseDouble(parameter.trim().replace(thousandSeparator, "").replace(decimalSeparator, "."))
				: null;
	}

	public static Boolean chkBool(String parameter, Boolean valorDefault) {
		return hasContent(parameter) ? Boolean.parseBoolean(parameter.trim()) : valorDefault;
	}

	public static boolean hasContent(String parameter) {
		return parameter != null && !parameter.trim().isEmpty() && !parameter.trim().equals("(null)");
	}

	public static boolean hasContent(String[] parameter) {
		if (parameter != null)
			for (String item : parameter)
				if (hasContent(item))
					return true;
		return false;
	}
}
