package edworld.common.infra.boundary;

import java.util.Calendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import edworld.common.infra.util.DateUtil;

public class CalendarAdapter extends XmlAdapter<String, Calendar> {
	@Override
	public String marshal(Calendar dataHora) throws Exception {
		return DateUtil.dateToString(dataHora);
	}

	@Override
	public Calendar unmarshal(String dataHora) throws Exception {
		return DateUtil.parseDate(dataHora);
	}
}
