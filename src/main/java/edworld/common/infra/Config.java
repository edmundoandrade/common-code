package edworld.common.infra;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	public static String getEncoding() {
		return "UTF-8";
	}

	private Properties prop;

	public Config(String fileName) {
		prop = new Properties();
		try {
			prop.load(new FileReader(fileName));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Config(InputStream stream) {
		prop = new Properties();
		try {
			prop.load(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getProp(String key) {
		return getProp(key, null);
	}

	public String getProp(String key, String defaultValue) {
		String value = prop.getProperty(key);
		return value == null ? defaultValue : value;
	}

	public boolean propContainsValue(String key, String value, String regexSeparator) {
		for (String admin : getProp(key, "").split(regexSeparator))
			if (value.equalsIgnoreCase(admin.trim()))
				return true;
		return false;
	}
}
