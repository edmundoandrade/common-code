package edworld.common.infra.boundary;

import java.util.Calendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import edworld.common.infra.util.DateUtil;

public class TimestampAdapter extends XmlAdapter<String, Calendar> {
	@Override
	public String marshal(Calendar timeStamp) throws Exception {
		return DateUtil.timeStampToString(timeStamp);
	}

	@Override
	public Calendar unmarshal(String timeStamp) throws Exception {
		return DateUtil.parseTimeStamp(timeStamp);
	}
}
