package edworld.common.core;

public class Link {
	private String texto;
	private String href;

	public Link(String texto, String href) {
		this.texto = texto;
		this.href = href;
	}

	public String getTexto() {
		return texto;
	}

	public String getHref() {
		return href;
	}

	public String toHTML() {
		return "<a href=\"" + getHref() + "\">" + getTexto() + "</a>";
	}
}
