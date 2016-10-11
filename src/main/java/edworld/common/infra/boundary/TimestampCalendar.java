package edworld.common.infra.boundary;

import java.util.Calendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edworld.common.infra.util.DateUtil;

@XmlJavaTypeAdapter(TimestampAdapter.class)
public class TimestampCalendar {
	private Calendar timestamp;

	public TimestampCalendar(Calendar timestamp) {
		this.timestamp = timestamp;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TimestampCalendar)
			return getTimestamp().equals(((TimestampCalendar) other).getTimestamp());
		return super.equals(other);
	}

	@Override
	public String toString() {
		return DateUtil.timeStampToString(timestamp);
	}
}
