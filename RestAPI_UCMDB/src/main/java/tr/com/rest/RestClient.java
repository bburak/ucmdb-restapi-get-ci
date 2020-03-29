package tr.com.rest;

import java.io.IOException;
import java.nio.file.attribute.AclEntry.Builder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Class RestClient communicating with UCMDB REST API.
 * 
 * The RestClient class provides methods to send http requests to the API and to
 * receive the response.
 * 
 *
 */
public class RestClient {

	private HttpClient client;
	private HashMap<String, String> headers;
	private String baseURL;

	/**
	 * Constructor instantiates http client and headers, sets BaseURL for API
	 * connection
	 * 
	 * @param baseURL Base URL of UCMDB REST API
	 * @param verify  verify SSL verification enabled/disabled
	 */
	public RestClient(String baseURL, Boolean verify)
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		headers = new HashMap<String, String>();
		this.baseURL = baseURL;
		// create basic http client if ssl verification is enabled
		if (verify) {
			client = HttpClientBuilder.create().build();
		} else {
			// create SSL connection Socket Factory allow self-signed certificates
			// create client with custom settings afterwards
			SSLConnectionSocketFactory sslsf;
			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();

			sslsf = new SSLConnectionSocketFactory(sslcontext, null, null, new NoopHostnameVerifier());
			client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		}
	}

	public void authenticate(String username, String password) throws IOException, HttpException {

		String url = baseURL + "/authenticate";

		// adminUser has the integration access.
		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("password", password);
		json.put("clientContext", 1);

		// format JSONObject to StringEntity to be added to http request
		StringEntity requestEntity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);

		JSONObject result = sendPostRequest(url, requestEntity);

		// get token from response and set to global headers
		String token = result.get("token").toString();
		headers.put("Authorization", "Bearer " + token);
		System.out.println("Token: " + token);
	}

	/**
	 * Execute TQL from UCMDB by TQL name
	 * 
	 * The excecuteTQL function expects a TQL name parameter to execute TQL from
	 * UCMDB by its name and receive the TQL results. For sending the http request
	 * the helper function sendPostRequest() is used. If no exception occurred
	 * during http request, the function returns the TQL results received from
	 * sendPostRequest().
	 * 
	 * @param tqlName Name of TQL to be executed
	 * @return Result of given TQL as JSONObject. Result is separated in CIs and
	 *         Relationships.
	 * @throws IOException   during http request (by sendPostRequest() function).
	 *                       Not cached to allow handling in controller class.
	 * @throws HttpException
	 */
	JSONObject executeTQL(String tqlName) throws IOException, HttpException {

		String url = baseURL + "/topology";

		StringEntity requestEntity = new StringEntity(tqlName, ContentType.APPLICATION_JSON);
		return sendPostRequest(url, requestEntity);
	}

	public JSONObject getCI(String ciId) throws IOException, HttpException {
		String url = String.valueOf(this.baseURL) + "/dataModel/ci/" + ciId;
		return sendGetRequest(url);
	}

	/**
	 * Send Get requests to API
	 * 
	 * Send http GET request to the given url. Therefore, new HttpGet object from
	 * http-client is created and needed http Headers are added to the object. After
	 * executing the request neither an exception has been thrown in case of any
	 * failures (e.g. target server down) or a response object has been sent. If the
	 * response status is 'OK', result is returned. Otherwise, IOException is thrown
	 * (e.g. Unauthorized, Bad Request etc.).
	 * 
	 * @param url URL of API to connect to
	 * @return Result of http request, if no error occurred during request
	 * @throws IOException   thrown when target server is not reachable
	 * @throws HttpException
	 */
	private JSONObject sendGetRequest(String url) throws IOException, HttpException {

		HttpGet request = new HttpGet(url);
		for (String key : headers.keySet()) {
			request.addHeader(key, headers.get(key));
		}

		HttpResponse response = client.execute(request);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new HttpException(String.valueOf(statusCode));
		}

		// Read the contents of an entity and return it as a String.
		String content = EntityUtils.toString(response.getEntity());

		return (JSONObject) JSONValue.parse(content);
	}

	/**
	 * Send Post requests to API
	 * 
	 * Send http POST request to the given url. Therefore, new HttpPost object from
	 * http-client is created and needed http Headers are added to the object.
	 * Additionally, post parameters are added to the request object as well to post
	 * data to the API. After executing the request neither an exception has been
	 * thrown in case of any failures (e.g. target server down) or a response object
	 * has been sent. If the response status is 'OK', result is returned. Otherwise,
	 * IOException is thrown (e.g. Unauthorized, Bad Request etc.).
	 * 
	 * @param url      URL of API to connect to
	 * @param postData body entity
	 * @return Result of http request, if no error occurred during request
	 * @throws IOException   thrown when target server is not reachable
	 * @throws HttpException
	 */
	private JSONObject sendPostRequest(String url, StringEntity postData) throws IOException, HttpException {

		HttpPost request = new HttpPost(url);
		for (String key : this.headers.keySet()) {
			request.addHeader(key, this.headers.get(key));
		}
		request.setEntity(postData);
		HttpResponse response = client.execute(request);
		System.out.println("Http post request sent for authentication...");

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new HttpException(String.valueOf(statusCode));
		} else {
			System.out.println(statusCode + " - The request has succeeded! ");
		}

		// Read the contents of an entity and return it as a String.
		String content = EntityUtils.toString(response.getEntity());

		return (JSONObject) JSONValue.parse(content);
	}

}
