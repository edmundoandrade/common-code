package edworld.common.infra.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

import edworld.common.core.Link;

public abstract class TextUtil {
	public static final String LINE_BREAK = System.getProperty("line.separator");

	public static String standard(String name) {
		if (name == null)
			return null;
		return removeDiacritics(name.toLowerCase()).replaceAll("[\\s/<=>;:\\.,()\\?]", "_").replaceAll("_d[aeo]_", "_");
	}

	public static String removeDiacritics(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String fullTrim(String text) {
		String result = text == null ? null : text.replaceAll("(?is)\\s", " ").trim();
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
			return DataUtil.dataToString((Calendar) conteudo);
		if (conteudo instanceof String[])
			return formatar(pack((String[]) conteudo, "", " | ", ""));
		if (conteudo instanceof Link)
			return ((Link) conteudo).toHTML();
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
}
