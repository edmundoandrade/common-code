package edworld.common.infra.boundary;

import java.sql.Types;

public class NullableBoolean extends Nullable {
	public NullableBoolean(Boolean value) {
		super(value);
	}

	@Override
	public Class<?> getClazz() {
		return Boolean.class;
	}

	@Override
	public int sqlType() {
		return Types.BOOLEAN;
	}
}
