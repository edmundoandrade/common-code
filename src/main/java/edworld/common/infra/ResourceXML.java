package edworld.common.infra;

import static edworld.common.infra.util.TextUtil.format;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.xml.utils.XMLChar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ResourceXML extends ResourceWEB<Document> {
	public ResourceXML(final String url) {
		this(url, Config.getEncoding());
	}

	public ResourceXML(final String url, final String encoding) {
		super(url, new Callable<Document>() {
			@Override
			public Document call() throws Exception {
				if (url.startsWith("file:"))
					return read(new URL(url).openStream(), encoding);
				CloseableHttpClient httpclient = openHttpClient();
				try {
					CloseableHttpResponse response = httpclient.execute(httpGet(url));
					try {
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
							throw new IllegalArgumentException(errorDetail(response, encoding));
						return read(response.getEntity().getContent(), encoding);
					} finally {
						response.close();
					}
				} finally {
					closeHttpClient(httpclient);
				}
			}

			private Document read(InputStream input, String encoding) throws Exception {
				try {
					InputSource inputSource = new InputSource(
							new StringReader(sanitizeXML(IOUtils.toString(input, encoding))));
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
					doc.getDocumentElement().normalize();
					return doc;
				} finally {
					input.close();
				}
			}
		});
	}

	@Override
	public void save(File target) {
		target.getParentFile().mkdirs();
		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(getContents()),
					new StreamResult(target));
		} catch (TransformerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public List<Element> getElements(String name) {
		return getElements(getContents().getDocumentElement(), name);
	}

	public static String getProperty(Element entity, String property) {
		return getProperty(entity, property, null);
	}

	public static String getProperty(Element entity, String property, String defaultTextContent) {
		NodeList elements = entity.getElementsByTagName(property);
		if (elements.getLength() == 0 || elements.item(0).getTextContent().trim().isEmpty())
			return defaultTextContent;
		return elements.item(0).getTextContent().trim();
	}

	public static List<Element> getElements(Element entity, String name) {
		return toList(entity.getElementsByTagName(name));
	}

	private static List<Element> toList(NodeList items) {
		List<Element> list = new ArrayList<Element>();
		for (int i = 0; i < items.getLength(); i++)
			list.add((Element) items.item(i));
		return list;
	}

	public static List<Element> getXPathElements(Element entity, String xPath) {
		List<Element> list = new ArrayList<Element>();
		try {
			NodeList itens = (NodeList) XPathFactory.newInstance().newXPath().compile(xPath).evaluate(entity,
					XPathConstants.NODESET);
			for (int i = 0; i < itens.getLength(); i++)
				list.add((Element) itens.item(i));
			return list;
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Document parse(String xml) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(sanitizeXML(xml).getBytes(Config.getEncoding())));
			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String sanitizeXML(String contents) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < contents.length(); i++)
			if (XMLChar.isValid(contents.charAt(i)))
				sb.append(contents.charAt(i));
		return sb.toString();
	}

	public static String xmlElement(String name, Object contents, String... attributes) {
		String seqAttributes = "";
		for (String attribute : attributes)
			seqAttributes += " " + attribute;
		return "<" + name + seqAttributes + ">" + format(contents) + "</" + name + ">";
	}
}
