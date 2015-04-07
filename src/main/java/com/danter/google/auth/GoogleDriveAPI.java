package com.danter.google.auth;

import java.io.IOException;

public class GoogleDriveAPI{
    private static final String USER_DRIVE_URL = "https://www.googleapis.com/drive/v2";
    public GoogleAuthHelper helper;

    public GoogleDriveAPI() {
        helper = new GoogleAuthHelper();
    }
    
    public GoogleDriveAPI(GoogleAuthHelper GoogleAuthObj) {
        helper = GoogleAuthObj;
    }

    public String getFiles() throws IOException{
		//final GenericUrl url = new GenericUrl(USER_DRIVE_URL+"/about");
        return helper.accessAPI(USER_DRIVE_URL+"/files");
    }


}
