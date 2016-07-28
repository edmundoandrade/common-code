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

	public static String extrairConteudoHTML(String texto) {
		return unescapeHtml4(regexHTML("<...>").matcher(texto).replaceAll("").replaceAll("\\s+", " ").trim());
	}

	public static String primeiraOcorrencia(Pattern regex, String texto) {
		Matcher matcher = regex.matcher(texto);
		if (matcher.find())
			return matcher.group(1).trim().replaceAll("\\s+", " ");
		return null;
	}

	public static List<String> listarOcorrencias(Pattern regex, String texto) {
		return listarOcorrencias(regex, texto, 1);
	}

	public static List<String> listarOcorrencias(Pattern regex, String texto, int indiceGrupo) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = regex.matcher(texto);
		while (matcher.find()) {
			String ocorrencia = matcher.group(indiceGrupo).trim().replaceAll("\\s+", " ");
			if (!result.contains(ocorrencia))
				result.add(ocorrencia);
		}
		return result;
	}
}
