package edworld.common.infra.util;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexUtil {
	public static Pattern regexHTML(String regexHTML) {
		return Pattern.compile(regexHTML.replace("...", "[^<>]*").replace("___", "[^'\"]*"));
	}

	public static String extractHTMLContents(String text) {
		return unescapeHtml4(regexHTML("<...>").matcher(text).replaceAll("").replaceAll("\\s+", " ").trim());
	}

	public static String firstOccurrence(Pattern regex, String text) {
		Matcher matcher = regex.matcher(text);
		if (matcher.find())
			return matcher.group(1).trim().replaceAll("\\s+", " ");
		return null;
	}

	public static List<String> listOccurrences(Pattern regex, String text) {
		return listOccurrences(regex, text, 1);
	}

	public static List<String> listOccurrences(Pattern regex, String text, int groupIndex) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = regex.matcher(text);
		while (matcher.find()) {
			String occurrence = matcher.group(groupIndex).trim().replaceAll("\\s+", " ");
			if (!result.contains(occurrence))
				result.add(occurrence);
		}
		return result;
	}
}
