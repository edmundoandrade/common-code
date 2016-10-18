package edworld.common.infra;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.w3c.dom.Element;

import edworld.common.infra.util.PDFUtil;

public abstract class PDFDocument {
	protected URL url;
	protected boolean infoLoaded;
	private Element rootElement;

	public PDFDocument(File file) {
		try {
			this.url = file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public PDFDocument(URL url) {
		this.url = url;
	}

	public String toXML() {
		return PDFUtil.toXML(url);
	}

	public List<String> toTextLines() {
		return PDFUtil.toTextLines(url);
	}

	public List<Element> getXMLElements() {
		return ResourceXML.getXPathElements(getRootElement(), "page/*");
	}

	public List<Element> getXMLTextElements() {
		return ResourceXML.getElements(getRootElement(), "text");
	}

	private Element getRootElement() {
		if (rootElement == null)
			rootElement = ResourceXML.parse(toXML()).getDocumentElement();
		return rootElement;
	}

	protected void loadInfoIfNecessary() {
		if (infoLoaded)
			return;
		loadInfo();
		infoLoaded = true;
	}

	protected abstract void loadInfo();
}
