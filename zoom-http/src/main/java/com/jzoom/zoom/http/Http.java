package com.jzoom.zoom.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.common.json.JSON;

/**
 * 本类用于对接微信支付等接口 提供GET/POST方法
 * 
 * @author jzoom
 *
 */
public class Http {

	public static final String Content_Type = "Content-Type";

	public static final String APPLICATION_JSON = "application/json; charset=utf-8";
	public static final String APPLICATION_FORM = "application/x-www-form-urlencoded; charset=utf-8";
	public static final String APPLICATION_MULTIPART = "multipart/form-data; boundary=";

	public static final String X_Forward_For = "X-Forward-For";

	/**
	 * 
	 * @author jzoom
	 *
	 */
	public static class Response {

		final HttpURLConnection connection;

		public Response(HttpURLConnection connection) {
			this.connection = connection;
		}

		public String getHeader(String key) {
			return this.connection.getHeaderField(key);
		}

		public <T> T json(Class<T> classOfT) throws IOException {
			InputStream is = null;
			try {
				is = connection.getInputStream();
				return JSON.parse(is, classOfT);
			} finally {
				Io.close(is);
				connection.disconnect();
			}
		}

		@SuppressWarnings("unchecked")
		public Map<String, Object> jsonMap() throws IOException {
			return json(Map.class);
		}

		public int getStatusCode() throws IOException {
			return connection.getResponseCode();
		}

		public String getUrl() {
			
			return connection.getURL().toString();
		}

	}

	public static interface HttpCallback {
		/**
		 * 可以设置一些额外头部
		 * 
		 * @param connection
		 */
		void preHandle(HttpURLConnection connection) throws IOException;

		/**
		 * 可以返回一个新的response
		 * 
		 * @param connection
		 * @param response
		 */
		Response afterHandle(HttpURLConnection connection, Response response) throws IOException;
	}

	public static interface HttpHandler {
		void doOutput(HttpURLConnection connection) throws IOException;
	}
	public static class Client {

		private String baseUrl;
		private int readTimeout = 10000;
		private int connectTimeout = 5000;
		private Map<String, String> headers;
		private HttpCallback callback;

		Client() {
			headers = new HashMap<String, String>();
		}

		public Client addDefaultHeader(String key, String value) {
			headers.put(key, value);
			return this;
		}

		public Client setCallback(HttpCallback callback) {
			this.callback = callback;
			return this;
		}

		public Client setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public Response get(String url) throws IOException {
			return execute(url, "GET",null);
		}
		public Response post(String url, String content) throws IOException {
			return post(url,  content.getBytes("utf-8"));
		}
		public Response post(String url,HttpHandler handler) throws IOException {
			return execute(url, "POST", handler);
		}
		public Response postJson(String url,final Object data) throws IOException {
			return execute(url, "POST", new HttpHandler() {
				
				@Override
				public void doOutput(HttpURLConnection connection) throws IOException {
					JSON.write(connection.getOutputStream(), data);
				}
			});
		}
		public Response post(String url, final byte[] bytes) throws IOException {
			return execute(url,"POST", new HttpHandler() {

				@Override
				public void doOutput(HttpURLConnection connection) throws IOException {
					Io.writeAndClose(connection.getOutputStream(),bytes);
				}
			});
		}
		private String getUrl(String url) {
			return baseUrl == null ? url : baseUrl + url;
		}

		private HttpURLConnection createConnection(String url) throws ConnectionException {
			try {
				HttpURLConnection connection = Http.createConnection(getUrl(url));
				connection.setConnectTimeout(connectTimeout);
				connection.setReadTimeout(readTimeout);
				return connection;
			} catch (IOException e) {
				throw new ConnectionException(e);
			}

		}


		private Response execute(String url, String method, HttpHandler handler) throws IOException {
			HttpURLConnection connection = null;
			final HttpCallback callback = this.callback;
			final Map<String, String> headers = this.headers;
			try {
				connection = createConnection(url);
				connection.setRequestMethod(method);
				// 设置默认头
				for (Entry<String, String> entry : headers.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
				if (callback != null) {
					callback.preHandle(connection);
				}
				if(handler!=null) {
					connection.setDoOutput(true);
					handler.doOutput(connection);
				}
				Response response = new Response(connection);
				if (callback != null) {
					response = callback.afterHandle(connection, response);
				}
				return response;
			} catch (IOException e) {
				if (connection != null) {
					connection.disconnect();
				}
				throw e;
			}

		}

		

	}

	public static class ConnectionException extends IOException {

		public ConnectionException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public ConnectionException(String arg0, Throwable arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		public ConnectionException(String arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		public ConnectionException(Throwable arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 2654595220234736085L;

	}

	private static HttpURLConnection createConnection(String url) throws IOException {
		URL URL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
		if (connection instanceof HttpsURLConnection) {
			// https，需要特殊设置
			HttpsUtil.setConnections((HttpsURLConnection) connection);
		}
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(false);
		return connection;
	}

	public static Client newClient() {
		return new Client();
	}
	
	private static Client CLIENT = newClient();

	public static Response get( String url ) throws IOException {
		return CLIENT.get(url);
	}

}
