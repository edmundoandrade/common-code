package edworld.common.infra;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class RecursoWordTest {
	@Test
	public void getElementsDOCX() {
		List<DocumentElement> elements = new RecursoWord(getClass().getResourceAsStream("/office/table1.docx"))
				.getElements();
		assertEquals(4, elements.size());
		assertEquals("Table I", elements.get(0).toString());
		List<TableRow> linhasTabela = ((DocumentTable) elements.get(1)).getRows();
		assertThat(linhasTabela.get(0).toString(), startsWith("Line A: | First line"));
		assertThat(linhasTabela.get(1).toString(), startsWith("B1: | X | B2: | Y"));
		assertThat(normalize(linhasTabela.get(2).toString()), startsWith("30ºC:good | {#}First item"));
		assertThat(normalize(linhasTabela.get(3).toString()), startsWith("Last line:ABC"));
		assertEquals("", elements.get(2).toString());
	}

	@Test
	public void getElementsDOC() {
		List<DocumentElement> elements = new RecursoWord(getClass().getResourceAsStream("/office/table2.doc"))
				.getElements();
		assertEquals(3, elements.size());
		assertEquals("Table II", normalize(elements.get(0).toString()));
		List<TableRow> linhasTabela = ((DocumentTable) elements.get(1)).getRows();
		assertThat(linhasTabela.get(0).toString(), startsWith("Line A: | First line"));
		assertThat(linhasTabela.get(1).toString(), startsWith("B1: | XYZ | B2: |"));
		assertThat(normalize(linhasTabela.get(2).getCells().get(1).toHTML()), startsWith("<td><ol><li>First item"));
		assertThat(normalize(linhasTabela.get(2).toString()), startsWith("Acentuação:OK | {#}First item"));
		assertThat(normalize(linhasTabela.get(3).toString()), startsWith("Last line:ABC |"));
	}

	private String normalize(String conteudo) {
		return conteudo.replaceAll("\r\n?|\n", "");
	}
}
