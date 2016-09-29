package edworld.common.infra.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import edworld.common.infra.Config;

public class HTTPUtil {
	public static String get(String uri, String token, String tokenType) {
		HttpGet httpget = new HttpGet(uri);
		httpget.addHeader("Authorization", tokenType + " " + token);
		try {
			CloseableHttpClient client = createHttpClient();
			try {
				HttpResponse response = client.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					try {
						return IOUtils.toString(instream, Config.getEncoding());
					} finally {
						instream.close();
					}
				}
			} finally {
				client.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return null;
	}

	public static String post(String uri, List<NameValuePair> params) {
		HttpPost httppost = new HttpPost(uri);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, Config.getEncoding()));
			CloseableHttpClient client = createHttpClient();
			try {
				HttpResponse response = client.execute(httppost);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					try {
						return IOUtils.toString(instream, Config.getEncoding());
					} finally {
						instream.close();
					}
				}
			} finally {
				client.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return null;
	}

	private static CloseableHttpClient createHttpClient() {
		return HttpClientBuilder.create().useSystemProperties().build();
	}
}
