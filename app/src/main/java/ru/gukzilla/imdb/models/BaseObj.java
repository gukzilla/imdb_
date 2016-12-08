package ru.gukzilla.imdb.models;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class BaseObj {

    JSONObject json = new JSONObject();

    private BaseObj(){};

    public BaseObj(JSONObject json) {
        this.json = json;
    }

    public JSONObject getJson() {
        if(json == null) {
            json = new JSONObject();
        }

        return json;
    }

    public String getStringOrNull(String key) {
        if(getJson().isNull(key) || getJson().optString(key).isEmpty()) return null;

        return getJson().optString(key);
    }

    public String getStringOrEmpty(String key) {
        if(getJson().isNull(key)) {
            return "";
        }

        String value = getJson().optString(key, "").trim();
        if(value.isEmpty()) {
            return "";
        }

        return value;
    }

    public JSONObject getJSONObjectFromJson(String key) {
        return getJson().optJSONObject(key);
    }

    public JSONArray getJSONArrayFromJson(String key) {
        return getJson().optJSONArray(key);
    }

    @Override
    public String toString() {
        return getJson().toString();
    }
}
