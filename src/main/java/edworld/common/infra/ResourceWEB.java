package edworld.common.infra;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import edworld.common.infra.util.HTMLUtil;

/**
 * To prevent permission errors (401), credentials must be provided, by calling:
 * 
 * Authenticator.setDefault(new Authenticator() { protected
 * PasswordAuthentication getPasswordAuthentication() { return new
 * PasswordAuthentication(user, password); } });
 */
public abstract class ResourceWEB<T> {
	protected static final int MAX_NUM_TRIES = 5;
	private String url;
	private Callable<T> action;
	private T contents;
	private Map<String, Integer> tries = new HashMap<String, Integer>();

	private static CloseableHttpClient singletonHttpClient;

	public static void openSingletonHttpClient() {
		closeSingletonHttpClient();
		singletonHttpClient = HttpClients.createDefault();
	}

	public static void closeSingletonHttpClient() {
		try {
			if (singletonHttpClient != null) {
				singletonHttpClient.close();
				singletonHttpClient = null;
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected static CloseableHttpClient openHttpClient() {
		if (singletonHttpClient == null)
			return HttpClients.createDefault();
		return singletonHttpClient;
	}

	protected static void closeHttpClient(CloseableHttpClient httpclient) throws IOException {
		if (httpclient == singletonHttpClient)
			return;
		httpclient.close();
	}

	protected static HttpRequestBase httpGet(String url) {
		return configProfix(new HttpGet(url));
	}

	protected static HttpRequestBase httpPost(String url, List<NameValuePair> urlParameters) {
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		return configProfix(post);
	}

	private static HttpRequestBase configProfix(HttpRequestBase request) {
		String httpProxyHost = System.getProperty("http.proxyHost", "");
		int httpProxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "80"));
		if (!httpProxyHost.isEmpty())
			request.setConfig(
					RequestConfig.custom().setProxy(new HttpHost(httpProxyHost, httpProxyPort, "http")).build());
		return request;
	}

	protected static String errorDetail(CloseableHttpResponse response, String encoding) throws IOException {
		InputStream input = response.getEntity().getContent();
		try {
			String message = IOUtils.toString(input, encoding);
			if (message.contains("</html>"))
				return "[code: " + response.getStatusLine().getStatusCode() + "] " + HTMLUtil.textHTML(message);
			return "[" + response.getStatusLine().getStatusCode() + "] "
					+ HTMLUtil.unescapeHTML(message).replaceAll("(?s)\\s+", " ").trim();
		} finally {
			input.close();
		}
	}

	protected static URLConnection getURLConnection(String url) throws IOException {
		URLConnection connection = new URL(url).openConnection();
		connection.setUseCaches(false);
		return connection;
	}

	protected static void closeURLConnection(URLConnection connection) {
		if (connection instanceof HttpURLConnection)
			((HttpURLConnection) connection).disconnect();
	}

	protected ResourceWEB(String url, Callable<T> action) {
		this.url = url;
		this.action = action;
	}

	public String getUrl() {
		return url;
	}

	public T getContents() {
		if (contents == null)
			contents = tryActionForURL(url);
		return contents;
	}

	public abstract void save(File destino);

	protected T tryActionForURL(String url) {
		try {
			if (tries.containsKey(url))
				Thread.sleep(3000 + (MAX_NUM_TRIES - tries.get(url)) * 2000);
			return action.call();
		} catch (Exception e) {
			if (!tries.containsKey(url)) {
				int pos = e.getMessage().indexOf("code:");
				if (pos >= 0)
					System.out.println("[" + e.getMessage().substring(pos, pos + 9) + "] " + url);
				else
					System.out.println(url + " [" + e.getMessage() + "]");
			}
			if (e.getMessage().contains("code: 403") || e.getMessage().contains("code: 500")
					|| e.getMessage().contains("code: 502") || e.getMessage().contains("code: 503")) {
				if (tries.containsKey(url))
					tries.put(url, tries.get(url) - 1);
				else
					tries.put(url, MAX_NUM_TRIES);
				if (tries.get(url) > 0)
					return tryActionForURL(url);
			}
			throw new IllegalArgumentException(e);
		}
	}
}