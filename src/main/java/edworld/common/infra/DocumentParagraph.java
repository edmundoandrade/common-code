package edworld.common.infra;

import static edworld.common.infra.util.TextUtil.fullTrim;

import edworld.common.infra.util.HTMLUtil;

public class DocumentParagraph extends DocumentElement {
	private String text;
	private String numberingFormat;

	public DocumentParagraph(String text, String numberingFormat) {
		this.text = text;
		this.numberingFormat = numberingFormat;
	}

	public String getText() {
		return text;
	}

	public String getNumberingFormat() {
		return numberingFormat;
	}

	public String toHTML() {
		if (getNumberingFormat() == null)
			return "<p>" + HTMLUtil.escapeHTML(fullTrim(text)) + "</p>";
		return "<p class=\"automatic-numbering\">" + HTMLUtil.escapeHTML(fullTrim(text)) + "</p>";
	}

	@Override
	public String toString() {
		String prefix = getNumberingFormat() == null ? "" : "{#}";
		return prefix + text;
	}
}
