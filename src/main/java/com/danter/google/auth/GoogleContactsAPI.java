package com.danter.google.auth;

import java.io.IOException;
//Libraries for contacts
//  Used to get output as xml
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class GoogleContactsAPI{
    private static final String USER_CONTACTS_URL = "https://www.google.com/m8/feeds/contacts";
    /* Contacts API Scopes:
     * https://www.google.com/m8/feeds read/write access to Contacts and Contact Groups
     * https://www.googleapis.com/auth/contacts.readonly read-only access to Contacts and Contact Groups
     */
    public GoogleAuthHelper helper;

    public GoogleContactsAPI() {
        helper = new GoogleAuthHelper();
    }
    
    public GoogleContactsAPI(GoogleAuthHelper GoogleAuthObj) {
        helper = GoogleAuthObj;
    }

    /*
     * Under Construction:
     * The API does not output the contacts -> only the contact object ids
     */
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
        
        return helper.accessAPI(USER_CONTACTS_URL+"/default/full/339c39c8c0dc79c");
    
    }

}
