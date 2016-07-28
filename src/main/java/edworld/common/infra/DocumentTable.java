package edworld.common.infra;

import static edworld.common.infra.util.TextUtil.LINE_BREAK;

import java.util.ArrayList;
import java.util.List;

public class DocumentTable extends DocumentElement {
	private List<TableRow> rows = new ArrayList<>();

	public void addRow(TableRow row) {
		rows.add(row);
	}

	public List<TableRow> getRows() {
		return rows;
	}

	public String toHTML() {
		String result = "<table>" + LINE_BREAK;
		for (TableRow row : rows)
			result += row.toHTML() + LINE_BREAK;
		result += "</table>";
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		for (TableRow row : rows)
			result += row.toString() + LINE_BREAK;
		return result;
	}
}
