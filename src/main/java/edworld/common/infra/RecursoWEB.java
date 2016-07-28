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
public abstract class RecursoWEB<T> {
	protected static final int MAX_NUM_TENTATIVAS = 5;
	private String urlEndereco;
	private Callable<T> acao;
	private T conteudo;
	private Map<String, Integer> tentativas = new HashMap<String, Integer>();

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

	protected static HttpRequestBase httpGet(String enderecoURL) {
		return configProfix(new HttpGet(enderecoURL));
	}

	protected static HttpRequestBase httpPost(String enderecoURL, List<NameValuePair> urlParameters) {
		HttpPost post = new HttpPost(enderecoURL);
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

	protected static URLConnection getURLConnection(String enderecoURL) throws IOException {
		URLConnection connection = new URL(enderecoURL).openConnection();
		connection.setUseCaches(false);
		return connection;
	}

	protected static void closeURLConnection(URLConnection connection) {
		if (connection instanceof HttpURLConnection)
			((HttpURLConnection) connection).disconnect();
	}

	protected RecursoWEB(String urlEndereco, Callable<T> acao) {
		this.urlEndereco = urlEndereco;
		this.acao = acao;
	}

	public String getUrlEndereco() {
		return urlEndereco;
	}

	public T getConteudo() {
		if (conteudo == null)
			conteudo = processarURLComTentativas(urlEndereco);
		return conteudo;
	}

	public abstract void arquivar(File destino);

	protected T processarURLComTentativas(String url) {
		try {
			if (tentativas.containsKey(url))
				Thread.sleep(3000 + (MAX_NUM_TENTATIVAS - tentativas.get(url)) * 2000);
			return acao.call();
		} catch (Exception e) {
			if (!tentativas.containsKey(url)) {
				int pos = e.getMessage().indexOf("code:");
				if (pos >= 0)
					System.out.println("[" + e.getMessage().substring(pos, pos + 9) + "] " + url);
				else
					System.out.println(url + " [" + e.getMessage() + "]");
			}
			if (e.getMessage().contains("code: 403") || e.getMessage().contains("code: 500")
					|| e.getMessage().contains("code: 502") || e.getMessage().contains("code: 503")) {
				if (tentativas.containsKey(url))
					tentativas.put(url, tentativas.get(url) - 1);
				else
					tentativas.put(url, MAX_NUM_TENTATIVAS);
				if (tentativas.get(url) > 0)
					return processarURLComTentativas(url);
			}
			throw new IllegalArgumentException(e);
		}
	}
}