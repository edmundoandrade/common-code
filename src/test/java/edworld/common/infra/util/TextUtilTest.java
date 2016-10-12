package edworld.common.infra.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class TextUtilTest {
	@Test
	public void standard() {
		assertEquals("casa_farinha", TextUtil.standard("Casa da Farinha"));
		assertEquals("tabela_valores_media_-_cento", TextUtil.standard("Tabela de VALORES/media - por cento"));
		assertEquals("preco_dolares_reais", TextUtil.standard("Preço em Dólares ou Reais"));
		assertEquals("casa", TextUtil.standard("Casa dos"));
		assertEquals("silva", TextUtil.standard("DA SILVA"));
	}

	@Test
	public void format() {
		assertEquals("3091,572", TextUtil.format(new BigDecimal("3.091572E+3")));
		assertEquals("28/07/2016", TextUtil.format(DateUtil.date(28, 7, 2016)));
		assertEquals("Sim", TextUtil.format(true));
	}

	@Test
	public void fullTrim() {
		assertEquals("abc   def ghi", TextUtil.fullTrim(" \t abc \t def\nghi \t  "));
		assertEquals("X Y", TextUtil.fullTrim("X\u00A0Y"));
	}

	@Test
	public void pack() {
		assertEquals("|a1|b2|c3|", TextUtil.pack(ArrayUtils.toArray("a1", "b2", "c3")));
	}

	@Test
	public void unpack() {
		assertArrayEquals(ArrayUtils.toArray("x1", "y2", "z3"), TextUtil.unpack("|x1|y2|z3|"));
	}

	@Test
	public void wordWrap() {
		assertEquals("g", TextUtil.wordWrap("abcdefg", 3)[2]);
		assertEquals("fg", TextUtil.wordWrap("abcdefg", 2, 3)[2]);
	}

	@Test
	public void normalizePhonemes() {
		assertEquals("atur_gasia", TextUtil.normalizePhonemes("Arthur Garcia"));
		assertEquals("atur_xnidir_xavis", TextUtil.normalizePhonemes("Arthur Schneider Chaves"));
		assertEquals("liunadu_acinu_sifirinu", TextUtil.normalizePhonemes("Leonardo Aquino Zefferino"));
		assertEquals("luis_susa_lagi", TextUtil.normalizePhonemes("Luiz Souza da Laje"));
		assertEquals("uginsia", TextUtil.normalizePhonemes("DA URGÊNCIA"));
	}
}
