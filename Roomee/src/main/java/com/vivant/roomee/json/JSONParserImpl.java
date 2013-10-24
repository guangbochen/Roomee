package com.vivant.roomee.json;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

/**
 * Created by guangbo on 21/10/13.
 */
public class JSONParserImpl implements JSONParser {

    private static InputStream is = null;
    private static JSONObject jObj = null;
    private String json = "";
    private final static String defaultUrl = "http://api.roomee.chrsptn.com/";


    public JSONParserImpl() {

    }

    public JSONObject getJSONFromUrl(String url) {
        //making HTTP request
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
//            String Url = defaultUrl + url;
            String Url = url;
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

    @Override
    public JSONObject postJSONToUrl(String url) {
        //making HTTP request
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String Url = defaultUrl + url;
            HttpPost httpPost = new HttpPost(Url);
            Log.d("MAD", Url);
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
