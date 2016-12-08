package ru.gukzilla.imdb.api;

import android.net.Uri;
import android.webkit.URLUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.gukzilla.imdb.models.Const;
import ru.gukzilla.imdb.models.Film;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class Api {

    private RequestClient requestClient;
    private final String url = "http://www.omdbapi.com/";

    public Api(){
        requestClient = new RequestClient();
    }

    public interface SearchListener {
        void onResult(List<Film> films);
    }

    public void searchAsync(String text, final SearchListener listener) {
        if(text == null || text.isEmpty()) {
            return;
        }

        Uri builtUri = Uri.parse(url)
                .buildUpon()
                .appendQueryParameter(Const.s, text)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final List<Film> films = new ArrayList<>();
        requestClient.getAsync(url, new RequestClient.Listener() {
            @Override
            public void onResult(Result result) {
                if(result.code() != 200 || result.getResponseJson() == null) {
                    listener.onResult(films);
                    return;
                }

                JSONObject search = result.getJSONObject();
                JSONArray resultsArr = search.optJSONArray(Const.Search);

                if(resultsArr == null) {
                    listener.onResult(films);
                    return;
                }

                for(int i = 0; i < resultsArr.length(); i++) {
                    JSONObject filmJs = resultsArr.optJSONObject(i);
                    films.add(new Film(filmJs));
                }
                listener.onResult(films);
            }
        });
    }

}
