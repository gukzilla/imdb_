package ru.gukzilla.imdb.models;

import org.json.JSONObject;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class FullVideo extends Video {

    public FullVideo(JSONObject json) {
        super(json);
    }

    public String getRuntime() {
        return getStringOrEmpty(Const.Runtime);
    }

    public String getGenre() {
        return getStringOrEmpty(Const.Genre);
    }

    public String getReleased() {
        return getStringOrEmpty(Const.Released);
    }

    public String getCountry() {
        return getStringOrEmpty(Const.Country);
    }

    public String getImdbRating() {
        return getStringOrEmpty(Const.imdbRating);
    }

    public String getDirector() {
        return getStringOrEmpty(Const.Director);
    }

    public String getWriter() {
        return getStringOrEmpty(Const.Writer);
    }

    public String getPlot() {
        return getStringOrEmpty(Const.Plot);
    }


}
