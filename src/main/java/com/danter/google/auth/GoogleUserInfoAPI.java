package com.danter.google.auth;

import java.io.IOException;

public class GoogleUserInfoAPI{
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    public GoogleAuthHelper helper;

    public GoogleUserInfoAPI() {
        helper = new GoogleAuthHelper();
    }
    
    public GoogleUserInfoAPI(GoogleAuthHelper GoogleAuthObj) {
        helper = GoogleAuthObj;
    }

	public String getUserInfo() throws IOException {
        return helper.accessAPI(USER_INFO_URL);
    }
}
