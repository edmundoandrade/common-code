package edworld.common.infra.boundary;

import java.sql.Types;
import java.util.Calendar;

public class NullableDate extends Nullable {
	public NullableDate(Calendar value) {
		super(value);
	}

	@Override
	public Class<?> getClazz() {
		return Calendar.class;
	}

	@Override
	public int sqlType() {
		return Types.DATE;
	}
}
