package edworld.common.infra;

import static edworld.common.infra.util.TextUtil.LINE_BREAK;

import java.util.ArrayList;
import java.util.List;

public class TableRow extends DocumentElement {
	private List<TableCell> cells = new ArrayList<>();

	public void addCell(TableCell cell) {
		cells.add(cell);
	}

	public List<TableCell> getCells() {
		return cells;
	}

	public String toHTML() {
		String result = "<tr>" + LINE_BREAK;
		for (TableCell cell : getCells())
			result += cell.toHTML() + LINE_BREAK;
		result += "</tr>";
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		String prefix = "";
		for (TableCell cell : getCells()) {
			result += prefix + cell.toString();
			prefix = " | ";
		}
		return result;
	}
}
