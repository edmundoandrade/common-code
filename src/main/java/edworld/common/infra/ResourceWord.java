package edworld.common.infra;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class ResourceWord {
	private List<DocumentElement> elements = new ArrayList<>();

	public ResourceWord(InputStream stream) {
		try {
			InputStream input = new BufferedInputStream(stream);
			try {
				if (POIXMLDocument.hasOOXMLHeader(input))
					loadDOCX(input);
				else
					loadDOC(input);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private void loadDOCX(InputStream input) throws IOException {
		XWPFDocument doc = new XWPFDocument(input);
		try {
			for (IBodyElement element : doc.getBodyElements())
				if (element instanceof XWPFParagraph) {
					XWPFParagraph par = ((XWPFParagraph) element);
					elements.add(new DocumentParagraph(par.getText(), par.getNumFmt()));
				} else if (element instanceof XWPFTable) {
					DocumentTable table = new DocumentTable();
					elements.add(table);
					for (XWPFTableRow row : ((XWPFTable) element).getRows()) {
						TableRow currentRow = new TableRow();
						table.addRow(currentRow);
						for (XWPFTableCell cell : row.getTableCells()) {
							edworld.common.infra.TableCell currentCell = new edworld.common.infra.TableCell();
							for (XWPFParagraph par : cell.getParagraphs())
								currentCell.addParagraph(new DocumentParagraph(par.getText(), par.getNumFmt()));
							currentRow.addCell(currentCell);
						}
					}
				}
		} finally {
			doc.close();
		}
	}

	private void loadDOC(InputStream input) throws IOException {
		HWPFDocument doc = new HWPFDocument(input);
		Range range = doc.getRange();
		boolean tableProcessed = false;
		for (int i = 0; i < range.numParagraphs(); i++) {
			Paragraph par = range.getParagraph(i);
			if (!par.isInTable()) {
				elements.add(new DocumentParagraph(par.text(), numeringFormat(par)));
				tableProcessed = false;
			} else if (!tableProcessed) {
				DocumentTable table = new DocumentTable();
				elements.add(table);
				Table tbl = range.getTable(par);
				for (int rowIdx = 0; rowIdx < tbl.numRows(); rowIdx++) {
					TableRow currentRow = new TableRow();
					table.addRow(currentRow);
					org.apache.poi.hwpf.usermodel.TableRow row = tbl.getRow(rowIdx);
					for (int colIdx = 0; colIdx < row.numCells(); colIdx++) {
						edworld.common.infra.TableCell currentCell = new edworld.common.infra.TableCell();
						TableCell cell = row.getCell(colIdx);
						for (int parIdx = 0; parIdx < cell.numParagraphs(); parIdx++) {
							Paragraph paragraph = cell.getParagraph(parIdx);
							currentCell.addParagraph(new DocumentParagraph(text(paragraph), numeringFormat(paragraph)));
						}
						currentRow.addCell(currentCell);
					}
				}
				tableProcessed = true;
			}
		}
	}

	private String numeringFormat(Paragraph par) {
		if (par.isInList() && par.getIlfo() == 8)
			return "ol" + par.getIlvl();
		if (par.isInList() && par.getIlfo() == 11)
			return "ul" + par.getIlvl();
		return null;
	}

	private String text(Paragraph paragraph) {
		return paragraph.text().replace("\u0007", "");
	}

	public List<DocumentElement> getElements() {
		return elements;
	}
}
