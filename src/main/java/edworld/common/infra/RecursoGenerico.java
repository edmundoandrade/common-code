package edworld.common.infra;

import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

public class RecursoGenerico extends RecursoWEB<byte[]> {
	public RecursoGenerico(final String enderecoURL) {
		super(enderecoURL, new Callable<byte[]>() {
			@Override
			public byte[] call() throws Exception {
				InputStream input = new URL(enderecoURL).openStream();
				try {
					return IOUtils.toByteArray(input);
				} finally {
					input.close();
				}
			}
		});
	}

	@Override
	public void arquivar(File destino) {
		try {
			writeByteArrayToFile(destino, getConteudo());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
