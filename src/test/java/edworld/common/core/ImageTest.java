package edworld.common.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImageTest {
	@Test
	public void toHTML() {
		Image image = new Image("http://host/image.jpeg", "Alternative text", 200, 180);
		assertEquals(
				"<img src=\"http://host/image.jpeg\" class=\"img-responsive\" alt=\"Alternative text\" width=\"200\" height=\"180\">",
				image.toHTML());
	}
}
