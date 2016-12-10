package ru.gukzilla.imdb.api;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.gukzilla.imdb.models.Const;
import ru.gukzilla.imdb.models.FullVideo;
import ru.gukzilla.imdb.models.Video;

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
        void onResult(List<Video> videoLists);
        void onError(Exception e);
        void onComplete();
    }

    public interface VideoListener {
        void onResult(FullVideo video);
        void onError(Exception e);
        void onComplete();
    }

    public void searchAsync(String text, final SearchListener listener) {
        final List<Video> videoLists = new ArrayList<>();

        if(text == null || text.isEmpty()) {
            listener.onResult(videoLists);
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
                    videoLists.add(new Video(filmJs));
                }

                listener.onResult(videoLists);
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


    public void getFullVideoById(final String id, final VideoListener videoListener) {
        Uri builtUri = Uri.parse(url)
                .buildUpon()
                .appendQueryParameter(Const.i, id)
                .appendQueryParameter(Const.plot, Const.full)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
            videoListener.onError(e);
        }

        restClient.getAsync(url, new RestClient.Listener() {
            @Override
            public void onResult(Result result) {
                if(result.code() != 200 || result.getResponseJson() == null) {
                    videoListener.onError(null);
                    return;
                }

                JSONObject videoJs = result.getJSONObject();
                if(videoJs == null) {
                    videoListener.onError(null);
                    return;
                }

                FullVideo video = new FullVideo(videoJs);

                videoListener.onResult(video);
                videoListener.onComplete();
            }
        });
    }
}
