package edworld.common.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TreeNode<T> {
	public static <N extends TreeNodeKeys> TreeNode<N> createFromKeys(String rootKey, Collection<N> keysCollection) {
		TreeNode<N> root = new TreeNode<>(rootKey, null);
		for (N item : keysCollection) {
			TreeNode<N> lastNode = root;
			for (int i = 0; i < item.getKeys().length; i++)
				if (i == item.getKeys().length - 1)
					lastNode.putChild(new TreeNode<>(item.getKeys()[i], item));
				else
					lastNode = lastNode.putChild(new TreeNode<N>(item.getKeys()[i], null));
		}
		return root;
	}

	private String key;
	private T item;
	private List<TreeNode<T>> children = new ArrayList<>();

	public TreeNode(String key, T item) {
		this.key = key;
		this.item = item;
	}

	public String getKey() {
		return key;
	}

	public T getItem() {
		return item;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public TreeNode<T> putChild(TreeNode<T> child) {
		int pos = getChildren().indexOf(child);
		if (pos >= 0)
			return getChildren().get(pos);
		getChildren().add(child);
		return child;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TreeNode<?>)
			return getKey().equals(((TreeNode<?>) obj).getKey());
		return super.equals(obj);
	}
}
