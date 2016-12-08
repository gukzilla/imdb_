package ru.gukzilla.imdb.models;

import org.json.JSONObject;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class Film extends BaseObj {

    public Film(JSONObject json) {
        super(json);
    }

    public String getTitle() {
        return getStringOrEmpty(Const.Title);
    }

    public String getYear() {
        return getStringOrEmpty(Const.Year);
    }

    public String getImdbID() {
        return getStringOrEmpty(Const.imdbID);
    }

    public String getType() {
        return getStringOrEmpty(Const.Type);
    }

    public String getPoster() {
        return getStringOrNull(Const.Poster);
    }
}
