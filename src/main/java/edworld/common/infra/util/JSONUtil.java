package edworld.common.infra.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.type.TypeReference;

public abstract class JSONUtil {
	private static ObjectMapper objectMapper;

	public static String toString(Object object) {
		StringWriter writer = new StringWriter();
		try {
			objectMapper().writeValue(writer, object);
			String result = writer.toString();
			writer.close();
			return result;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static void toFile(Object object, File targetFile) {
		targetFile.getParentFile().mkdirs();
		try {
			objectMapper().writeValue(targetFile, object);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Map<String, String> toMap(String jsonText) {
		return JSONUtil.fromString(jsonText, new TypeReference<Map<String, String>>() {
		});
	}

	public static <T> T fromString(String jsonText, TypeReference<T> typeRef) {
		try {
			return objectMapper().readValue(jsonText, typeRef);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T fromFile(File arquivo, TypeReference<T> typeRef) {
		try {
			return objectMapper().readValue(arquivo, typeRef);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T fromStream(InputStream input, TypeReference<T> typeRef) {
		try {
			try {
				return objectMapper().readValue(input, typeRef);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static ObjectMapper objectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper().disable(Feature.WRITE_DATES_AS_TIMESTAMPS).enable(Feature.INDENT_OUTPUT);
			objectMapper.setDateFormat(DateUtil.FORMAT_TIMESTAMP);
		}
		return objectMapper;
	}

	public static boolean areEquals(Object o1, Object o2) {
		return toString(o1).equals(toString(o2));
	}
}
