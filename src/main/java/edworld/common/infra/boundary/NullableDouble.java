package edworld.common.infra.boundary;

import java.sql.Types;

public class NullableDouble extends Nullable {
	public NullableDouble(Double value) {
		super(value);
	}

	@Override
	public Class<?> getClazz() {
		return Double.class;
	}

	@Override
	public int sqlType() {
		return Types.DOUBLE;
	}
}
