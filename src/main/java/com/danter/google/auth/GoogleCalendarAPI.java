package com.danter.google.auth;

import java.io.IOException;

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

public final class GoogleCalendarAPI{

    // user's calendar url
    private static final String USER_CALENDAR_URL = "https://www.googleapis.com/calendar/v3";
    public GoogleAuthHelper helper;

    public GoogleCalendarAPI() {
        helper = new GoogleAuthHelper();
    }
    
    public GoogleCalendarAPI(GoogleAuthHelper GoogleAuthObj) {
        helper = GoogleAuthObj;
    }

    public String getCalendarList() throws IOException{
        return helper.accessAPI(USER_CALENDAR_URL+"/users/me/calendarList");
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
        return helper.accessAPI(USER_CALENDAR_URL+"/calendars/"+id+"/events");
    }

    @SuppressWarnings("unchecked")
    public String getCurrentWeekCalendarEvents() throws IOException{
        String calendarEvents = getCalendarEvents();
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
            return "GoogleCalendar.java ERROR: "+e.getMessage();
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
                return "GoogleCalendar.java-> ERROR: "+e.getMessage();
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
            return "GoogleCalendar.java-> ERROR: "+e.getMessage();
        }
    }
}
