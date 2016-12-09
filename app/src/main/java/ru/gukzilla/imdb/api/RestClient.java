package ru.gukzilla.imdb.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class RestClient {

    Handler handler;
    private final LruCache<String, Bitmap> cache = new LruCache<>(5 * 1024 * 1024);

    public RestClient() {
        handler = new Handler(Looper.getMainLooper());
    }

    public interface Listener {
        void onResult(Result result);
    }

    public interface BitmapListener {
        void onResult(Bitmap bitmap);
    }

    public Result get(URL url) {
        Result result = new Result(url.toString(), 0, null);
        result.setMethod(RestMethod.GET);

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(RestMethod.GET.name());
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            InputStream in = connection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            result.setResponseCode(connection.getResponseCode());

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
            if (connection != null) {
                connection.disconnect();
            }
            result.print();
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

    public Bitmap getBitmapFromURL(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(RestMethod.GET.name());
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getBitmapFromURLAsync(final URL url, final BitmapListener bitmapListener) {
        final String urlStr = url.toString();
        Bitmap cachedBitmap = cache.get(urlStr);
        if(cachedBitmap != null) {
            bitmapListener.onResult(cachedBitmap);
        } else {
            Threads.run(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = getBitmapFromURL(url);
                    if(bitmap != null) {
                        cache.put(urlStr, bitmap);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bitmapListener.onResult(bitmap);
                        }
                    });
                }
            });
        }
    }
}
