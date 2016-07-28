package edworld.common.infra.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DataUtilTest {
	@Test
	public void parsePeriodo() {
		String periodo = "21 DE ABRIL DE 1500";
		assertEquals("21/04/1500", DataUtil.dataToString(DataUtil.parseDataInicioPeriodo(periodo)));
		assertEquals("21/04/1500", DataUtil.dataToString(DataUtil.parseDataFimPeriodo(periodo)));
		periodo = "23 a 25 DE FEVEREIRO DE 2016";
		assertEquals("23/02/2016", DataUtil.dataToString(DataUtil.parseDataInicioPeriodo(periodo)));
		assertEquals("25/02/2016", DataUtil.dataToString(DataUtil.parseDataFimPeriodo(periodo)));
		periodo = "1º a 3 de março de 2016";
		assertEquals("01/03/2016", DataUtil.dataToString(DataUtil.parseDataInicioPeriodo(periodo)));
		assertEquals("03/03/2016", DataUtil.dataToString(DataUtil.parseDataFimPeriodo(periodo)));
		periodo = "31 de maio a 2 de junho de 2016";
		assertEquals("31/05/2016", DataUtil.dataToString(DataUtil.parseDataInicioPeriodo(periodo)));
		assertEquals("02/06/2016", DataUtil.dataToString(DataUtil.parseDataFimPeriodo(periodo)));
		periodo = "30 de dezembro de 2015 a 1º de janeiro de 2016";
		assertEquals("30/12/2015", DataUtil.dataToString(DataUtil.parseDataInicioPeriodo(periodo)));
		assertEquals("01/01/2016", DataUtil.dataToString(DataUtil.parseDataFimPeriodo(periodo)));
		periodo = "31 DE MAIO A 3 JUNHO DE 2016";
		assertEquals("31/05/2016", DataUtil.dataToString(DataUtil.parseDataInicioPeriodo(periodo)));
		assertEquals("03/06/2016", DataUtil.dataToString(DataUtil.parseDataFimPeriodo(periodo)));
	}
}
