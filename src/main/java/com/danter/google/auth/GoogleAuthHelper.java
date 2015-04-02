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

//XML Pretty Printing Libraries
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

//JSON Pretty Printing Library
import org.codehaus.jackson.map.ObjectMapper;

//Date + Time Libraries
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A helper class for Google's OAuth2 authentication API.
 * @version 20130224
 * @author Matyas Danter
 */
public final class GoogleAuthHelper {

	/**
	 * Please provide a value for the CLIENT_ID constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_ID = "enter your client id here";
	/**
	 * Please provide a value for the CLIENT_SECRET constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_SECRET = "enter your client secret here";

	/**
	 * Callback URI that google will redirect to after successful authentication
	 */
    private static final String CALLBACK_URI = "http://localhost:8080/OAuth2v1/index.jsp";

    /* Contacts API Scopes:
     * https://www.google.com/m8/feeds read/write access to Contacts and Contact Groups
     * https://www.googleapis.com/auth/contacts.readonly read-only access to Contacts and Contact Groups
     */

	// start google authentication constants
	private static final Iterable<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email;https://mail.google.com/mail/feed/atom;https://www.googleapis.com/auth/drive.readonly;https://www.googleapis.com/auth/contacts.readonly;https://www.googleapis.com/auth/calendar.readonly;https://www.googleapis.com/auth/webmasters.readonly".split(";"));
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    // user's email url
    private static final String USER_INBOX_URL = "https://mail.google.com/mail/feed/atom";
    // user's drive url
    private static final String USER_DRIVE_URL = "https://www.googleapis.com/drive/v2";
    // user's calendar url
    private static final String USER_CALENDAR_URL = "https://www.googleapis.com/calendar/v3";
    private static final String USER_CONTACTS_URL = "https://www.google.com/m8/feeds/contacts";
    private static final String USER_WEBMASTER_URL = "https://www.googleapis.com/webmasters/v3";
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

    public String getWebmasterToolsSites() throws IOException{
		// Make an authenticated request
		//final GenericUrl url = new GenericUrl(USER_DRIVE_URL+"/about");
		final GenericUrl url = new GenericUrl(USER_WEBMASTER_URL+"/sites");
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


        //String temp = "";
        //URL obj = new URL(USER_CONTACTS_URL+"/default/full");
        //URLConnection conn = obj.openConnection();
        //BufferedReader in = new BufferedReader(
        //                        new InputStreamReader(
        //                        conn.getInputStream()));
        //String inputLine;
        //while ((inputLine = in.readLine()) != null)
        //    temp += inputLine;
        //in.close();

        //return temp;
        

		final GenericUrl url = new GenericUrl(USER_CONTACTS_URL+"/default/full/339c39c8c0dc79c");
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
		return jsonIdentity;
    
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

    @SuppressWarnings("unchecked")
    public String getCurrentWeekCalendarEvents(String calendarEvents) throws IOException{
        String currentDate = "";
        String endOfTheWeek = "";
        String startDate = "";

        JSONParser parser = new JSONParser();
        JSONArray array = new JSONArray();
        JSONArray result = new JSONArray();
        JSONObject json = new JSONObject();
        JSONObject startDateJsonObject = new JSONObject();
        Object obj = new Object();

        Calendar today = Calendar.getInstance();
        Calendar sunday = Calendar.getInstance();
        Calendar eventDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat sdfRFC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.add(Calendar.DAY_OF_WEEK,-(today.get(Calendar.DAY_OF_WEEK)-1));

        sunday.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        sunday.set(Calendar.HOUR_OF_DAY, 0);
        sunday.set(Calendar.MINUTE, 0);
        sunday.set(Calendar.SECOND, 0);
        sunday.add(Calendar.DATE,7);
        
        //Retrieving events Json Array from Json Object
        try {
            obj = parser.parse(calendarEvents);
        }catch(Exception e){
            return "GoogleAuthHelper.java-> line 282: "+e.getMessage();
        }
        json = (JSONObject)obj;
        array = (JSONArray)json.get("items");

        //Parsing through Json Array for events which fall in the scope of the
        //current week
        for(int i = 0;;++i)
        {
            try{
                json = (JSONObject)array.get(i);
            }catch (Exception e){
                break;
            }
            if ((startDateJsonObject = (JSONObject)json.get("start")) == null){
                continue;
            }
            if ((startDate = (String)startDateJsonObject.get("date")) == null){
                if ((startDate = (String)startDateJsonObject.get("dateTime")) == null){
                    continue;
                }
            }
            try{
                eventDate.setTime(sdf.parse(startDate));
            }catch(Exception e){
                return "GoogleAuthHelper.java-> line 307: "+e.getMessage();
            }
            if (eventDate.compareTo(today) == -1 || eventDate.compareTo(sunday) == 1){
                continue;
            }
            //Add current JSON event to result JSON Array
            result.add(json);
        }
        return prettyPrintJSON(result.toString());
    }

    public String prettyPrintJSON(String json){
        ObjectMapper mapper = new ObjectMapper();
        try{
            Object ppJson = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ppJson);
        }catch(Exception e){
            return "GoogleAuthHelper.java-> line 324: "+e.getMessage();
        }
    }

    public String prettyPrintXML(String xml){
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
