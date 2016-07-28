package edworld.common.infra.boundary;

import java.sql.Types;

public class NullableEnum extends Nullable {
	public NullableEnum(Enum<?> value) {
		super(value);
	}

	@Override
	public Object getValue() {
		return value.toString();
	}

	@Override
	public Class<?> getClazz() {
		return Enum.class;
	}

	@Override
	public int sqlType() {
		return Types.VARCHAR;
	}
}
