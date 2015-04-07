package com.danter.google.auth;

import java.io.IOException;

public class GoogleAtomFeedAPI{

    public GoogleAuthHelper helper;
    private static final String USER_INBOX_URL = "https://mail.google.com/mail/feed/atom";

    public GoogleAtomFeedAPI() {
        helper = new GoogleAuthHelper();
    }
    
    public GoogleAtomFeedAPI(GoogleAuthHelper GoogleAuthObj) {
        helper = GoogleAuthObj;
    }

    public String getUnreadEmails() throws IOException{
        return helper.accessAPI(USER_INBOX_URL);
    }
}
