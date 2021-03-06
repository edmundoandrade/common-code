package edworld.common.core.entity;

import static edworld.common.infra.util.DateUtil.FORMAT_DATE_TIME_ISO;
import static edworld.common.infra.util.DateUtil.dateToString;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import edworld.common.infra.boundary.TimestampCalendar;

public class VersionedEntity {
	@XmlElement
	protected String userId;
	@XmlElement
	protected TimestampCalendar timestamp;
	@XmlElement
	protected Integer version;

	public String getUserId() {
		return userId;
	}

	public TimestampCalendar getTimestamp() {
		return timestamp;
	}

	public Integer getVersion() {
		return version;
	}

	@JsonIgnore
	public String getLastModified() {
		return "V." + version + ", " + userId + ", " + dateToString(timestamp.getTimestamp(), FORMAT_DATE_TIME_ISO);
	}

	public void incrementVersion() {
		version = version == null ? 1 : version + 1;
	}

	public void setupVersioning(String userId, TimestampCalendar timestamp, Integer version) {
		this.userId = userId;
		this.timestamp = timestamp;
		this.version = version;
	}

	public void unifyVersioning(VersionedEntity versionedEntity) {
		this.userId = versionedEntity.getUserId();
		this.timestamp = versionedEntity.getTimestamp();
		this.version = versionedEntity.getVersion();
	}
}
