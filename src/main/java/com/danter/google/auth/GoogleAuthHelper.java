package com.danter.google.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * A helper class for Google's OAuth2 authentication API.
 * @author Renish Matta
 */
public final class GoogleAuthHelper {

	/*
     * Provide a value for the CLIENT_ID and CLIENT_SECRET constant before proceeding, set this up at https://code.google.com/apis/console/
     * */
	private static final String CLIENT_ID = "";
	private static final String CLIENT_SECRET = "";

	/* Callback URI that google will redirect to after successful authentication */
    private static final String CALLBACK_URI = "http://localhost:8080/OAuth2v1/index.jsp";
	private static final Iterable<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email;https://mail.google.com/mail/feed/atom;https://www.googleapis.com/auth/drive.readonly;https://www.googleapis.com/auth/contacts.readonly;https://www.googleapis.com/auth/calendar.readonly;https://www.googleapis.com/auth/webmasters.readonly".split(";"));
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private String stateToken;
	private final GoogleAuthorizationCodeFlow flow;
    HttpRequestFactory requestFactory;

	/* Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE */
    public GoogleAuthHelper() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).build();
		generateStateToken();
	}

	/* Builds a login URL based on client ID, secret, callback URI, and scope */
	public String buildLoginUrl() {
		final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
	}

	/* Generates a secure state token */
	private void generateStateToken(){
		SecureRandom sr1 = new SecureRandom();
		stateToken = "google;"+sr1.nextInt();
	}

	/* Accessor for state token */
	public String getStateToken(){
		return stateToken;
	}

    public void generateRequestFactory(final String authCode) throws IOException {
		final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
		final Credential credential = flow.createAndStoreCredential(response, null);
		requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
    }

    public String accessAPI(String APIurl) throws IOException {
		// Make an authenticated request
		final GenericUrl url = new GenericUrl(APIurl);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
        return jsonIdentity;

    }
}
