package edworld.common.infra.boundary;

import java.sql.Types;

public class NullableInteger extends Nullable {
	public NullableInteger(Integer value) {
		super(value);
	}

	@Override
	public Class<?> getClazz() {
		return Integer.class;
	}

	@Override
	public int sqlType() {
		return Types.INTEGER;
	}
}
