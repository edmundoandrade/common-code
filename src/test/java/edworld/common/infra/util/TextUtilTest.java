package edworld.common.infra.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class TextUtilTest {
	@Test
	public void formatar() {
		assertEquals("3091,572", TextUtil.formatar(new BigDecimal("3.091572E+3")));
		assertEquals("28/07/2016", TextUtil.formatar(DataUtil.data(28, 7, 2016)));
		assertEquals("Sim", TextUtil.formatar(true));
	}

	@Test
	public void fullTrim() {
		assertEquals("abc   def ghi", TextUtil.fullTrim(" \t abc \t def\nghi \t  "));
	}

	@Test
	public void pack() {
		assertEquals("|a1|b2|c3|", TextUtil.pack(ArrayUtils.toArray("a1", "b2", "c3")));
	}

	@Test
	public void unpack() {
		assertArrayEquals(ArrayUtils.toArray("x1", "y2", "z3"), TextUtil.unpack("|x1|y2|z3|"));
	}
}