package edworld.common.infra;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class ResourceWordTest {
	@Test
	public void getElementsDOCX() {
		List<DocumentElement> elements = new ResourceWord(getClass().getResourceAsStream("/office/table1.docx"))
				.getElements();
		assertEquals(4, elements.size());
		assertEquals("Table I", elements.get(0).toString());
		List<TableRow> tableRows = ((DocumentTable) elements.get(1)).getRows();
		assertThat(tableRows.get(0).toString(), startsWith("Line A: | First line"));
		assertThat(tableRows.get(1).toString(), startsWith("B1: | X | B2: | Y"));
		assertThat(normalize(tableRows.get(2).toString()), startsWith("30ºC:good | {#}First item"));
		assertThat(normalize(tableRows.get(3).toString()), startsWith("Last line:ABC"));
		assertEquals("", elements.get(2).toString());
	}

	@Test
	public void getElementsDOC() {
		List<DocumentElement> elements = new ResourceWord(getClass().getResourceAsStream("/office/table2.doc"))
				.getElements();
		assertEquals(3, elements.size());
		assertEquals("Table II", normalize(elements.get(0).toString()));
		List<TableRow> tableRows = ((DocumentTable) elements.get(1)).getRows();
		assertThat(tableRows.get(0).toString(), startsWith("Line A: | First line"));
		assertThat(tableRows.get(1).toString(), startsWith("B1: | XYZ | B2: |"));
		assertThat(normalize(tableRows.get(2).getCells().get(1).toHTML()), startsWith("<td><ol><li>First item"));
		assertThat(normalize(tableRows.get(2).toString()), startsWith("Acentuação:OK | {#}First item"));
		assertThat(normalize(tableRows.get(3).toString()), startsWith("Last line:ABC |"));
	}

	private String normalize(String contents) {
		return contents.replaceAll("\r\n?|\n", "");
	}
}
