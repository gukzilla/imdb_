package ru.gukzilla.imdb.api;

import android.os.Handler;
import android.os.Looper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class RequestClient {

    Handler handler;

    public RequestClient() {
        handler = new Handler(Looper.getMainLooper());
    }

    public interface Listener {
        void onResult(Result result);
    }

    public Result get(URL url) {
        Result result = new Result(url.toString(), 0, null);
        result.setMethod(RestMethod.GET);

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(RestMethod.GET.name());
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            result.setResponseCode(urlConnection.getResponseCode());

            try {
                StringBuilder sb = new StringBuilder();
                int data = isw.read();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    sb.append(current);
                }
                result.setResponseJson(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                result.setResponseJson(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }


    public void getAsync(final URL url, final Listener listener) {
        Threads.run(new Runnable() {
            @Override
            public void run() {
                final Result result = get(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(result);
                    }
                });
            }
        });
    }

}
