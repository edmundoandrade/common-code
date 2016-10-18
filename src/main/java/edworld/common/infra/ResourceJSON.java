package edworld.common.infra;

import java.io.File;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.codehaus.jackson.type.TypeReference;

import edworld.common.infra.util.JSONUtil;

public class ResourceJSON extends ResourceWEB<Map<String, String>> {
	public ResourceJSON(final String url) {
		super(url, new Callable<Map<String, String>>() {
			@Override
			public Map<String, String> call() throws Exception {
				URLConnection connection = getURLConnection(url);
				try {
					return JSONUtil.fromStream(connection.getInputStream(),
							new TypeReference<HashMap<String, String>>() {
							});
				} finally {
					closeURLConnection(connection);
				}
			}
		});
	}

	@Override
	public void save(File target) {
		JSONUtil.toFile(getContents(), target);
	}
}
