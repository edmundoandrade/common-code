package edworld.common.infra.boundary;

import java.sql.Types;

import org.apache.commons.codec.binary.Base64;

public class NullableByteSequence extends Nullable {
	public NullableByteSequence(byte[] value) {
		super(value);
	}

	@Override
	public Object getValue() {
		return Base64.encodeBase64String((byte[]) value);
	}

	@Override
	public Class<?> getClazz() {
		return String.class;
	}

	@Override
	public int sqlType() {
		return Types.VARCHAR;
	}
}
