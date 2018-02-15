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

	@Test
	public void encodeURLParam() {
		assertEquals("Teste+com+acentua%C3%A7%C3%A3o+gr%C3%A1fica+e%2Fou+pontua%C3%A7%C3%A3o.",
				HTMLUtil.encodeURLParam("Teste com acentuação gráfica e/ou pontuação."));
	}

	@Test
	public void decodeURLParam() {
		assertEquals("Teste com acentuação gráfica e/ou pontuação.",
				HTMLUtil.decodeURLParam("Teste+com+acentua%C3%A7%C3%A3o+gr%C3%A1fica+e%2Fou+pontua%C3%A7%C3%A3o."));
	}
}
