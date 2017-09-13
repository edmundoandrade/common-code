package edworld.common.infra.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateUtilTest {
	@Test
	public void parseDateRange() {
		String periodo = "21 DE ABRIL DE 1500";
		assertEquals("21/04/1500", DateUtil.dateToString(DateUtil.parseStartDate(periodo)));
		assertEquals("21/04/1500", DateUtil.dateToString(DateUtil.parseEndDate(periodo)));
		periodo = "23 a 25 DE FEVEREIRO DE 2016";
		assertEquals("23/02/2016", DateUtil.dateToString(DateUtil.parseStartDate(periodo)));
		assertEquals("25/02/2016", DateUtil.dateToString(DateUtil.parseEndDate(periodo)));
		periodo = "1º a 3 de março de 2016";
		assertEquals("01/03/2016", DateUtil.dateToString(DateUtil.parseStartDate(periodo)));
		assertEquals("03/03/2016", DateUtil.dateToString(DateUtil.parseEndDate(periodo)));
		periodo = "31 de maio a 2 de junho de 2016";
		assertEquals("31/05/2016", DateUtil.dateToString(DateUtil.parseStartDate(periodo)));
		assertEquals("02/06/2016", DateUtil.dateToString(DateUtil.parseEndDate(periodo)));
		periodo = "30 de dezembro de 2015 a 1º de janeiro de 2016";
		assertEquals("30/12/2015", DateUtil.dateToString(DateUtil.parseStartDate(periodo)));
		assertEquals("01/01/2016", DateUtil.dateToString(DateUtil.parseEndDate(periodo)));
		periodo = "31 DE MAIO A 3 JUNHO DE 2016";
		assertEquals("31/05/2016", DateUtil.dateToString(DateUtil.parseStartDate(periodo)));
		assertEquals("03/06/2016", DateUtil.dateToString(DateUtil.parseEndDate(periodo)));
		periodo = "8 de dezembro 2017";
		assertEquals("08/12/2017", DateUtil.dateToString(DateUtil.parseStartDate(periodo)));
		assertEquals("08/12/2017", DateUtil.dateToString(DateUtil.parseEndDate(periodo)));
	}

	@Test
	public void parseTime() {
		assertEquals("08:00", DateUtil.parseTime("8h"));
		assertEquals("19:00", DateUtil.parseTime("19"));
		assertEquals("12:03", DateUtil.parseTime("12h3"));
		assertEquals("07:15", DateUtil.parseTime("7:15"));
		assertEquals("09:02", DateUtil.parseTime("9h2"));
		assertEquals("09:22", DateUtil.parseTime("9h22"));
		assertEquals("23:17", DateUtil.parseTime("23h17"));
	}
}
