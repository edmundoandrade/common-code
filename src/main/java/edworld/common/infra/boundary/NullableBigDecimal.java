package edworld.common.infra.boundary;

import java.math.BigDecimal;
import java.sql.Types;

public class NullableBigDecimal extends Nullable {
	public NullableBigDecimal(BigDecimal value) {
		super(value);
	}

	@Override
	public Class<?> getClazz() {
		return BigDecimal.class;
	}

	@Override
	public int sqlType() {
		return Types.NUMERIC;
	}
}
