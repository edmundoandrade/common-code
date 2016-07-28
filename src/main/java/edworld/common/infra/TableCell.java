package edworld.common.infra;

import static edworld.common.infra.util.TextUtil.LINE_BREAK;

import java.util.ArrayList;
import java.util.List;

public class TableCell extends DocumentElement {
	private List<DocumentParagraph> paragraphs = new ArrayList<>();

	public void addParagraph(DocumentParagraph paragraph) {
		paragraphs.add(paragraph);
	}

	public List<DocumentParagraph> getParagraphs() {
		return paragraphs;
	}

	public String toHTML() {
		boolean automaticNumbering = false;
		String result = "<td>";
		for (DocumentParagraph paragraph : getParagraphs()) {
			String html = paragraph.toHTML();
			if (html.startsWith("<p class=\"automatic-numbering\">")) {
				if (!automaticNumbering) {
					html = "<ol>" + LINE_BREAK + html;
					automaticNumbering = true;
				}
				html = html.replace("<p class=\"automatic-numbering\">", "<li>").replace("</p>", "</li>");
			} else if (automaticNumbering) {
				html += LINE_BREAK + "</ol>";
				automaticNumbering = false;
			}
			result += LINE_BREAK + html;
		}
		if (automaticNumbering) {
			result += LINE_BREAK + "</ol>";
			automaticNumbering = false;
		}
		result += LINE_BREAK + "</td>";
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		String prefix = "";
		for (DocumentParagraph paragraph : getParagraphs()) {
			result += prefix + paragraph.toString();
			prefix = LINE_BREAK;
		}
		return result;
	}
}
