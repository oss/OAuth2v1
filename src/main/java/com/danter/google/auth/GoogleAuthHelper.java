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

//Used for pretty printing xml
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

//Libraries for contacts
//  Used to get output as xml
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

//JSON Libraries
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * A helper class for Google's OAuth2 authentication API.
 * @version 20130224
 * @author Matyas Danter
 */
public final class GoogleAuthHelper {

	/**
	 * Please provide a value for the CLIENT_ID constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_ID = "375780499333-16898giq908uhqnjvmqf26g9lt06sa7d.apps.googleusercontent.com";
	/**
	 * Please provide a value for the CLIENT_SECRET constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_SECRET = "1FVlO_B4wOE52Gudhbq7rNlv";

	/**
	 * Callback URI that google will redirect to after successful authentication
	 */
    private static final String CALLBACK_URI = "http://localhost:8080/OAuth2v1/index.jsp";

    /* Contacts API Scopes:
     * https://www.google.com/m8/feeds read/write access to Contacts and Contact Groups
     * https://www.googleapis.com/auth/contacts.readonly read-only access to Contacts and Contact Groups
     */
	
	// start google authentication constants
	private static final Iterable<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email;https://mail.google.com/mail/feed/atom;https://www.googleapis.com/auth/drive.readonly;https://www.googleapis.com/auth/contacts.readonly;https://www.googleapis.com/auth/calendar.readonly".split(";"));
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    // user's email url
    private static final String USER_INBOX_URL = "https://mail.google.com/mail/feed/atom";
    // user's drive url
    private static final String USER_DRIVE_URL = "https://www.googleapis.com/drive/v2";
    // user's calendar url
    private static final String USER_CALENDAR_URL = "https://www.googleapis.com/calendar/v3";
    private static final String USER_CONTACTS_URL = "https://www.google.com/m8/feeds/contacts";
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	// end google authentication constants
	
	private String stateToken;
	
	private final GoogleAuthorizationCodeFlow flow;

    private HttpRequestFactory requestFactory;
	
	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE 
	 */
	public GoogleAuthHelper() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).build();
		
		generateStateToken();
	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope 
	 */
	public String buildLoginUrl() {
		
		final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		
		return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
	}
	
	/**
	 * Generates a secure state token 
	 */
	private void generateStateToken(){
		
		SecureRandom sr1 = new SecureRandom();
		
		stateToken = "google;"+sr1.nextInt();
		
	}
	
	/**
	 * Accessor for state token
	 */
	public String getStateToken(){
		return stateToken;
	}

    public void generateRequestFactory(final String authCode) throws IOException {
		final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
		final Credential credential = flow.createAndStoreCredential(response, null);
		requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
    }

	public String getUserInfo() throws IOException {
		// Make an authenticated request
		final GenericUrl url = new GenericUrl(USER_INFO_URL);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
        return jsonIdentity;
    }

    public String getUnreadEmails() throws IOException{
		// Make an authenticated request
		final GenericUrl url = new GenericUrl(USER_INBOX_URL);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
		return jsonIdentity;
    }
    
    public String getFiles() throws IOException{
		// Make an authenticated request
		//final GenericUrl url = new GenericUrl(USER_DRIVE_URL+"/about");
		final GenericUrl url = new GenericUrl(USER_DRIVE_URL+"/files");
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
		return jsonIdentity;
    }

    public String getContacts() throws IOException{
		// Make an authenticated request
		//final GenericUrl url = new GenericUrl(USER_DRIVE_URL+"/about");
        //HttpClient client = new DefaultHttpClient();
		//final GenericUrl url = new GenericUrl(USER_CONTACTS_URL+"/default/full");
		//final HttpRequest request = requestFactory.buildGetRequest(url);
		//request.getHeaders().setContentType("application/json");
	    ////final String jsonIdentity = request.execute().parseAsString();
		////return jsonIdentity;
        //HttpResponse response = client.execute(request);
        //HttpEntity entity = response.getEntity();
        //String content = EntityUtils.toString(entity);
        //return content;
        String temp = "";
        URL obj = new URL(USER_CONTACTS_URL+"/default/full");
        URLConnection conn = obj.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                conn.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) 
            temp += inputLine;
        in.close();
        
        return temp;
    }

    public String getCalendarList() throws IOException{
		// Make an authenticated request
		final GenericUrl url = new GenericUrl(USER_CALENDAR_URL+"/users/me/calendarList");
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
		return jsonIdentity;
    }
    
    public String getCalendarEvents() throws IOException{
        int i = 0;
        String id = "";
        JSONParser parser = new JSONParser();
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        Object obj = new Object();
        String calendardata = getCalendarList();
        try {
            obj = parser.parse(calendardata);
        } catch (Exception e){
            return e.getMessage();
        }
        json = (JSONObject)obj;
        array = (JSONArray)json.get("items");
        //This value 0 must be hardcoded -> 
        for(i = 0;;++i)
        {
            json = (JSONObject)array.get(i);
            try{
                if ((Boolean)json.get("primary"))
                {
                    break;
                }
            }catch (Exception e){
                return e.getMessage();
            }
        }
        json = (JSONObject)array.get(i);
        id = (String)json.get("id");
		final GenericUrl url = new GenericUrl(USER_CALENDAR_URL+"/calendars/"+id+"/events");
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
		return jsonIdentity;
    }

    public String formatXml(String xml){
         try{
             Transformer serializer= SAXTransformerFactory.newInstance().newTransformer();
             serializer.setOutputProperty(OutputKeys.INDENT, "yes");
             //serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
             serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
             //serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount", "2");
             Source xmlSource=new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
             StreamResult res =  new StreamResult(new ByteArrayOutputStream());                
             serializer.transform(xmlSource, res);
             return new String(((ByteArrayOutputStream)res.getOutputStream()).toByteArray());
        }catch(Exception e){ 
             //TODO log error
             return xml;
         }   
     }   

}
