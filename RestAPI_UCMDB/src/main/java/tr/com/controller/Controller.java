package tr.com.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.apache.http.HttpException;
import org.json.simple.JSONObject;

import tr.com.rest.RestClient;

public class Controller {

	static String UCMDB_BASE_URL = "https://192.168.x.x:port/rest-api";
	static boolean isSSL = false;
	static String UCMDB_USER = "sysadmin";
	static String UCMDB_PASS = "sysadmin.";

	public static void main(String Args[]) {
		RestClient client;

		try {
			client = new RestClient(UCMDB_BASE_URL, isSSL);
		} catch (NoSuchAlgorithmException e) {
			String message = "Disabling SSL verification failed.";
			String title = "Error";
			System.out.println(message);
			return;
		} catch (KeyStoreException e) {
			String message = "Disabling SSL verification failed.";
			System.out.println(message);
			return;
		} catch (KeyManagementException e) {
			String message = "Disabling SSL verification failed.";
			System.out.println(message);
			return;
		}
		System.out.println("Authenticating at " + UCMDB_BASE_URL + " with user " + UCMDB_USER);

		try {
			client.authenticate(UCMDB_USER, UCMDB_PASS);

		} catch (IOException e) {
			String message = "Connection Error: Target server not available";
			System.out.println(message);
			return;
		} catch (HttpException e) {
			int statusCode = Integer.parseInt(e.getMessage());
			System.out.println(statusCode);
			return;
		}
		Scanner sc= new Scanner(System.in);
		System.out.println("Enter a CI Id: ");
		String ciId= sc.nextLine();
		System.out.println("Get CI from UCMDB with " + ciId + "...");
		
		JSONObject tqlResult;
		try {
			tqlResult = client.getCI(ciId);
		} catch (IOException e) {
			String message = "Connection Error: Target server not available";
			System.out.println(message);
			return;
		} catch (HttpException e) {
			int statusCode = Integer.parseInt(e.getMessage());
			System.out.println(statusCode);
			return;
		}
		System.out.print(tqlResult);

	}
}
