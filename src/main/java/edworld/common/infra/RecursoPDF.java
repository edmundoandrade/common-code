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
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import edworld.common.infra.util.TextUtil;

public class RecursoPDF {
	private PDFont fonteNormal = PDType1Font.HELVETICA;
	private PDFont fonteNegrito = PDType1Font.HELVETICA_BOLD;
	private int TAMANHO_FONTE_TITULO = 12;
	private int TAMANHO_FONTE_CONTINUACAO = 9;
	private int TAMANHO_FONTE_RODAPE = 9;
	private String titulo;
	private String versao;
	private PDDocument doc;
	private PDImageXObject logo;
	private PDPageContentStream conteudoPagina;
	private float alturaPagina;
	private float larguraPagina;
	private String contato;
	private URL logoURL;

	public RecursoPDF(String titulo, String versao, String contato, URL logoURL) {
		this.titulo = titulo;
		this.versao = versao;
		this.contato = contato;
		this.logoURL = logoURL;
		doc = new PDDocument();
		try {
			logo = LosslessFactory.createFromImage(doc, getLogotipo());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public PDFont getFonteNormal() {
		return fonteNormal;
	}

	public PDFont getFonteNegrito() {
		return fonteNegrito;
	}

	public void novaPaginaRetrato() {
		PDPage page = novaPagina(false);
		alturaPagina = page.getMediaBox().getHeight();
		larguraPagina = page.getMediaBox().getWidth();
	}

	public void novaPaginaPaisagem() {
		PDPage page = novaPagina(true);
		alturaPagina = page.getMediaBox().getWidth();
		larguraPagina = page.getMediaBox().getHeight();
	}

	protected PDPage novaPagina(boolean paisagem) {
		try {
			closePage();
			PDPage page = new PDPage(PDRectangle.A4);
			if (paisagem)
				page.setRotation(90);
			doc.addPage(page);
			conteudoPagina = new PDPageContentStream(doc, page);
			if (paisagem)
				conteudoPagina.transform(new Matrix(0, 1, -1, 0, page.getMediaBox().getWidth(), 0));
			return page;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public float getAlturaPagina() {
		return alturaPagina;
	}

	public float getLarguraPagina() {
		return larguraPagina;
	}

	public float exibirCabecalho(float y, boolean continuacao) {
		float y0 = y;
		try {
			conteudoPagina.drawImage(logo, 10F, y, logo.getWidth() * 0.1F, logo.getHeight() * 0.1F);
			y -= 20F;
			conteudoPagina.setNonStrokingColor(Color.BLUE);
			y -= exibirLinhaTexto(40F, y, titulo, fonteNormal, TAMANHO_FONTE_TITULO);
			conteudoPagina.setNonStrokingColor(Color.BLACK);
			if (continuacao)
				y -= exibirLinhaTexto(40F, y, "(continuação)", fonteNormal, TAMANHO_FONTE_CONTINUACAO);
			y -= 5F;
			return y0 - y;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public float exibirTexto(float x, float y, String texto, PDFont fonte, float fonteTamanho, int lineLength) {
		return exibirTexto(x, y, texto, fonte, fonteTamanho, lineLength, x, lineLength);
	}

	public float exibirTexto(float x, float y, String texto, PDFont fonte, float fonteTamanho, int lineLength,
			float firstLineX, int firstLineLength) {
		float result = 0;
		for (String linha : TextUtil.wordWrap(texto, lineLength, firstLineLength))
			result += exibirLinhaTexto(result == 0 ? firstLineX : x, y, linha, fonte, fonteTamanho, false);
		return result;
	}

	public float exibirLinhaTexto(float x, float y, String texto, PDFont fonte, float fonteTamanho) {
		return exibirLinhaTexto(x, y, texto, fonte, fonteTamanho, false);
	}

	public float exibirLinhaTexto(float x, float y, String texto, PDFont fonte, float fonteTamanho, boolean truncar) {
		try {
			conteudoPagina.beginText();
			conteudoPagina.setFont(fonte, fonteTamanho);
			conteudoPagina.newLineAtOffset(x, y);
			conteudoPagina.showText(truncar ? truncar(texto) : texto);
			conteudoPagina.endText();
			return 15F * (fonteTamanho / 9F);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void desenharLinha(float xI, float yI, float xF, float yF) {
		try {
			conteudoPagina.moveTo(xI, yI);
			conteudoPagina.lineTo(xF, yF);
			conteudoPagina.stroke();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void exibirRodape() {
		try {
			conteudoPagina.setNonStrokingColor(Color.BLUE);
			conteudoPagina.beginText();
			conteudoPagina.setFont(fonteNormal, TAMANHO_FONTE_RODAPE);
			conteudoPagina.newLineAtOffset(40F, 30F);
			conteudoPagina.showText("Versão de " + versao);
			conteudoPagina.newLineAtOffset(250F, 0F);
			conteudoPagina.showText(contato);
			conteudoPagina.endText();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected void closePage() {
		if (conteudoPagina == null)
			return;
		try {
			conteudoPagina.close();
			conteudoPagina = null;
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

	public void salvar(String nomeArquivo) {
		closePage();
		try {
			doc.save(nomeArquivo);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private String truncar(String texto) {
		if (texto.length() < 145)
			return texto;
		return texto.substring(0, 143) + "...";
	}

	private BufferedImage getLogotipo() throws IOException {
		return ImageIO.read(logoURL);
	}
}
