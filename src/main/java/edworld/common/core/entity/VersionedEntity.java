package edworld.common.core.entity;

import static edworld.common.infra.util.DateUtil.dateTimeToString;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import edworld.common.infra.boundary.TimestampCalendar;

public class VersionedEntity {
	@XmlElement
	protected String user;
	@XmlElement
	protected TimestampCalendar timestamp;
	@XmlElement
	protected Integer version;

	public String getUser() {
		return user;
	}

	public TimestampCalendar getTimestamp() {
		return timestamp;
	}

	public Integer getVersion() {
		return version;
	}

	@JsonIgnore
	public String getLastModified() {
		return "Vers." + version + ", " + user + ", " + dateTimeToString(timestamp.getTimestamp());
	}

	public void incrementVersion() {
		version = version == null ? 1 : version + 1;
	}

	public void setupVersioning(String user, TimestampCalendar timestamp, Integer version) {
		this.user = user;
		this.timestamp = timestamp;
		this.version = version;
	}

	public void unifyVersioning(VersionedEntity versionedEntity) {
		this.user = versionedEntity.getUser();
		this.timestamp = versionedEntity.getTimestamp();
		this.version = versionedEntity.getVersion();
	}
}