package edworld.common.infra;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PlanilhaEletronica {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public PlanilhaEletronica(InputStream stream) throws IOException {
		workbook = new XSSFWorkbook(stream);
		sheet = workbook.getSheetAt(0);
	}

	public void setFolhaTrabalho(String nome) {
		sheet = workbook.getSheet(nome);
	}

	public String celula(int linha, int coluna) {
		XSSFRow row = sheet.getRow(linha - 1);
		if (row == null)
			return null;
		XSSFCell col = row.getCell(coluna - 1);
		if (col == null)
			return null;
		return col.toString().trim();
	}

	public String celula(int linha, int coluna, String valorDefault) {
		String result = celula(linha, coluna);
		return result == null ? valorDefault : result;
	}
}
