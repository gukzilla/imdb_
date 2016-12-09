package ru.gukzilla.imdb.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class Result {
    private RestMethod requestMethod;
    private String requestUrl;
    private String requestJson;

    private int responseCode;
    private String responseJson;

    public Result(String requestUrl, int responseCode, String requestJson) {
        this.requestUrl = requestUrl;
        this.responseCode = responseCode;
        this.requestJson = requestJson;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int code() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public RestMethod getMethod() {
        return requestMethod;
    }

    public void setMethod(RestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public JSONObject getJSONObject() {
        try {
            return new JSONObject(getResponseJson());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray getJSONArray() {
        try {
            return new JSONArray(getResponseJson());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void print() {
        // request
        StringBuilder sb = new StringBuilder();
        sb.append("REQ METHOD: ").append(requestMethod.name()).append("\n");
        sb.append("REQ URL: ").append(getRequestUrl() == null ? "null" : getRequestUrl()).append("\n");
        sb.append("REQ JSON: ").append(getRequestJson() == null ? "null" : getRequestJson()).append("\n\n\n");

        // response
        sb.append("RESP CODE: ").append(Integer.toString(code())).append("\n");
        sb.append("RESPONSE JSON: ").append(getResponseJson() == null ? "null" : getResponseJson());

        Log.i("RESULT: ", sb.toString());
    }
}
