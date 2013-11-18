package com.vivant.roomee.json;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * This class manages HTTP GET and POST method to the Roomee RESTful services
 * Created by guangbo on 18/11/13.
 */
public class RoomeeRestClient {

    private final static String BASE_URL = "http://api.roomee.chrsptn.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * this method handles HTTP GET request
     * @param url, string url
     * @param params, HTTP request parameters
     * @param responseHandler, AsyncHttpResponseHandler
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * this method handles HTTP POST request
     * @param url, string url
     * @param params, HTTP request parameters
     * @param responseHandler, AsyncHttpResponseHandler
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * this method returns the absolute Url of the RESTful service
     * @param relativeUrl, String relative url
     * @return string, absolute url
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
