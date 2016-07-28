package edworld.common.infra;

import java.io.File;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.codehaus.jackson.type.TypeReference;

import edworld.common.infra.util.JSONUtil;

public class RecursoJSON extends RecursoWEB<Map<String, String>> {
	public RecursoJSON(final String enderecoURL) {
		super(enderecoURL, new Callable<Map<String, String>>() {
			@Override
			public Map<String, String> call() throws Exception {
				URLConnection connection = getURLConnection(enderecoURL);
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
	public void arquivar(File destino) {
		JSONUtil.toFile(getConteudo(), destino);
	}
}
