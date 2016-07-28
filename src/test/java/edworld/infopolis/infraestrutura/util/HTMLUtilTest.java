package edworld.infopolis.infraestrutura.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edworld.common.infra.util.HTMLUtil;

public class HTMLUtilTest {
	@Test
	public void textHTML() {
		assertEquals("DOU de 10/09/2015", HTMLUtil.textHTML("<td><span>DOU de </span> <span>10/09/2015</span> </td>"));
	}
}
