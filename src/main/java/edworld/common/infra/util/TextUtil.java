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
	public static final String LINE_BREAK = System.getProperty("line.separator");

	public static String standard(String name) {
		if (name == null)
			return null;
		return removeDiacritics(name.toLowerCase()).replaceAll("[\\s/<=>;:\\.,()\\?]", "_")
				.replaceAll("_d[aeo]s?_", "_").replaceAll("^(.*)_d[aeo]s?$", "$1");
	}

	public static String removeDiacritics(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String fullTrim(String text) {
		String result = text == null ? null : text.replaceAll("(?is)(\\s|\\xA0)", " ").trim();
		return result == null || result.isEmpty() ? null : result;
	}

	public static String formatar(Object conteudo) {
		if (conteudo == null)
			return "";
		if (conteudo instanceof Boolean)
			return (Boolean) conteudo ? "Sim" : "Não";
		if (conteudo instanceof BigDecimal)
			return ((BigDecimal) conteudo).toPlainString().replace(",", "").replace(".", ",");
		if (conteudo instanceof Calendar)
			return DateUtil.dateToString((Calendar) conteudo);
		if (conteudo instanceof String[])
			return formatar(pack((String[]) conteudo, "", " | ", ""));
		if (conteudo instanceof Link)
			return ((Link) conteudo).toHTML();
		if (conteudo instanceof Image)
			return ((Image) conteudo).toHTML();
		return conteudo.toString().trim();
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
		String remainingText = text.trim();
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
		return list.toArray(new String[list.size()]);
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
