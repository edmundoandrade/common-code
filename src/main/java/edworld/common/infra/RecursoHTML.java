package edworld.common.infra;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

public class RecursoHTML extends RecursoWEB<String> {
	public RecursoHTML(String enderecoURL) {
		this(enderecoURL, Config.getEncoding());
	}

	public RecursoHTML(final String enderecoURL, final String encoding) {
		super(enderecoURL, new Callable<String>() {
			@Override
			public String call() throws Exception {
				URLConnection connection = getURLConnection(enderecoURL);
				try {
					InputStream input = connection.getInputStream();
					try {
						return IOUtils.toString(input, encoding);
					} finally {
						input.close();
					}
				} finally {
					closeURLConnection(connection);
				}
			}
		});
	}

	@Override
	public void arquivar(File destino) {
		try {
			writeStringToFile(destino, getConteudo(), Config.getEncoding());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
