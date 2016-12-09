package ru.gukzilla.imdb.store;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.gukzilla.imdb.models.FullVideo;

/**
 * Created by evgeniy on 09.12.16.
 */

public class BookMarksStorage {

    private String TAG = getClass().getSimpleName();
    private SharedPreferences sharedPreferences;

    public BookMarksStorage(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public void save(FullVideo video) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(video.getImdbID(), video.toString());
        editor.apply();
    }

    public boolean inTheBookMarks(String imdbID) {
        return sharedPreferences.getString(imdbID, null) != null;
    }

    public void remove(FullVideo video) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(video.getImdbID());
        editor.apply();
    }

    public List<FullVideo> getAllBookmarks() {
        List<FullVideo> list = new ArrayList<>();
        Map<String, ?> map = sharedPreferences.getAll();
        for(Map.Entry<String, ?> entry : map.entrySet()) {
            String jsonString = (String) entry.getValue();
            JSONObject json;
            try {
                json = new JSONObject(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            FullVideo fullVideo = new FullVideo(json);
            list.add(fullVideo);
        }
        return list;
    }
}
