package edworld.common.infra.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HTMLUtilTest {
	@Test
	public void textHTML() {
		assertEquals("DOU de 10/09/2015", HTMLUtil.textHTML("<td><span>DOU de </span><span>10/09/2015</span> </td>"));
		assertEquals("Sessão Solene", HTMLUtil.textHTML("<b><span>Sessão Solene<o:p></o:p></span></b>"));
		assertEquals("Debater \"TEMA\" já.", HTMLUtil.textHTML("<p>&nbsp;Debater <b>\"TEMA\"</b><em> já.</em></p>"));
	}
}
