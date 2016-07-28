package edworld.common.infra.util;

import java.util.List;

public abstract class ListUtil {
	public static <T> T first(List<T> list) {
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	public static <T> T first(T[] array) {
		if (array == null || array.length == 0)
			return null;
		return array[0];
	}

	public static <T> T last(List<T> list) {
		if (list.isEmpty())
			return null;
		return list.get(list.size() - 1);
	}

	public static String partOf(String[] parts, int index) {
		if (index >= parts.length)
			return null;
		return parts[index].isEmpty() ? null : parts[index];
	}

	public static boolean contains(String[] array, String item) {
		if (array == null)
			return false;
		for (String itemCandidate : array)
			if (itemCandidate.equals(item))
				return true;
		return false;
	}
}
