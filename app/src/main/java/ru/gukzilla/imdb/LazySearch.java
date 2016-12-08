package ru.gukzilla.imdb;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class LazySearch {

    Timer requestTimer;
    TimerTask requestTask;
    AtomicLong timeCount = new AtomicLong(0);
    String lastSearch = "";


    public interface SearchCallBack {
        void onFinished(String lastText);
    }

    public String getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(String search) {
        lastSearch = search;
    }


    public void search(final String text, final SearchCallBack endCallback) {
        timeCount.set(0);
        setLastSearch(text);

        if (requestTask == null) {
            requestTask = new TimerTask() {
                @Override
                public void run() {
                    timeCount.addAndGet(100);

                    if (timeCount.get() >= 500L) {
                        endCallback.onFinished(getLastSearch());

                        requestTask.cancel();
                        requestTask = null;

                        requestTimer.cancel();
                        requestTimer = null;
                    }
                }
            };
        }

        if (requestTimer == null) {
            requestTimer = new Timer();
            requestTimer.scheduleAtFixedRate(requestTask, 0, 100);
        }
    }

}
