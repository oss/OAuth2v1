OAuth2v1
========

Prerequisites

- Google API access credentials (Client ID, Client Secret). Set it up here https://code.google.com/apis/console/
- Set up allowed Redirect URIs at Google API -> API Access. Input: http://localhost:8080/OAuth2v1/index.jsp

Requires:
    openjdk-7
    maven  >= 3.2.5
    tomcat >= 7.054

Installation Instructions:

1. git clone the project to your local machine (https://github.com/oss/OAuth2v1)
2. install maven and tomcat on your machine
3. copy the contents of the conf files in the conf-files directory in the git project to the following locations:
        settings.xml: /etc/maven/settings.xml
        tomcat-users.xml: /usr/share/tomcat/conf/tomcat-users.xml
4. Add Client ID, and Client Secret parameters to GoogleAuthHelper.java
5. inside the project, run the following command:
        mvn clean compile tomcat:run 
6. Browse to: http://localhost:8080/OAuth2v1/ 
   Click "log in with google" on top of this page

