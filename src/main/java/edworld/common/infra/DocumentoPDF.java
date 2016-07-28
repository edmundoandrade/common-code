package edworld.common.infra;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.w3c.dom.Element;

import edworld.common.infra.util.PDFUtil;

public abstract class DocumentoPDF {
	protected URL url;
	protected boolean infoLoaded;
	private Element rootElement;

	public DocumentoPDF(File recurso) {
		try {
			this.url = recurso.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public DocumentoPDF(URL url) {
		this.url = url;
	}

	public String toXML() {
		return PDFUtil.toXML(url);
	}

	public List<String> toTextLines() {
		return PDFUtil.toTextLines(url);
	}

	public List<Element> getElementosXML() {
		return RecursoXML.getElementosXPath(getRootElement(), "page/*");
	}

	public List<Element> getElementosXMLTextuais() {
		return RecursoXML.getElementos(getRootElement(), "text");
	}

	private Element getRootElement() {
		if (rootElement == null)
			rootElement = RecursoXML.parse(toXML()).getDocumentElement();
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
