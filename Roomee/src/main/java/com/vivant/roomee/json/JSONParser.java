package com.vivant.roomee.json;

import org.apache.http.NameValuePair;
import org.json.JSONObject;
import java.util.List;

/**
 * Created by guangbo on 24/10/13.
 */
public interface JSONParser {
    public JSONObject getJSONFromUrl(String url);
    public JSONObject postJSONToUrl(String url, List<NameValuePair> meetingData);
}
