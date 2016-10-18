package edworld.common.infra;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

public class ResourceHTML extends ResourceWEB<String> {
	public ResourceHTML(String url) {
		this(url, Config.getEncoding());
	}

	public ResourceHTML(final String url, final String encoding) {
		super(url, new Callable<String>() {
			@Override
			public String call() throws Exception {
				URLConnection connection = getURLConnection(url);
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
	public void save(File target) {
		try {
			writeStringToFile(target, getContents(), Config.getEncoding());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
