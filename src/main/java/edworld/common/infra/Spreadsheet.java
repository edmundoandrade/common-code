package edworld.common.infra;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Spreadsheet {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public Spreadsheet(InputStream stream) throws IOException {
		workbook = new XSSFWorkbook(stream);
		sheet = workbook.getSheetAt(0);
	}

	public void setCurrentSheet(String name) {
		sheet = workbook.getSheet(name);
	}

	public String cell(int rowNumber, int columnNumber) {
		XSSFRow row = sheet.getRow(rowNumber - 1);
		if (row == null)
			return null;
		XSSFCell col = row.getCell(columnNumber - 1);
		if (col == null)
			return null;
		return col.toString().trim();
	}

	public String cell(int rowNumber, int columnNumber, String defaultValue) {
		String result = cell(rowNumber, columnNumber);
		return result == null ? defaultValue : result;
	}
}
