package com.vivant.roomee.json;

import org.json.JSONObject;

/**
 * Created by guangbo on 24/10/13.
 */
public interface JSONParser {
    public JSONObject getJSONFromUrl(String url);
    public JSONObject postJSONToUrl(String url);
}
