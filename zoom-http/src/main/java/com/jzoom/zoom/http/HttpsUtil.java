package com.jzoom.zoom.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.jzoom.zoom.common.io.Io;

class HttpsUtil {
	private static final SSLContext sc;
	private static final HostnameVerifier HOSTNAME_VERIFIER = new TrustAnyHostnameVerifier();
	static{
		try {
			sc = SSLContext.getInstance("SSL", "SunJSSE");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		
	}
	
	
	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
	
	public static void setConnections(HttpsURLConnection httpConnection){
		httpConnection.setSSLSocketFactory(sc.getSocketFactory());
		httpConnection.setHostnameVerifier(HOSTNAME_VERIFIER);
	}

}
