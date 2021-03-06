package edworld.common.infra;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.Matrix;

import edworld.common.core.Link;
import edworld.common.infra.util.TextUtil;

public class ResourcePDF {
	private static final Color LINK_COLOR = Color.BLUE;
	private static final Color TITLE_COLOR = Color.BLUE;
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final float TITLE_FONT_SIZE = 12.0F;
	private static final float CONTINUATION_FONT_SIZE = 9.0F;
	private static final float FOOTER_FONT_SIZE = 9.0F;
	private PDFont normalFont = PDType1Font.HELVETICA;
	private PDFont boldFont = PDType1Font.HELVETICA_BOLD;
	private String title;
	private String version;
	private PDDocument doc;
	private BufferedImage logo;
	private PDPageContentStream pageContent;
	private float pageHeight;
	private float pageWidth;
	private String contact;
	private URL logoURL;
	private PDPage currentPage;
	private PDFont currentFont;

	public ResourcePDF(String title, String version, String contact, URL logoURL) {
		this.title = title;
		this.version = version;
		this.contact = contact;
		this.logoURL = logoURL;
		doc = new PDDocument();
	}

	public PDFont getNormalFont() {
		return normalFont;
	}

	public PDFont getBoldFont() {
		return boldFont;
	}

	public void newPortraitPage() {
		PDPage page = newPage(false);
		pageHeight = page.getMediaBox().getHeight();
		pageWidth = page.getMediaBox().getWidth();
	}

	public void newLandscapePage() {
		PDPage page = newPage(true);
		pageHeight = page.getMediaBox().getWidth();
		pageWidth = page.getMediaBox().getHeight();
	}

	protected PDPage newPage(boolean paisagem) {
		try {
			closePage();
			PDPage page = new PDPage(PDRectangle.A4);
			if (paisagem)
				page.setRotation(90);
			doc.addPage(page);
			currentPage = page;
			pageContent = new PDPageContentStream(doc, page);
			if (paisagem)
				pageContent.transform(new Matrix(0, 1, -1, 0, page.getMediaBox().getWidth(), 0));
			return page;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public float getPageHeight() {
		return pageHeight;
	}

	public float getPageWidth() {
		return pageWidth;
	}

	public float showHeader(float y, boolean continuation) {
		float y0 = y;
		try {
			showImage(getLogo(), 10F, y, 0.1F, 0.1F);
			y -= 20F;
			pageContent.setNonStrokingColor(TITLE_COLOR);
			y -= showTextLine(40F, y, title, normalFont, TITLE_FONT_SIZE);
			pageContent.setNonStrokingColor(TEXT_COLOR);
			if (continuation)
				y -= showTextLine(40F, y, "(continuação)", normalFont, CONTINUATION_FONT_SIZE);
			y -= 5F;
			return y0 - y;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public float showText(float x, float y, String text, PDFont font, float fontSize, int lineLength) {
		return showText(x, y, text, font, fontSize, lineLength, x, lineLength);
	}

	public float showText(float x, float y, String text, PDFont font, float fontSize, int lineLength, float firstLineX,
			int firstLineLength) {
		float y0 = y;
		for (String linha : TextUtil.wordWrap(text, lineLength, firstLineLength))
			y -= showTextLine(y == y0 ? firstLineX : x, y, linha, font, fontSize, false);
		return y0 - y;
	}

	public float showTextLine(float x, float y, String text, PDFont font, float fontSize) {
		return showTextLine(x, y, text, font, fontSize, false);
	}

	public float showTextLine(float x, float y, String text, PDFont font, float fontSize, boolean truncate) {
		try {
			pageContent.beginText();
			setFont(font, fontSize);
			pageContent.newLineAtOffset(x, y);
			showText(truncate ? truncate(text) : text);
			pageContent.endText();
			return textHeight(font, fontSize);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public float showLink(float x, float y, Link link, PDFont font, float fontSize) {
		try {
			pageContent.setNonStrokingColor(LINK_COLOR);
			float dy = showTextLine(x, y, link.getTexto(), font, fontSize);
			pageContent.setNonStrokingColor(TEXT_COLOR);
			PDActionURI action = new PDActionURI();
			action.setURI(link.getHref());
			PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
			borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
			PDAnnotationLink txtLink = new PDAnnotationLink();
			txtLink.setBorderStyle(borderULine);
			txtLink.setColor(new PDColor(LINK_COLOR.getRGBColorComponents(null), null));
			txtLink.setAction(action);
			txtLink.setRectangle(
					new PDRectangle(x, y - 2, textWidth(link.getTexto(), font, fontSize), textHeight(font, fontSize)));
			currentPage.getAnnotations().add(txtLink);
			return dy;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public float showImage(BufferedImage image, float x, float y, float widthScale, float heightScale) {
		try {
			PDImageXObject pdImage = createImage(image);
			pageContent.drawImage(pdImage, x, y, pdImage.getWidth() * widthScale, pdImage.getHeight() * heightScale);
			return pdImage.getHeight() * heightScale;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void showFooter() {
		try {
			pageContent.setNonStrokingColor(LINK_COLOR);
			pageContent.beginText();
			setFont(normalFont, FOOTER_FONT_SIZE);
			pageContent.newLineAtOffset(40F, 30F);
			showText("Versão de " + version);
			pageContent.newLineAtOffset(250F, 0F);
			showText(contact);
			pageContent.endText();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void drawLine(float xI, float yI, float xF, float yF) {
		try {
			pageContent.moveTo(xI, yI);
			pageContent.lineTo(xF, yF);
			pageContent.stroke();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected void closePage() {
		if (pageContent == null)
			return;
		try {
			pageContent.close();
			pageContent = null;
			currentPage = null;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void close() {
		closePage();
		if (doc == null)
			return;
		try {
			doc.close();
			doc = null;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void save(String fileName) {
		closePage();
		try {
			doc.save(fileName);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected PDImageXObject createImage(BufferedImage image) throws IOException {
		return LosslessFactory.createFromImage(doc, image);
	}

	protected BufferedImage getLogo() {
		if (logo == null)
			try {
				logo = ImageIO.read(logoURL);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		return logo;
	}

	protected float textHeight(PDFont font, float fontSize) throws IOException {
		return font.getBoundingBox().getHeight() * fontSize / 700F;
	}

	protected float textWidth(String text, PDFont font, float fontSize) throws IOException {
		return font.getStringWidth(text) * fontSize / 1000F;
	}

	protected String truncate(String text) {
		if (text.length() < 145)
			return text;
		return text.substring(0, 143) + "...";
	}

	protected void setFont(PDFont font, float fontSize) throws IOException {
		pageContent.setFont(font, fontSize);
		currentFont = font;
	}

	protected void showText(String text) throws IOException {
		pageContent.showText(checkFontGlyphs(text, currentFont));
	}

	protected String checkFontGlyphs(String text, PDFont font) throws IOException {
		if (hasValidFontGlyphs(text, font))
			return text;
		String result = "";
		for (int i = 0; i < text.length(); i++)
			if (hasValidFontGlyphs(result + text.charAt(i), font))
				result += text.charAt(i);
			else
				result += " ";
		return result;
	}

	protected boolean hasValidFontGlyphs(String text, PDFont font) throws IOException {
		try {
			font.encode(text);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
