package edworld.common.infra;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class RecursoXMLTest {
	@Test
	public void sanitizeXML() throws Exception {
		InputStream input = getClass().getResourceAsStream("/sf-agenda.caractere-invalido.xml");
		try {
			InputSource inputSource = new InputSource(
					new StringReader(RecursoXML.sanitizeXML(IOUtils.toString(input, "UTF-8"))));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
			doc.getDocumentElement().normalize();
			assertEquals("Reunioes", doc.getDocumentElement().getNodeName());
		} finally {
			input.close();
		}
	}
}
