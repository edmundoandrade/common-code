package edworld.common.infra.util;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edworld.common.core.Link;

public class HTMLUtil {
	public static final String LF = System.getProperty("line.separator");
	private static final String TEMPLATE_CONTENT = "${content}";

	public static List<String> extractLinksHTML(String html) {
		return extractTrechosHTML(html, Pattern.compile("(?is)<a href=\"([^\"]*)\"[^>]*>.*?</a>"));
	}

	public static List<String> extractHeadingsHTML(String html) {
		return extractHeadingsHTML(html, null);
	}

	public static List<String> extractHeadingsHTML(String html, Integer level) {
		String levelRule = level == null ? "\\d+" : level.toString();
		return extractTrechosHTML(html, Pattern.compile("(?is)<h" + levelRule + "[^>]*>(.*?)</h" + levelRule + ">"));
	}

	public static List<String> extractTablesHTML(String html) {
		return extractTrechosHTML(html, Pattern.compile("(?is)<table[^>]*>.*?</table>"), 0, "<table");
	}

	public static List<String> extractTableRowsHTML(String table) {
		return extractTrechosHTML(table, Pattern.compile("(?is)<tr[^>]*>.*?</tr>"), 0, "<tr");
	}

	public static List<String> extractTableCellsHTML(String tableRow) {
		return extractTrechosHTML(tableRow, Pattern.compile("(?is)<t[dh][^>]*>.*?</t[dh]>"));
	}

	public static String encodeURLParam(String parametro) {
		try {
			return URLEncoder.encode(parametro, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static List<String> extractTrechosHTML(String html, Pattern regex) {
		return extractTrechosHTML(html, regex, 0);
	}

	public static List<String> extractTrechosHTML(String html, Pattern regex, int groupIndex) {
		return extractTrechosHTML(html, regex, groupIndex, null);
	}

	public static List<String> extractTrechosHTML(String html, Pattern regex, int groupIndex,
			String fromLastOcurrence) {
		List<String> trechos = new ArrayList<String>();
		Matcher matcher = regex.matcher(html);
		while (matcher.find()) {
			String item = matcher.group(groupIndex);
			if (fromLastOcurrence != null)
				item = item.substring(item.lastIndexOf(fromLastOcurrence));
			trechos.add(item);
		}
		return trechos;
	}

	public static String textHTML(String html) {
		return textHTML(html, Pattern.compile("(?is)<[^/>]*>([^<]*)</[^>]*>")).trim();
	}

	public static String textHTML(String html, Pattern regex) {
		StringBuffer sb = new StringBuffer();
		Matcher matcher = regex.matcher(html);
		while (matcher.find())
			matcher.appendReplacement(sb, textHTML(matcher.group(1), regex));
		matcher.appendTail(sb);
		String result = sb.toString();
		if (regex.matcher(result).find())
			return textHTML(result, regex);
		return unescapeHTML(result);
	}

	public static void openHTML(String pageTemplate, PrintStream out, String title, String... navigation) {
		openHTML(pageTemplate, "/", out, title, navigation);
	}

	public static void openHTML(String pageTemplate, String rootPath, PrintStream out, String title,
			String... navigation) {
		String navigationHTML = "";
		for (int i = 0; i < navigation.length; i += 2)
			navigationHTML += "<li><a href=\"" + navigation[i + 1] + "\">" + navigation[i] + "</a></li>" + LF;
		String template = fillRootPath(pageTemplate, rootPath);
		template = template.replace("${title}", title);
		template = template.replace("${navigation}", navigationHTML);
		out.println(template.substring(0, template.indexOf(TEMPLATE_CONTENT)));
	}

	public static String fillRootPath(String template, String rootPath) {
		return template.replace("~/", rootPath);
	}

	public static void closeHTML(String pageTemplate, PrintStream out) {
		out.print(pageTemplate.substring(pageTemplate.indexOf(TEMPLATE_CONTENT) + TEMPLATE_CONTENT.length()));
	}

	public static String escapeHTML(String html) {
		return escapeHtml4(html);
	}

	public static String unescapeHTML(String html) {
		return unescapeHtml4(html).replaceAll("\\p{Z}", " ");
	}

	public static String composeLink(String texto, URI uri) {
		return new Link(texto, uri.toString()).toHTML();
	}
}
