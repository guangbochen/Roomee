package com.vivant.roomee.json;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * This class manages the HTTP GET and POST methods
 * and parse the JSON data returned by the RESTful server
 * Created by guangbo on 21/10/13.
 */
public class JSONParserImpl implements JSONParser {

    private static InputStream is = null;
    private static JSONObject jObj = null;
    private String json = "";
    private final static String defaultUrl = "http://api.roomee.chrsptn.com/";

    /**
     * constructor to initialise variables
     */
    public JSONParserImpl() { }

    /**
     * this method manages HTTP GET request
     * @param url, String server URL
     * @return JSONObject, JSON object
     */
    public JSONObject getJSONFromUrl(String url) {
        //making HTTP request
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String Url = defaultUrl + url;
            HttpGet httpGet = new HttpGet(Url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseDataIntoJsonObject();
    }

    /**
     * this method manages HTTP POST request
     * @param url, String server URL
     * @param meetingData, POST Data
     * @return JSONObject, JSON object
     */
    @Override
    public JSONObject postJSONToUrl(String url, List<NameValuePair> meetingData) {
        //making HTTP request
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String Url = defaultUrl + url;
            HttpPost httpPost = new HttpPost(Url);
            httpPost.setEntity(new UrlEncodedFormEntity(meetingData));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseDataIntoJsonObject();
    }


    /**
     * this method parse the return json data into json object
     * @return JSONObject, JSON object
     */
    private JSONObject parseDataIntoJsonObject()
    {
        //using bufferReader to parse the data into String
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        }
        catch (Exception e) {
            Log.e("Buffer Error", "Error message " + e.toString());
            e.printStackTrace();
        }

        //parse the string to the json object
        try {
            jObj = new JSONObject(json);
        }
        catch (JSONException e) {
            Log.e("JSON Parser", "Error message " + e.toString());
        }
        return jObj;
    }


}
