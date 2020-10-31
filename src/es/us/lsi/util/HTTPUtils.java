package es.us.lsi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;



/**
 * @author reinaqu
 * Clase de utilidad para realizar peticiones http.
 */
public class HTTPUtils {
	static final Charset ENCODING = StandardCharsets.UTF_8;

	public static String httpGet(String uriStr) throws URISyntaxException, IOException {

		StringBuilder sb = null;
		URL url = (new URI(uriStr)).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), ENCODING));
		sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();

		return sb.toString();
	}

}
