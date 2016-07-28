package edworld.common.infra.boundary;

import java.sql.Types;

import edworld.common.infra.util.TextUtil;

public class NullableStringSequence extends Nullable {
	public NullableStringSequence(String[] value) {
		super(value);
	}

	@Override
	public Object getValue() {
		return TextUtil.pack((String[]) value);
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
