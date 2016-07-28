package edworld.common.infra;

import static edworld.common.infra.util.TextUtil.formatar;

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

public class RecursoXML extends RecursoWEB<Document> {
	public RecursoXML(final String enderecoURL) {
		this(enderecoURL, Config.getEncoding());
	}

	public RecursoXML(final String enderecoURL, final String encoding) {
		super(enderecoURL, new Callable<Document>() {
			@Override
			public Document call() throws Exception {
				if (enderecoURL.startsWith("file:"))
					return read(new URL(enderecoURL).openStream(), encoding);
				CloseableHttpClient httpclient = openHttpClient();
				try {
					CloseableHttpResponse response = httpclient.execute(httpGet(enderecoURL));
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
	public void arquivar(File destino) {
		destino.getParentFile().mkdirs();
		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(getConteudo()),
					new StreamResult(destino));
		} catch (TransformerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public List<Element> getElementos(String nome) {
		return getElementos(getConteudo().getDocumentElement(), nome);
	}

	public static String getConteudoPropriedade(Element entidade, String propriedade) {
		NodeList elements = entidade.getElementsByTagName(propriedade);
		if (elements.getLength() == 0 || elements.item(0).getTextContent().trim().isEmpty())
			return null;
		return elements.item(0).getTextContent().trim();
	}

	public static List<Element> getElementos(Element entidade, String nome) {
		return toList(entidade.getElementsByTagName(nome));
	}

	private static List<Element> toList(NodeList itens) {
		List<Element> lista = new ArrayList<Element>();
		for (int i = 0; i < itens.getLength(); i++)
			lista.add((Element) itens.item(i));
		return lista;
	}

	public static List<Element> getElementosXPath(Element entidade, String xPath) {
		List<Element> lista = new ArrayList<Element>();
		try {
			NodeList itens = (NodeList) XPathFactory.newInstance().newXPath().compile(xPath).evaluate(entidade,
					XPathConstants.NODESET);
			for (int i = 0; i < itens.getLength(); i++)
				lista.add((Element) itens.item(i));
			return lista;
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

	public static String sanitizeXML(String conteudo) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < conteudo.length(); i++)
			if (XMLChar.isValid(conteudo.charAt(i)))
				sb.append(conteudo.charAt(i));
		return sb.toString();
	}

	public static String elementoXML(String nome, Object conteudo, String... atributos) {
		String seqAtributos = "";
		for (String atributo : atributos)
			seqAtributos += " " + atributo;
		return "<" + nome + seqAtributos + ">" + formatar(conteudo) + "</" + nome + ">";
	}
}
