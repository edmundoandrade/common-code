package edworld.common.infra.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class TextUtilTest {
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
