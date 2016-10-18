package edworld.common.infra;

import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

public class ResourceGeneric extends ResourceWEB<byte[]> {
	public ResourceGeneric(final String url) {
		super(url, new Callable<byte[]>() {
			@Override
			public byte[] call() throws Exception {
				InputStream input = new URL(url).openStream();
				try {
					return IOUtils.toByteArray(input);
				} finally {
					input.close();
				}
			}
		});
	}

	@Override
	public void save(File target) {
		try {
			writeByteArrayToFile(target, getContents());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
