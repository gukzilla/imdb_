package ru.gukzilla.imdb.store;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.gukzilla.imdb.models.Const;
import ru.gukzilla.imdb.models.FullVideo;
import ru.gukzilla.imdb.models.Video;

/**
 * Created by evgeniy on 09.12.16.
 */

public class CacheStorage {

    private String TAG = getClass().getSimpleName();
    private SharedPreferences sharedPreferences;

    public CacheStorage(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public void saveVideoList(List<Video> videoArrayList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray array = new JSONArray();
        for(Video video : videoArrayList) {
            array.put(video.getJson());
        }
        editor.putString(Const.array, array.toString());
        editor.apply();
    }

    public List<Video> getSavedVideoList() {
        List<Video> list = new ArrayList<>();
        String arrStr = sharedPreferences.getString(Const.array, "[]");

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(arrStr);
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            Video video = new Video(jsonObject);
            list.add(video);
        }

        return list;
    }

    public void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }
}
