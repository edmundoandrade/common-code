package edworld.common.infra;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class ResourceXMLTest {
	@Test
	public void sanitizeXML() throws Exception {
		InputStream input = getClass().getResourceAsStream("/sf-agenda.caractere-invalido.xml");
		try {
			InputSource inputSource = new InputSource(
					new StringReader(ResourceXML.sanitizeXML(IOUtils.toString(input, "UTF-8"))));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
			doc.getDocumentElement().normalize();
			assertEquals("Reunioes", doc.getDocumentElement().getNodeName());
		} finally {
			input.close();
		}
	}

	@Test
	public void getProperty() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Reunioes><Reuniao><Codigo>5365</Codigo></Reuniao></Reunioes>";
		Document doc = ResourceXML.parse(xml);
		List<Element> list = ResourceXML.getElements(doc.getDocumentElement(), "Reuniao");
		assertEquals(1, list.size());
		assertEquals("5365", ResourceXML.getProperty(list.get(0), "Codigo"));
		assertEquals("-", ResourceXML.getProperty(list.get(0), "Local", "-"));
	}
}
