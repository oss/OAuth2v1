package com.danter.google.auth;

import java.io.IOException;

public class GoogleWebmasterAPI{
    private static final String USER_WEBMASTER_URL = "https://www.googleapis.com/webmasters/v3";
    public GoogleAuthHelper helper;

    public GoogleWebmasterAPI() {
        helper = new GoogleAuthHelper();
    }
    
    public GoogleWebmasterAPI(GoogleAuthHelper GoogleAuthObj) {
        helper = GoogleAuthObj;
    }

    public String getWebmasterToolsSites() throws IOException{
        return helper.accessAPI(USER_WEBMASTER_URL+"/sites");
    }
}
