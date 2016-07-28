package edworld.common.infra.boundary;

import java.sql.Types;

public class NullableTimestampCalendar extends Nullable {
	public NullableTimestampCalendar(TimestampCalendar value) {
		super(value);
	}

	@Override
	public Class<?> getClazz() {
		return TimestampCalendar.class;
	}

	@Override
	public int sqlType() {
		return Types.TIMESTAMP;
	}
}
