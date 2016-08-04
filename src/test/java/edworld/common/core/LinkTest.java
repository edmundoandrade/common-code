package edworld.common.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LinkTest {
	@Test
	public void toHTML() {
		Link link = new Link("Page title", "http://host/page.html");
		assertEquals("<a href=\"http://host/page.html\">Page title</a>", link.toHTML());
	}
}
