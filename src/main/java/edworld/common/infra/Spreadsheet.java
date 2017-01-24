package edworld.common.infra;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Spreadsheet {
	public enum SpreadsheetFormat {
		XLS, XLSX
	}

	private Workbook workbook;
	private Sheet sheet;

	public Spreadsheet(InputStream stream) throws IOException {
		this(stream, SpreadsheetFormat.XLSX);
	}

	public Spreadsheet(InputStream stream, SpreadsheetFormat format) throws IOException {
		if (format.equals(SpreadsheetFormat.XLS))
			workbook = new HSSFWorkbook(stream);
		else
			workbook = new XSSFWorkbook(stream);
		sheet = workbook.getSheetAt(0);
	}

	public void setCurrentSheet(String name) {
		sheet = workbook.getSheet(name);
	}

	public String cell(int rowNumber, int columnNumber) {
		Row row = sheet.getRow(rowNumber - 1);
		if (row == null)
			return null;
		Cell col = row.getCell(columnNumber - 1);
		if (col == null)
			return null;
		return col.toString().trim();
	}

	public String cell(int rowNumber, int columnNumber, String defaultValue) {
		String result = cell(rowNumber, columnNumber);
		return result == null ? defaultValue : result;
	}
}
