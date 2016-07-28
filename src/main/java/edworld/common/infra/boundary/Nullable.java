package edworld.common.infra.boundary;

public abstract class Nullable {
	protected Object value;

	public Nullable(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public boolean isNull() {
		return value == null;
	}

	public abstract Class<?> getClazz();

	public abstract int sqlType();
}
