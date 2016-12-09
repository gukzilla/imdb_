package ru.gukzilla.imdb.api;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.gukzilla.imdb.models.Const;
import ru.gukzilla.imdb.models.Film;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class Api {

    private RestClient restClient;
    private final String url = "http://www.omdbapi.com/";

    public Api(){
        restClient = new RestClient();
    }

    public interface SearchListener {
        void onResult(List<Film> films);
        void onError(Exception e);
        void onComplete();
    }

    public void searchAsync(String text, final SearchListener listener) {
        if(text == null || text.isEmpty()) {
            listener.onComplete();
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
            listener.onError(e);
        }

        final List<Film> films = new ArrayList<>();
        restClient.getAsync(url, new RestClient.Listener() {
            @Override
            public void onResult(Result result) {
                if(result.code() != 200 || result.getResponseJson() == null) {
                    listener.onError(null);
                    return;
                }

                JSONObject search = result.getJSONObject();
                JSONArray resultsArr = search.optJSONArray(Const.Search);

                if(resultsArr == null) {
                    listener.onError(null);
                    return;
                }

                for(int i = 0; i < resultsArr.length(); i++) {
                    JSONObject filmJs = resultsArr.optJSONObject(i);
                    films.add(new Film(filmJs));
                }

                listener.onResult(films);
                listener.onComplete();
            }
        });
    }

    public void downloadBitmap(String url, RestClient.BitmapListener bitmapListener) {
        try {
            restClient.getBitmapFromURLAsync(new URL(url), bitmapListener);
        } catch (Exception e) {
            e.printStackTrace();
            bitmapListener.onResult(null);
        }
    }

}
