package edworld.common.core;

public class Image {
	private String src;
	private String alt;
	private Integer width;
	private Integer height;

	public Image(String src, String alt, Integer width, Integer height) {
		this.src = src;
		this.alt = alt;
		this.width = width;
		this.height = height;
	}

	public String getSrc() {
		return src;
	}

	public String getAlt() {
		return alt;
	}

	private Integer getWidth() {
		return width;
	}

	private Integer getHeight() {
		return height;
	}

	public String toHTML() {
		return "<img src=\"" + getSrc() + "\" class=\"img-responsive\" alt=\"" + getAlt() + "\" width=\"" + getWidth()
				+ "\" height=\"" + getHeight() + "\">";
	}
}
